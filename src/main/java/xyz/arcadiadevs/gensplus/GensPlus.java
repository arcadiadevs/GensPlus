package xyz.arcadiadevs.gensplus;

import com.awaitquality.api.spigot.chat.ChatUtil;
import com.cryptomorin.xseries.XMaterial;
import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.HologramPool;
import com.github.unldenis.hologram.IHologramPool;
import com.github.unldenis.hologram.placeholder.Placeholders;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.arcadiadevs.gensplus.commands.Commands;
import xyz.arcadiadevs.gensplus.commands.CommandsTabCompletion;
import xyz.arcadiadevs.gensplus.events.BlockBreak;
import xyz.arcadiadevs.gensplus.events.BlockInteraction;
import xyz.arcadiadevs.gensplus.events.BlockPlace;
import xyz.arcadiadevs.gensplus.events.CraftItem;
import xyz.arcadiadevs.gensplus.events.EntityExplode;
import xyz.arcadiadevs.gensplus.events.InstantBreak;
import xyz.arcadiadevs.gensplus.events.OnInventoryOpen;
import xyz.arcadiadevs.gensplus.events.OnJoin;
import xyz.arcadiadevs.gensplus.events.OnWandUse;
import xyz.arcadiadevs.gensplus.events.SmeltItem;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.models.PlayerData;
import xyz.arcadiadevs.gensplus.models.WandData;
import xyz.arcadiadevs.gensplus.models.events.DropEvent;
import xyz.arcadiadevs.gensplus.models.events.Event;
import xyz.arcadiadevs.gensplus.models.events.SellEvent;
import xyz.arcadiadevs.gensplus.models.events.SpeedEvent;
import xyz.arcadiadevs.gensplus.placeholders.PlaceHolder;
import xyz.arcadiadevs.gensplus.tasks.CleanupTask;
import xyz.arcadiadevs.gensplus.tasks.DataSaveTask;
import xyz.arcadiadevs.gensplus.tasks.EventLoop;
import xyz.arcadiadevs.gensplus.tasks.SpawnerTask;
import xyz.arcadiadevs.gensplus.utils.HologramsUtil;
import xyz.arcadiadevs.gensplus.utils.ItemUtil;
import xyz.arcadiadevs.gensplus.utils.Metrics;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;

/**
 * The main plugin class for GensPlus.
 */
public final class GensPlus extends JavaPlugin {

  /**
   * Gets the instance of the GensPlus plugin.
   */
  @Getter
  public static GensPlus instance;

  /**
   * Gets the hologram pool instance for hologram management.
   *
   * @implNote Null if holograms are disabled.
   */
  @Getter
  private IHologramPool hologramPool;

  /**
   * Gets placeholders instance.
   *
   * @implNote Null if holograms are disabled.
   */
  @Getter
  private Placeholders placeholders;

  /**
   * Gets the Gson instance used for JSON serialization/deserialization.
   */
  @Getter
  private Gson gson;

  /**
   * Gets the data handler for locations.
   */
  @Getter
  private LocationsData locationsData;

  /**
   * Gets the data handler for wands.
   */
  @Getter
  private WandData wandData;

  /**
   * Gets the data handler for player data.
   */
  @Getter
  private PlayerData playerData;

  /**
   * Gets the data handler for generators.
   */
  @Getter
  private GeneratorsData generatorsData;

  /**
   * Gets the economy plugin instance.
   */
  @Getter
  private Economy econ = null;

  /**
   * Gets the list of events.
   */
  @Getter
  private List<Event> events;

  /**
   * Gets the data save task.
   */
  private DataSaveTask dataSaveTask;

  @Override
  public void onEnable() {
    instance = this;

    saveDefaultConfig();

    moveBlockData();

    saveResourceIfNotExists("data/block_data.json", false);
    saveResourceIfNotExists("data/wands_data.json", false);
    saveResourceIfNotExists("data/player_data.json", false);
    saveResourceIfNotExists("messages.yml", false);

    setupEconomy();

    Messages.init();

    gson = new GsonBuilder().registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
        .setPrettyPrinting()
        .create();

    generatorsData = loadGeneratorsData();

    locationsData = new LocationsData(loadBlockDataFromJson());

    playerData = new PlayerData(loadPlayerDataFromJson());

    wandData = new WandData(loadWandsDataFromJson());

    events = loadGensPlusEvents();

    new Metrics(this, 19293);

    if (getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null) {
      new PlaceHolder(locationsData, playerData, getConfig()).register();
    }

    loadHolograms();

    // Register events
    loadBukkitEvents();

    // Register tasks
    registerTasks();

    // Register commands
    registerCommands();

    // Register tab completion
    registerTabCompletion();

    // Load player data in case the plugin gets enabled manually after server start
    loadPlayers();
  }

  @Override
  public void onDisable() {
    dataSaveTask.saveBlockDataToJson();
    dataSaveTask.saveWandDataToJson();
    dataSaveTask.savePlayerDataToJson();

    if (getConfig().getBoolean(Config.DEVELOPER_OPTIONS.getPath())) {
      // Remove all files
      new File(getDataFolder(), "data/block_data.json").delete();
    }
  }

  private void registerCommands() {
    getCommand("gensplus").setExecutor(
        new Commands(generatorsData, playerData, events));
    getCommand("generators").setExecutor(
        new Commands(generatorsData, playerData, events));
    getCommand("selldrops").setExecutor(
        new Commands(generatorsData, playerData, events));
  }

  private void registerTabCompletion() {
    getCommand("gensplus").setTabCompleter(new CommandsTabCompletion(generatorsData));
    getCommand("selldrops").setTabCompleter(new CommandsTabCompletion(generatorsData));
  }

  private void loadBukkitEvents() {
    final HashSet<Listener> events = new HashSet<>();

    events.add(new BlockPlace(locationsData, playerData, getConfig()));
    events.add(new BlockBreak(locationsData, generatorsData));
    events.add(new BlockInteraction(locationsData, getConfig()));
    events.add(new InstantBreak(locationsData, generatorsData));
    events.add(new OnJoin(generatorsData, playerData, getConfig()));
    events.add(new EntityExplode(locationsData, generatorsData));
    events.add(new OnWandUse(wandData, getConfig()));
    events.add(new OnInventoryOpen());
    events.add(new CraftItem());
    events.add(new SmeltItem());

    events.forEach(event -> Bukkit.getPluginManager().registerEvents(event, this));
  }

  private void registerTasks() {
    // Run block data save task every 5 minutes
    dataSaveTask = new DataSaveTask(this);

    dataSaveTask.runTaskTimerAsynchronously(this, 0, 20);

    // Run spawner task every second
    new SpawnerTask(locationsData.locations(), generatorsData)
        .runTaskTimerAsynchronously(this, 0, 20);

    // Start event loop
    new EventLoop(events).runTaskTimerAsynchronously(this, 0, 20);

    new CleanupTask(locationsData).runTaskTimerAsynchronously(this, 0, 20);
  }

  /**
   * Sets up the economy plugin for handling currency.
   *
   * @throws RuntimeException if Vault or an economy plugin is not found.
   */
  private void setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      throw new RuntimeException("Vault not found");
    }

    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
        .getRegistration(Economy.class);

    if (rsp == null) {
      throw new RuntimeException(
          "No economy plugin found. Please install one, for example EssentialsX.");
    }

    econ = rsp.getProvider();
  }

  /**
   * Loads the list of events based on the plugin configuration.
   *
   * @return The list of events.
   */
  private ArrayList<Event> loadGensPlusEvents() {
    ArrayList<Event> events = new ArrayList<>();
    if (getConfig().getBoolean(Config.EVENTS_DROP_EVENT_ENABLED.getPath())) {
      events.add(
          new DropEvent(getConfig().getLong(Config.EVENTS_DROP_EVENT_MULTIPLIER.getPath()),
              getConfig().getString(Config.EVENTS_DROP_EVENT_NAME.getPath())));
    }

    if (getConfig().getBoolean(Config.EVENTS_SPEED_EVENT_ENABLED.getPath())) {
      events.add(
          new SpeedEvent(getConfig().getLong(Config.EVENTS_SPEED_EVENT_MULTIPLIER.getPath()),
              getConfig().getString(Config.EVENTS_SPEED_EVENT_NAME.getPath())));
    }

    if (getConfig().getBoolean(Config.EVENTS_SELL_EVENT_ENABLED.getPath())) {
      events.add(
          new SellEvent(getConfig().getLong(Config.EVENTS_SELL_EVENT_MULTIPLIER.getPath()),
              getConfig().getString(Config.EVENTS_SELL_EVENT_NAME.getPath())));
    }

    return events;
  }

  /**
   * Loads the generators data from the plugin configuration.
   *
   * @return The generators data.
   * @throws RuntimeException if duplicate tier is found or an invalid item name or item meta is
   *                          encountered.
   */
  private GeneratorsData loadGeneratorsData() {
    List<GeneratorsData.Generator> generators = new ArrayList<>();
    List<Map<?, ?>> generatorsConfig = getConfig().getMapList(Config.GENERATORS.getPath());

    for (Map<?, ?> generator : generatorsConfig) {
      final String name = (String) generator.get("name");
      final String dropDisplayName = (String) generator.get("dropDisplayName");
      final boolean instantBreak = (boolean) generator.get("instantBreak");
      int tier = (int) generator.get("tier");
      int speed = (int) generator.get("speed");
      double price = (double) generator.get("price");
      double sellPrice = (double) generator.get("sellPrice");
      String spawnItem = (String) generator.get("spawnItem");
      String blockType = (String) generator.get("blockType");
      List<String> lore =
          ((List<String>) generator.get("lore")).isEmpty() ? getConfig().getStringList(
              Config.DEFAULT_LORE.getPath()) : (List<String>) generator.get("lore");

      lore = lore.stream().map(s -> s.replace("%tier%", String.valueOf(tier)))
          .map(s -> s.replace("%speed%", String.valueOf(speed)))
          .map(s -> s.replace("%price%", String.valueOf(price)))
          .map(s -> s.replace("%sellPrice%", String.valueOf(sellPrice)))
          .map(s -> s.replace("%spawnItem%", spawnItem))
          .map(s -> s.replace("%blockType%", blockType))
          .map(ChatUtil::translate).toList();

      if (generators.stream().anyMatch(g -> g.tier() == tier)) {
        throw new RuntimeException("Duplicate tier found: " + tier);
      }

      ItemStack spawnItemStack = ItemUtil.getUniversalItem(spawnItem, true);
      ItemStack blockTypeStack = ItemUtil.getUniversalItem(blockType, false);

      if (spawnItemStack == null || blockTypeStack == null) {
        throw new RuntimeException("Invalid item name");
      }

      ItemMeta blockTypeMeta = blockTypeStack.getItemMeta();
      ItemMeta spawnItemMeta = spawnItemStack.getItemMeta();

      if (blockTypeMeta == null || spawnItemMeta == null) {
        throw new RuntimeException("Invalid item meta");
      }

      blockTypeMeta.setDisplayName(ChatUtil.translate(name));
      blockTypeMeta.setLore(lore);

      blockTypeStack.setItemMeta(blockTypeMeta);

      List<String> itemSpawnLore = ((List<String>) generator.get("itemSpawnLore")).isEmpty()
          ? Config.DEFAULT_ITEM_SPAWN_LORE.getStringList()
          : (List<String>) generator.get("itemSpawnLore");

      String formattedSellPrice = econ.format(sellPrice);

      itemSpawnLore = itemSpawnLore.stream().map(s -> s.replace("%tier%", String.valueOf(tier)))
          .map(s -> s.replace("%sellPrice%", formattedSellPrice))
          .map(ChatUtil::translate)
          .toList();

      spawnItemMeta.setDisplayName(ChatUtil.translate(dropDisplayName));
      spawnItemMeta.setLore(itemSpawnLore);

      spawnItemStack.setItemMeta(spawnItemMeta);

      spawnItemStack = NBTEditor.set(spawnItemStack, tier, "gensplus", "spawnitem", "tier");
      blockTypeStack = NBTEditor.set(blockTypeStack, tier, "gensplus", "blocktype", "tier");

      generators.add(new GeneratorsData.Generator(
          name,
          tier,
          price,
          sellPrice,
          speed,
          spawnItemStack,
          blockTypeStack,
          lore,
          instantBreak
      ));
    }

    return new GeneratorsData(generators);
  }

  @SuppressWarnings("unchecked")
  private void loadHolograms() {
    hologramPool = hologramPool == null
        ? new HologramPool(this,
        getConfig().getInt(Config.HOLOGRAMS_VIEW_DISTANCE.getPath(), 2000))
        : hologramPool;

    placeholders = new Placeholders();

    if (!GensPlus.getInstance().getConfig().getBoolean(Config.HOLOGRAMS_ENABLED.getPath())) {
      return;
    }

    List<Map<?, ?>> generatorsConfig = instance.getConfig().getMapList("generators");

    for (LocationsData.GeneratorLocation location : getLocationsData().locations()) {
      GeneratorsData.Generator generator = generatorsData.getGenerator(location.getGenerator());

      Material material = XMaterial.matchXMaterial(generator.blockType().getType().toString())
          .orElseThrow(() -> new RuntimeException("Invalid item stack"))
          .parseItem()
          .getType();

      Map<?, ?> matchingGeneratorConfig = generatorsConfig.stream()
          .filter(generatorConfig -> generatorConfig.get("name").equals(generator.name()))
          .findFirst()
          .orElse(null);

      if (matchingGeneratorConfig == null) {
        continue;
      }

      List<String> lines = ((List<String>) matchingGeneratorConfig.get("hologramLines")).isEmpty()
          ? GensPlus.getInstance().getConfig()
          .getStringList(Config.DEFAULT_HOLOGRAM_LINES.getPath())
          : (List<String>) matchingGeneratorConfig.get("hologramLines");

      lines = lines
          .stream()
          .map(line -> line.replace("%name%", generator.name()))
          .map(line -> line.replace("%tier%", String.valueOf(generator.tier())))
          .map(line -> line.replace("%speed%", String.valueOf(generator.speed())))
          .map(line -> line.replace("%spawnItem%", generator.spawnItem().getType().toString()))
          .map(line -> line.replace("%sellPrice%", String.valueOf(generator.sellPrice())))
          .map(ChatUtil::translate)
          .toList();

      Location center = location.getCenter();
      Hologram hologram = HologramsUtil.createHologram(center, lines, material);
      location.setHologram(hologram);
      hologramPool.takeCareOf(hologram);
    }
  }

  /**
   * Load player data in case the plugin gets enabled manually after server start.
   */
  private void loadPlayers() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (playerData.getData(player.getUniqueId()) == null) {
        playerData.create(player.getUniqueId(), Config.LIMIT_PER_PLAYER_DEFAULT_LIMIT.getInt());
      }
    }
  }

  private CopyOnWriteArrayList<LocationsData.GeneratorLocation> loadBlockDataFromJson() {
    try (FileReader reader = new FileReader(getDataFolder() + "/data/block_data.json")) {
      return gson.fromJson(reader,
          new TypeToken<CopyOnWriteArrayList<LocationsData.GeneratorLocation>>() {
          }.getType());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private List<WandData.Wand> loadWandsDataFromJson() {
    try (FileReader reader = new FileReader(getDataFolder() + "/data/wands_data.json")) {
      return gson.fromJson(reader,
          new TypeToken<List<WandData.Wand>>() {
          }.getType());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private List<PlayerData.Data> loadPlayerDataFromJson() {
    try (FileReader reader = new FileReader(getDataFolder() + "/data/player_data.json")) {
      return gson.fromJson(reader,
          new TypeToken<List<PlayerData.Data>>() {
          }.getType());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Create a function that will move block_data.json to /data/ if the block_data.json exists
  public void moveBlockData() {
    // Check if "block_data.json" exists in the plugin's data folder
    File blockDataFile = new File(getDataFolder(), "/block_data.json");
    if (blockDataFile.exists()) {
      // Create the "/data/" directory if it doesn't exist
      File targetDirectory = new File(getDataFolder(), "data");
      if (!targetDirectory.exists()) {
        targetDirectory.mkdirs();
      }

      try {
        // Move the file to the "/data/" directory
        Path sourcePath = Paths.get(blockDataFile.toURI());
        Path targetPath = Paths.get(getDataFolder().getPath(), "/data/block_data.json");
        Files.move(sourcePath, targetPath);
        getLogger().info("block_data.json moved to /data/ directory successfully.");
      } catch (Exception e) {
        getLogger().warning(
            "Failed to move block_data.json to /data/ directory: " + e.getMessage());
      }
    }
  }

  private void saveResourceIfNotExists(String resourcePath, boolean replace) {
    File file = new File(getDataFolder(), resourcePath);
    if (!file.exists()) {
      saveResource(resourcePath, replace);
    }
  }

}