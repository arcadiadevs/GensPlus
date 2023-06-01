package xyz.arcadiadevs.infiniteforge;

import com.cryptomorin.xseries.XMaterial;
import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.HologramPool;
import com.github.unldenis.hologram.IHologramPool;
import com.github.unldenis.hologram.placeholder.Placeholders;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samjakob.spigui.SpiGUI;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.arcadiadevs.infiniteforge.commands.Commands;
import xyz.arcadiadevs.infiniteforge.events.BlockBreak;
import xyz.arcadiadevs.infiniteforge.events.BlockPlace;
import xyz.arcadiadevs.infiniteforge.events.ClickEvent;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.models.HologramsData;
import xyz.arcadiadevs.infiniteforge.models.HologramsData.IfHologram;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;
import xyz.arcadiadevs.infiniteforge.models.events.DropEvent;
import xyz.arcadiadevs.infiniteforge.models.events.Event;
import xyz.arcadiadevs.infiniteforge.models.events.SellEvent;
import xyz.arcadiadevs.infiniteforge.models.events.SpeedEvent;
import xyz.arcadiadevs.infiniteforge.placeholders.PlaceHolder;
import xyz.arcadiadevs.infiniteforge.tasks.DataSaveTask;
import xyz.arcadiadevs.infiniteforge.tasks.EventLoop;
import xyz.arcadiadevs.infiniteforge.tasks.SpawnerTask;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
import xyz.arcadiadevs.infiniteforge.utils.HologramsUtil;
import xyz.arcadiadevs.infiniteforge.utils.ItemUtils;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

/**
 * The main plugin class for InfiniteForge.
 */
public final class InfiniteForge extends JavaPlugin {

  /**
   * Gets the instance of the InfiniteForge plugin.
   */
  @Getter
  public static InfiniteForge instance;

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
   * Gets the data handler for generators.
   */
  @Getter
  private GeneratorsData generatorsData;

  /**
   * Gets the SpiGUI instance for GUI management.
   */
  @Getter
  private SpiGUI spiGui;

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
   * Gets the list of holograms.
   *
   * @implNote Null if holograms are disabled.
   */
  @Getter
  private HologramsData hologramsData;

  /**
   * Gets the data save task.
   */
  private DataSaveTask dataSaveTask;

  @Override
  public void onEnable() {

    instance = this;

    saveDefaultConfig();

    saveResource("block_data.json", false);
    saveResource("holograms.json", false);

    setupEconomy();

    gson = new GsonBuilder().registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
        .setPrettyPrinting()
        .create();

    spiGui = new SpiGUI(this);

    generatorsData = loadGeneratorsData();

    locationsData = new LocationsData(loadBlockDataFromJson());

    events = loadInfiniteForgeEvents();

    if (getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null) {
      new PlaceHolder(locationsData).register();
    }

    loadHolograms();

    // Register events
    loadBukkitEvents();

    // Register tasks
    registerTasks();

    // Register commands
    registerCommands();
  }

  @Override
  public void onDisable() {
    dataSaveTask.saveBlockDataToJson();
    dataSaveTask.saveHologramDataToJson();

    if (getConfig().getBoolean("developer-options.debug")) {
      // Remove all files
      new File(getDataFolder(), "block_data.json").delete();
      new File(getDataFolder(), "holograms.json").delete();
    }
  }

  private void registerCommands() {
    getCommand("infiniteforge").setExecutor(new Commands(this, generatorsData));
    getCommand("getitem").setExecutor(new Commands(this, generatorsData));
    getCommand("generators").setExecutor(new Commands(this, generatorsData));
    getCommand("selldrops").setExecutor(new Commands(this, generatorsData));
  }

  private void loadBukkitEvents() {
    final HashSet<Listener> events = new HashSet<>();

    events.add(new BlockPlace(locationsData, hologramPool, hologramsData));
    events.add(new BlockBreak(locationsData, generatorsData, hologramsData, hologramPool));
    events.add(new ClickEvent(locationsData, generatorsData));

    events.forEach(event -> Bukkit.getPluginManager().registerEvents(event, this));
  }

  private void registerTasks() {
    // Run block data save task every 5 minutes
    dataSaveTask = new DataSaveTask(this);

    dataSaveTask.runTaskTimerAsynchronously(this, 0, 20);

    // Run spawner task every second
    new SpawnerTask(locationsData.getLocations(), generatorsData).runTaskTimerAsynchronously(this,
        0, 20);

    // Start event loop
    new EventLoop(this, events).runTaskLaterAsynchronously(this,
        TimeUtil.parseTime(getConfig().getString("events.time-between-events")));
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
  private ArrayList<Event> loadInfiniteForgeEvents() {
    ArrayList<Event> events = new ArrayList<>();
    if (getConfig().getBoolean("events.drop-event.enabled")) {
      events.add(new DropEvent(getConfig().getLong("events.drop-event.multiplier"),
          getConfig().getString("events.drop-event.name")));
    }
    if (getConfig().getBoolean("events.sell-event.enabled")) {
      events.add(new SellEvent(getConfig().getLong("events.sell-event.multiplier"),
          getConfig().getString("events.sell-event.name")));
    }
    if (getConfig().getBoolean("events.speed-event.enabled")) {
      events.add(new SpeedEvent(getConfig().getLong("events.speed-event.multiplier"),
          getConfig().getString("events.speed-event.name")));
    }
    return events;
  }

  /**
   * Loads holograms from holograms.json. If holograms are disabled, this method removes
   * hologramUuid from all locations and deletes holograms.json.
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void loadHolograms() {
    boolean enabled = getConfig().getBoolean("holograms.enabled", true);

    if (!enabled) {
      locationsData.getLocations().forEach(location -> location.setHologramUuid(null));

      // Remove holograms.json if it exists
      File file = new File(getDataFolder() + "/holograms.json");
      if (file.exists()) {
        file.delete();
      }

      hologramsData = null;

      return;
    }

    hologramPool = new HologramPool(this, getConfig().getInt("holograms.view-distance", 2000));
    placeholders = new Placeholders();

    try (FileReader reader = new FileReader(getDataFolder() + "/holograms.json")) {
      List<IfHologram> hologramsList = gson.fromJson(reader,
          new TypeToken<List<IfHologram>>() {
          }.getType());

      for (IfHologram hologramObject : hologramsList) {

        final Hologram hologram = HologramsUtil.createHologram(
            hologramObject.getLocation(),
            hologramObject.getDescription(),
            Material.matchMaterial(hologramObject.getItemStack())
        );

        hologramPool.takeCareOf(hologram);

        hologramObject.setHologram(hologram);
      }

      hologramsData = new HologramsData(hologramsList);

      for (LocationsData.GeneratorLocation location : locationsData.getLocations()) {
        if (location.getHologramUuid() != null) {
          continue;
        }

        GeneratorsData.Generator generator = location.getGeneratorObject();

        HashSet<Block> connectedBlocks = new HashSet<>();
        locationsData.traverseBlocks(location.getBlock(), location.getGenerator(), connectedBlocks);

        final List<LocationsData.GeneratorLocation> connectedObjects = connectedBlocks.stream()
            .map(locationsData::getLocationData)
            .toList();

        Location centerLocation = locationsData.getCenter(location);

        Material material = XMaterial.matchXMaterial(
                location.getGeneratorObject().blockType().getType().toString())
            .orElseThrow(() -> new RuntimeException("Invalid item stack"))
            .parseItem()
            .getType();

        List<String> lines = instance.getConfig().getStringList("holograms.lines")
            .stream()
            .map(line -> line.replace("%name%", generator.name()))
            .map(line -> line.replace("%tier%", String.valueOf(generator.tier())))
            .map(line -> line.replace("%speed%", String.valueOf(generator.speed())))
            .map(line -> line.replace("%spawnItem%", generator.spawnItem().getType().toString()))
            .map(line -> line.replace("%sellPrice%", String.valueOf(generator.sellPrice())))
            .map(ChatUtil::translate)
            .toList();

        Hologram hologram = HologramsUtil.createHologram(
            centerLocation,
            lines,
            material
        );

        hologramPool.takeCareOf(hologram);

        IfHologram ifHologram = new IfHologram(
            generator.name(),
            lines,
            centerLocation.getX(),
            centerLocation.getY(),
            centerLocation.getZ(),
            centerLocation.getWorld().getName(),
            generator.blockType().getType().toString(),
            hologram
        );

        hologramsData.addHologramData(ifHologram);
        location.setHologramUuid(ifHologram.getUuid());
        connectedObjects.forEach(object -> object.setHologramUuid(ifHologram.getUuid()));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Loads the generators data from the plugin configuration.
   *
   * @return The generators data.
   * @throws RuntimeException if duplicate tier is found or an invalid item name or item meta is
   *                          encountered.
   */
  @SuppressWarnings("unchecked")
  private GeneratorsData loadGeneratorsData() {
    List<GeneratorsData.Generator> generators = new ArrayList<>();
    List<Map<?, ?>> generatorsConfig = getConfig().getMapList("generators");

    for (Map<?, ?> generator : generatorsConfig) {
      final String name = (String) generator.get("name");
      int tier = (int) generator.get("tier");
      int speed = (int) generator.get("speed");
      double price = (double) generator.get("price");
      double sellPrice = (double) generator.get("sellPrice");
      String spawnItem = (String) generator.get("spawnItem");
      String blockType = (String) generator.get("blockType");
      List<String> lore =
          ((List<String>) generator.get("lore")).isEmpty() ? getConfig().getStringList(
              "default-lore") : (List<String>) generator.get("lore");

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

      ItemStack spawnItemStack = ItemUtils.getUniversalItem(spawnItem);
      ItemStack blockTypeStack = ItemUtils.getUniversalItem(blockType);

      if (spawnItemStack == null || blockTypeStack == null) {
        throw new RuntimeException("Invalid item name");
      }

      ItemMeta blockTypeMeta = blockTypeStack.getItemMeta();
      ItemMeta spawnItemMeta = spawnItemStack.getItemMeta();

      if (blockTypeMeta == null || spawnItemMeta == null) {
        throw new RuntimeException("Invalid item meta");
      }

      // set lore for generator block
      List<String> blockTypeLore = new ArrayList<>();

      blockTypeLore.add(ChatUtil.translate("&8Generator tier " + tier));
      blockTypeLore.addAll(lore);

      blockTypeMeta.setDisplayName(ChatUtil.translate(name));
      blockTypeMeta.setLore(blockTypeLore);

      blockTypeStack.setItemMeta(blockTypeMeta);

      // set lore for spawned item
      List<String> spawnLore = new ArrayList<>();

      List<String> itemSpawnLore = ((List<String>) generator.get("itemSpawnLore")).isEmpty()
          ? getConfig().getStringList("default-item-spawn-lore")
          : (List<String>) generator.get("itemSpawnLore");

      itemSpawnLore = itemSpawnLore.stream().map(s -> s.replace("%tier%", String.valueOf(tier)))
          .map(s -> s.replace("%sellPrice%", String.valueOf(sellPrice)))
          .map(ChatUtil::translate)
          .toList();

      // TODO: format sellPrice with economy;

      spawnLore.add(ChatUtil.translate("&8Generator drop tier " + tier));
      spawnLore.addAll(itemSpawnLore);

      spawnItemMeta.setDisplayName(ChatUtil.translate(name));
      spawnItemMeta.setLore(spawnLore);

      spawnItemStack.setItemMeta(spawnItemMeta);

      generators.add(
          new GeneratorsData.Generator(name, tier, price, sellPrice, speed, spawnItemStack,
              blockTypeStack, lore));
    }

    return new GeneratorsData(generators);
  }

  /**
   * Loads the block data from the JSON file.
   *
   * @return The list of generator locations.
   * @throws RuntimeException if an I/O error occurs while reading the JSON file.
   */
  private List<LocationsData.GeneratorLocation> loadBlockDataFromJson() {
    try (FileReader reader = new FileReader(getDataFolder() + "/block_data.json")) {
      return gson.fromJson(reader, new TypeToken<List<LocationsData.GeneratorLocation>>() {
      }.getType());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}