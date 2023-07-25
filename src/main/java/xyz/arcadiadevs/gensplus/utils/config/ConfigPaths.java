package xyz.arcadiadevs.gensplus.utils.config;

import java.io.File;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

/**
 * The ConfigManager class provides functionality for handling the configuration file in GensPlus.
 */

public enum ConfigPaths {
  ITEM_DESPAWN_TIME("item-despawn-time", "5m"),
  CAN_ITEMS_BE_PLACED("can-items-be-placed", false),
  DISABLED_WORLDS("disabled-worlds", new String[]{}),
  INSTANT_PICKUP("instant-pickup", false),
  ON_JOIN_ENABLED("on-join.enabled", true),
  ON_JOIN_GENERATOR_TIER("on-join.generator-tier", 1),
  ON_JOIN_GENERATOR_AMOUNT("on-join.generator-amount", 3),
  SELL_COMMAND_ENABLED("sell-command.enabled", true),
  LIMIT_SETTINGS_ENABLED("limit-settings.enabled", true),
  LIMIT_SETTINGS_USE_PERMISSIONS("limit-settings.use-permissions", true),
  LIMIT_SETTINGS_DEFAULT_LIMIT("limit-settings.default-limit", 20),
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
  GUIS_UPGRADE_GUI_UPGRADE_ONE("guis.upgrade-gui.upgradeOne", "guis.upgrade-gui.upgradeOne"),
  GUIS_UPGRADE_GUI_UPGRADE_ONE_FIRST_LINE("guis.upgrade-gui.upgradeOne.first-line",
      "guis.upgrade-gui.upgradeOne.first-line"),
  GUIS_UPGRADE_GUI_UPGRADE_ONE_LORE("guis.upgrade-gui.upgradeOne.lore",
      "guis.upgrade-gui.upgradeOne.lore"),
  GUIS_UPGRADE_GUI_UPGRADE_ALL("guis.upgrade-gui.upgradeAll", "guis.upgrade-gui.upgradeAll"),
  GUIS_UPGRADE_GUI_UPGRADE_ALL_FIRST_LINE("guis.upgrade-gui.upgradeAll.first-line",
      "guis.upgrade-gui.upgradeAll.first-line"),
  GUIS_UPGRADE_GUI_UPGRADE_ALL_LORE("guis.upgrade-gui.upgradeAll.lore",
      "guis.upgrade-gui.upgradeAll.lore"),
  GUIS_UPGRADE_GUI_TITLE("guis.upgrade-gui.title", "guis.upgrade-gui.title"),
  GUIS_UPGRADE_GUI_ROWS("guis.upgrade-gui.rows", "guis.upgrade-gui.rows"),
  HOLOGRAMS("holograms", "holograms"),
  HOLOGRAMS_ENABLED("holograms.enabled", "holograms.enabled"),
  HOLOGRAMS_VIEW_DISTANCE("holograms.view-distance", "holograms.view-distance"),
  EVENTS("events", "events"),
  EVENTS_TIME_BETWEEN_EVENTS("events.time-between-events", "events.time-between-events"),
  EVENTS_EVENT_DURATION("events.event-duration", "events.event-duration"),
  EVENTS_BROADCAST("events.broadcast", "events.broadcast"),
  EVENTS_BROADCAST_ENABLED("events.broadcast.enabled", "events.broadcast.enabled"),
  EVENTS_DROP_EVENT("events.drop-event", "events.drop-event"),
  EVENTS_DROP_EVENT_ENABLED("events.drop-event.enabled", "events.drop-event.enabled"),
  EVENTS_DROP_EVENT_NAME("events.drop-event.name", "events.drop-event.name"),
  EVENTS_DROP_EVENT_MULTIPLIER("events.drop-event.multiplier", "events.drop-event.multiplier"),
  EVENTS_SELL_EVENT("events.sell-event", "events.sell-event"),
  EVENTS_SELL_EVENT_ENABLED("events.sell-event.enabled", "events.sell-event.enabled"),
  EVENTS_SELL_EVENT_NAME("events.sell-event.name", "events.sell-event.name"),
  EVENTS_SELL_EVENT_MULTIPLIER("events.sell-event.multiplier", "events.sell-event.multiplier"),
  EVENTS_SPEED_EVENT("events.speed-event", "events.speed-event"),
  EVENTS_SPEED_EVENT_ENABLED("events.speed-event.enabled", "events.speed-event.enabled"),
  EVENTS_SPEED_EVENT_NAME("events.speed-event.name", "events.speed-event.name"),
  EVENTS_SPEED_EVENT_MULTIPLIER("events.speed-event.multiplier", "events.speed-event.multiplier"),
  PARTICLES("particles", "particles"),
  PARTICLES_ENABLED("particles.enabled", "particles.enabled"),
  PARTICLES_TYPE("particles.type", "particles.type"),
  PARTICLES_SOUND("particles.sound", "particles.sound"),
  DEFAULT_LORE("default-lore", "default-lore"),
  DEFAULT_ITEM_SPAWN_LORE("default-item-spawn-lore", "default-item-spawn-lore"),
  DEFAULT_HOLOGRAM_LINES("default-hologram-lines", "default-hologram-lines"),
  DEVELOPER_OPTIONS("developer-options.enabled", false),
  GENERATORS("generators", "generators");

  private final String path;
  private final Object defaultValue;

  ConfigPaths(String path, Object defaultValue) {
    this.path = path;
    this.defaultValue = defaultValue;
  }

  public String getPath() {
    return path;
  }

}
