package xyz.arcadiadevs.gensplus.utils.config;

import com.awaitquality.api.spigot.chat.ChatUtil;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.arcadiadevs.gensplus.GensPlus;

import java.util.ArrayList;
import java.util.Map;

/**
 * The ConfigManager class provides functionality for handling the configuration file in GensPlus.
 */
public enum Config {
  ITEM_DESPAWN_TIME("item-despawn-time", "5m"),
  CAN_DROPS_BE_PLACED("can-items-be-placed", false),
  CAN_DROPS_BE_USED_IN_CRAFTING("can-items-be-used-in-crafting", false),
  CAN_DROPS_BE_USED_IN_SMELTING("can-items-be-used-in-smelting", false),
  CAN_DROPS_BE_USED_IN_ENCHANTING("can-items-be-used-in-enchanting", false),
  DISABLE_GENERATORS_WHEN_OFFLINE("disable-generators-when-offline", true),
  DISABLED_WORLDS("disabled-worlds", new ArrayList<String>()),
  INSTANT_PICKUP("instant-pickup", false),
  ON_JOIN_ENABLED("on-join.enabled", true),
  ON_JOIN_GENERATOR_TIER("on-join.generator-tier", 1),
  ON_JOIN_GENERATOR_AMOUNT("on-join.generator-amount", 3),
  GENERATOR_UPGRADE_SNEAK("guis.upgrade-gui.sneak-required", true),
  GENERATOR_UPGRADE_ACTION("guis.upgrade-gui.action", "RIGHT_CLICK_BLOCK"),
  SELL_WAND_ACTION_SNEAK("wands.sell-wand.sneak-required", true),
  SELL_WAND_ACTION("wands.sell-wand.action", "RIGHT_CLICK_BLOCK"),
  SELL_WAND_UNLIMITED_USES_PREFIX("wands.sell-wand.unlimited-uses-prefix", "∞"),
  SELL_COMMAND_ENABLED("sell-command.enabled", true),
  SELL_COMMAND_ALLIASES("sell-command.aliases", new ArrayList<String>()),
  LIMIT_PER_ISLAND_ENABLED("limits.per-island.enabled", false),
  LIMIT_PER_ISLAND_GENS_PER_LEVEL("limits.per-island.gens-per-level", new ArrayList<>()),
  LIMIT_PER_PLAYER_ENABLED("limits.per-player.enabled", false),
  LIMIT_PER_PLAYER_USE_PERMISSIONS("limits.per-player.use-permissions", false),
  LIMIT_PER_PLAYER_USE_COMMANDS("limits.per-player.use-commands", true),
  LIMIT_PER_PLAYER_DEFAULT_LIMIT("limits.per-player.default-limit", 20),
  LIMIT_PER_PLAYER_UNLIMITED_PLACEHOLDER("limits.per-player.unlimited-placeholder", "unlimited"),
  CHUNK_RADIUS_ENABLED("radius.enabled", true),
  CHUNK_RADIUS_USE_PERMISSIONS("radius.use-permissions", true),
  CHUNK_RADIUS_DEFAULT_RADIUS("radius.default-radius", 1),
  MULTIPLIER_USE_PERMISSIONS("multiplier.use-permissions", true),
  MULTIPLIER_DEFAULT_MULTIPLIER("multiplier.default-multiplier", 1),
  GUIS_GENERATORS_GUI_ENABLED("guis.generators-gui.enabled", true),
  GUIS_GENERATORS_GUI_TITLE("guis.generators-gui.title", "Generators"),
  GUIS_GENERATORS_GUI_ROWS("guis.generators-gui.rows", 6),
  GUIS_GENERATORS_GUI_BORDER_ENABLED("guis.generators-gui.border.enabled", true),
  GUIS_GENERATORS_GUI_BORDER_MATERIAL("guis.generators-gui.border.material",
      "WHITE_STAINED_GLASS_PANE"),
  GUIS_GENERATORS_GUI_BORDER_NAME("guis.generators-gui.border.name", " "),
  GUIS_GENERATORS_GUI_NEXT_PAGE_MATERIAL("guis.generators-gui.material.next-page",
      "ARROW"),
  GUIS_GENERATORS_GUI_PREVIOUS_PAGE_MATERIAL("guis.generators-gui.material.previous-page",
      "ARROW"),
  GUIS_GENERATORS_GUI_CLOSE_BUTTON_MATERIAL("guis.generators-gui.material.close-button",
      "BARRIER"),
  GUIS_UPGRADE_GUI_ENABLED("guis.upgrade-gui.enabled", true),
  GUIS_UPGRADE_GUI_UPGRADE_ONE_FIRST_LINE("guis.upgrade-gui.upgradeOne.first-line",
      "&e》 &nClick to upgrade generator!&e 《"),
  GUIS_UPGRADE_GUI_UPGRADE_ONE_LORE("guis.upgrade-gui.upgradeOne.lore", new ArrayList<String>()),
  GUIS_UPGRADE_GUI_UPGRADE_ALL_FIRST_LINE("guis.upgrade-gui.upgradeAll.first-line",
      "&e》 &nClick to upgrade all generators!&e 《"),
  GUIS_UPGRADE_GUI_UPGRADE_ALL_LORE("guis.upgrade-gui.upgradeAll.lore", new ArrayList<String>()),
  GUIS_UPGRADE_GUI_TITLE("guis.upgrade-gui.title", "Upgrade Generator"),
  GUIS_UPGRADE_GUI_ROWS("guis.upgrade-gui.rows", 3),
  GUIS_SELL_GUI_ENABLED("guis.sell-gui.enabled", true),
  GUIS_SELL_GUI_TITLE("guis.sell-gui.title", "Sell Items"),
  GUIS_SELL_GUI_ROWS("guis.sell-gui.rows", 3),
  HOLOGRAMS_ENABLED("holograms.enabled", false),
  HOLOGRAMS_VIEW_DISTANCE("holograms.view-distance", 2000),
  EVENTS_TIME_BETWEEN_EVENTS("events.time-between-events", "1h"),
  EVENTS_EVENT_DURATION("events.event-duration", "2m"),
  EVENTS_BROADCAST_ENABLED("events.broadcast.enabled", true),
  EVENTS_DROP_EVENT_ENABLED("events.drop-event.enabled", true),
  EVENTS_DROP_EVENT_NAME("events.drop-event.name", "Drop Event"),
  EVENTS_DROP_EVENT_MULTIPLIER("events.drop-event.multiplier", 2),
  EVENTS_SELL_EVENT_ENABLED("events.sell-event.enabled", true),
  EVENTS_SELL_EVENT_NAME("events.sell-event.name", "Sell Event"),
  EVENTS_SELL_EVENT_MULTIPLIER("events.sell-event.multiplier", 2),
  EVENTS_SPEED_EVENT_ENABLED("events.speed-event.enabled", true),
  EVENTS_SPEED_EVENT_NAME("events.speed-event.name", "Speed Event"),
  EVENTS_SPEED_EVENT_MULTIPLIER("events.speed-event.multiplier", 2),
  PARTICLES_ENABLED("particles.enabled", true),
  PARTICLES_TYPE("particles.type", "FIREWORKS_SPARK"),
  PARTICLES_SOUND("particles.sound", "ENTITY_FIREWORK_ROCKET_BLAST"),
  DEFAULT_LORE("default-lore", new ArrayList<String>()),
  DEFAULT_ITEM_SPAWN_LORE("default-item-spawn-lore", new ArrayList<String>()),
  DEFAULT_HOLOGRAM_LINES("default-hologram-lines", new ArrayList<String>()),
  DEVELOPER_OPTIONS("developer-options.enabled", false),
  GENERATORS("generators", new ArrayList<Map<?, ?>>());

  @Getter
  private final String path;

  @Getter
  private final Object defaultValue;

  Config(String path, Object defaultValue) {
    this.path = path;
    this.defaultValue = defaultValue;
  }

  public Object get(boolean format) {
    FileConfiguration config = GensPlus.getInstance().getConfig();

    if (!config.contains(path)) {
      config.set(path, defaultValue);
      GensPlus.getInstance().saveConfig();
    }

    Object value = config.get(this.path);

    if (format && !(value instanceof String)) {
      throw new IllegalArgumentException("Cannot format non-string value!");
    }

    return format ? ChatUtil.translate(value.toString()) : value;
  }

  public Object get() {
    return get(false);
  }

  public boolean getBoolean() {
    return (boolean) get();
  }

  public int getInt() {
    return (int) get();
  }

  public double getDouble() {
    return (double) get();
  }

  public String getString() {
    return (String) get();
  }

  public String getStringFormatted() {
    return (String) getFormatted();
  }

  public ArrayList<String> getStringList() {
    return (ArrayList<String>) get();
  }

  public ArrayList<Map<?, ?>> getMapList() {
    return (ArrayList<Map<?, ?>>) get();
  }

  public ArrayList<Integer> getIntegerList() {
    return (ArrayList<Integer>) get();
  }

  public ArrayList<Double> getDoubleList() {
    return (ArrayList<Double>) get();
  }

  public ArrayList<Boolean> getBooleanList() {
    return (ArrayList<Boolean>) get();
  }

  public Object getFormatted() {
    return get(true);
  }

}
