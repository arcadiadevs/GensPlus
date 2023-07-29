package xyz.arcadiadevs.gensplus.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.utils.ChatUtil;

/**
 * The ConfigManager class provides functionality for handling the configuration file in GensPlus.
 */
public enum Config {
  ITEM_DESPAWN_TIME("item-despawn-time", "5m"),
  CAN_DROPS_BE_PLACED("can-items-be-placed", false),
  DISABLED_WORLDS("disabled-worlds", new ArrayList<String>()),
  INSTANT_PICKUP("instant-pickup", false),
  ON_JOIN_ENABLED("on-join.enabled", true),
  ON_JOIN_GENERATOR_TIER("on-join.generator-tier", 1),
  ON_JOIN_GENERATOR_AMOUNT("on-join.generator-amount", 3),
  SELL_COMMAND_ENABLED("sell-command.enabled", true),
  LIMIT_SETTINGS_ENABLED("limit-settings.enabled", true),
  LIMIT_SETTINGS_USE_PERMISSIONS("limit-settings.use-permissions", false),
  LIMIT_SETTINGS_USE_COMMANDS("limit-settings.use-commands", true),
  LIMIT_SETTINGS_DEFAULT_LIMIT("limit-settings.default-limit", 20),
  LIMIT_SETTINGS_UNLIMITED_PLACEHOLDER("limit-settings.unlimited-placeholder", "unlimited"),
  CHUNK_RADIUS_ENABLED("chunk-radius.enabled", true),
  CHUNK_RADIUS_USE_PERMISSIONS("chunk-radius.use-permissions", true),
  CHUNK_RADIUS_DEFAULT_RADIUS("chunk-radius.default-radius", 1),
  MULTIPLIER_USE_PERMISSIONS("multiplier.use-permissions", true),
  MULTIPLIER_DEFAULT_MULTIPLIER("multiplier.default-multiplier", 1),
  GUIS_GENERATORS_GUI_ENABLED("guis.generators-gui.enabled", true),
  GUIS_GENERATORS_GUI_TITLE("guis.generators-gui.title", "Generators"),
  GUIS_GENERATORS_GUI_ROWS("guis.generators-gui.rows", 6),
  GUIS_GENERATORS_GUI_BORDER_ENABLED("guis.generators-gui.border.enabled", true),
  GUIS_GENERATORS_GUI_BORDER_MATERIAL("guis.generators-gui.border.material",
      "WHITE_STAINED_GLASS_PANE"),
  GUIS_GENERATORS_GUI_BORDER_NAME("guis.generators-gui.border.name", " "),
  GUIS_UPGRADE_GUI_ENABLED("guis.upgrade-gui.enabled", true),
  GUIS_UPGRADE_GUI_UPGRADE_ONE_FIRST_LINE("guis.upgrade-gui.upgradeOne.first-line",
      "&e》 &nClick to upgrade generator!&e 《"),
  GUIS_UPGRADE_GUI_UPGRADE_ONE_LORE("guis.upgrade-gui.upgradeOne.lore", new ArrayList<String>()),
  GUIS_UPGRADE_GUI_UPGRADE_ALL_FIRST_LINE("guis.upgrade-gui.upgradeAll.first-line",
      "&e》 &nClick to upgrade all generators!&e 《"),
  GUIS_UPGRADE_GUI_UPGRADE_ALL_LORE("guis.upgrade-gui.upgradeAll.lore", new ArrayList<String>()),
  GUIS_UPGRADE_GUI_TITLE("guis.upgrade-gui.title", "Upgrade Generator"),
  GUIS_UPGRADE_GUI_ROWS("guis.upgrade-gui.rows", 3),
  HOLOGRAMS_ENABLED("holograms.enabled", false),
  HOLOGRAMS_VIEW_DISTANCE("holograms.view-distance", 300),
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
    }

    Object value = config.get(this.path);

    if (format && !(value instanceof String)) {
      throw new IllegalArgumentException("Cannot format non-string value!");
    }

    return format ? ChatUtil.translate(value.toString()) : value;
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

  public List<String> getStringList() {
    return (List<String>) get();
  }

  public List<Map<?, ?>> getMapList() {
    return (List<Map<?, ?>>) get();
  }

  public List<Integer> getIntegerList() {
    return (List<Integer>) get();
  }

  public List<Double> getDoubleList() {
    return (List<Double>) get();
  }

  public List<Boolean> getBooleanList() {
    return (List<Boolean>) get();
  }

  public Object get() {
    return get(false);
  }

  public Object getFormatted() {
    return get(true);
  }

}
