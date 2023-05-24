package xyz.arcadiadevs.infiniteforge;

import com.cryptomorin.xseries.XMaterial;
import com.github.unldenis.hologram.HologramPool;
import com.github.unldenis.hologram.IHologramPool;
import com.github.unldenis.hologram.InteractiveHologramPool;
import com.github.unldenis.hologram.placeholder.Placeholders;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.samjakob.spigui.SpiGUI;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.arcadiadevs.infiniteforge.commands.Commands;
import xyz.arcadiadevs.infiniteforge.events.BlockBreak;
import xyz.arcadiadevs.infiniteforge.events.BlockPlace;
import xyz.arcadiadevs.infiniteforge.events.ClickEvent;
import xyz.arcadiadevs.infiniteforge.objects.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.objects.LocationsData;
import xyz.arcadiadevs.infiniteforge.objects.events.DropEvent;
import xyz.arcadiadevs.infiniteforge.objects.events.Event;
import xyz.arcadiadevs.infiniteforge.objects.events.SellEvent;
import xyz.arcadiadevs.infiniteforge.objects.events.SpeedEvent;
import xyz.arcadiadevs.infiniteforge.placeholders.PlaceHolder;
import xyz.arcadiadevs.infiniteforge.tasks.DataSaveTask;
import xyz.arcadiadevs.infiniteforge.tasks.EventLoop;
import xyz.arcadiadevs.infiniteforge.tasks.SpawnerTask;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
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

  @Override
  public void onEnable() {

    instance = this;

    saveDefaultConfig();

    saveResource("block_data.json", false);

    setupEconomy();

    if (getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null) {
      new PlaceHolder().register();
    }

    gson = new GsonBuilder().registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
        .setPrettyPrinting().create();

    spiGui = new SpiGUI(this);

    generatorsData = loadGeneratorsData();

    locationsData = new LocationsData(loadBlockDataFromJson());

    events = loadEvents();

    // Register events
    getServer().getPluginManager().registerEvents(new BlockPlace(locationsData), this);
    getServer().getPluginManager()
        .registerEvents(new BlockBreak(locationsData, generatorsData), this);
    getServer().getPluginManager()
        .registerEvents(new ClickEvent(locationsData, generatorsData), this);

    // Run block data save task every 5 minutes
    new DataSaveTask(this).runTaskTimerAsynchronously(this, 0, 20);

    // Run spawner task every second
    new SpawnerTask(locationsData.getGenerators(), generatorsData).runTaskTimerAsynchronously(this,
        0, 20);

    // Start event loop
    new EventLoop(this, events).runTaskLaterAsynchronously(this,
        TimeUtil.parseTime(getConfig().getString("events.time-between-events")));

    // Register commands
    getCommand("infiniteforge").setExecutor(new Commands(this, generatorsData));
    getCommand("getitem").setExecutor(new Commands(this, generatorsData));
    getCommand("generators").setExecutor(new Commands(this, generatorsData));
    getCommand("selldrops").setExecutor(new Commands(this, generatorsData));
  }

  @Override
  public void onDisable() {
    new DataSaveTask(this).runTask(this);
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
  private ArrayList<Event> loadEvents() {
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
   * Loads the generators data from the plugin configuration.
   *
   * @return The generators data.
   * @throws RuntimeException if duplicate tier is found or an invalid item name or item meta is
   *                          encountered.
   */
  private GeneratorsData loadGeneratorsData() {
    List<GeneratorsData.Generator> generators = new ArrayList<>();
    List<Map<?, ?>> generatorsConfig = getConfig().getMapList("generators");

    for (Map<?, ?> generator : generatorsConfig) {
      final String name = (String) generator.get("name");
      int tier = (int) generator.get("tier");
      int speed = (int) generator.get("speed");
      double price = (double) generator.get("price");
      double sellPrice = (double) generator.get("sellprice");
      String spawnItem = (String) generator.get("spawnItem");
      String blockType = (String) generator.get("blockType");
      List<String> lore =
          ((List<String>) generator.get("lore")).isEmpty() ? getConfig().getStringList(
              "default-lore") : (List<String>) generator.get("lore");

      lore = lore.stream().map(s -> s.replace("%tier%", String.valueOf(tier)))
          .map(s -> s.replace("%speed%", String.valueOf(speed)))
          .map(s -> s.replace("%price%", String.valueOf(price)))
          .map(s -> s.replace("%sellprice%", String.valueOf(sellPrice)))
          .map(s -> s.replace("%spawnItem%", spawnItem))
          .map(s -> s.replace("%blockType%", blockType)).map(ChatUtil::translate).toList();

      if (generators.stream().anyMatch(g -> g.tier() == tier)) {
        throw new RuntimeException("Duplicate tier found: " + tier);
      }

      ItemStack spawnItemStack = XMaterial.matchXMaterial(spawnItem).orElseThrow().parseItem();
      ItemStack blockTypeStack = XMaterial.matchXMaterial(blockType).orElseThrow().parseItem();

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
      spawnItemMeta.setDisplayName(ChatUtil.translate(name));
      spawnItemMeta.setLore(
          Collections.singletonList(ChatUtil.translate("&8Generator drop tier " + tier)));

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