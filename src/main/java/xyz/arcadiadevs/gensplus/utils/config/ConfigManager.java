package xyz.arcadiadevs.gensplus.utils.config;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.gensplus.GensPlus;

/**
 * The ConfigManager class provides functionality for handling the configuration file in GensPlus.
 */
public class ConfigManager extends FileConfiguration {

  private final GensPlus instance;

  public ConfigManager(GensPlus instance) {
    this.instance = instance;
  }

  /**
   * The ConfigType enum represents the different types of configuration files in GensPlus.
   */
  public enum ConfigType {
    MESSAGES("messages.yml"),
    BLOCK_DATA("block-data.json");

    private final String fileName;

    ConfigType(String fileName) {
      this.fileName = fileName;
    }

    public String getFileName() {
      return fileName;
    }
  }

  public enum ConfigPaths {
    ITEM_DESPAWN_TIME("item-despawn-time"),
    CAN_ITEMS_BE_PLACED("can-items-be-placed"),
    DISABLED_WORLDS("disabled-worlds"),
    INSTANT_PICKUP("instant-pickup"),
    ON_JOIN("on-join"),
    ON_JOIN_ENABLED("on-join.enabled"),
    ON_JOIN_GENERATOR_TIER("on-join.generator-tier"),
    ON_JOIN_GENERATOR_AMOUNT("on-join.generator-amount"),
    SELL_COMMAND("sell-command"),
    SELL_COMMAND_ENABLED("sell-command.enabled"),
    LIMIT_SETTINGS("limit-settings"),
    LIMIT_SETTINGS_ENABLED("limit-settings.enabled"),
    LIMIT_SETTINGS_USE_PERMISSIONS("limit-settings.use-permissions"),
    LIMIT_SETTINGS_DEFAULT_LIMIT("limit-settings.default-limit"),
    CHUNK_RADIUS("chunk-radius"),
    CHUNK_RADIUS_ENABLED("chunk-radius.enabled"),
    CHUNK_RADIUS_USE_PERMISSIONS("chunk-radius.use-permissions"),
    CHUNK_RADIUS_DEFAULT_RADIUS("chunk-radius.default-radius"),
    MULTIPLIER("multiplier"),
    MULTIPLIER_USE_PERMISSIONS("multiplier.use-permissions"),
    MULTIPLIER_DEFAULT_MULTIPLIER("multiplier.default-multiplier"),
    GUIS("guis"),
    GUIS_GENERATORS_GUI("guis.generators-gui"),
    GUIS_GENERATORS_GUI_ENABLED("guis.generators-gui.enabled"),
    GUIS_GENERATORS_GUI_TITLE("guis.generators-gui.title"),
    GUIS_GENERATORS_GUI_ROWS("guis.generators-gui.rows"),
    GUIS_GENERATORS_GUI_BORDER("guis.generators-gui.border"),
    GUIS_GENERATORS_GUI_BORDER_ENABLED("guis.generators-gui.border.enabled"),
    GUIS_GENERATORS_GUI_BORDER_MATERIAL("guis.generators-gui.border.material"),
    GUIS_GENERATORS_GUI_BORDER_NAME("guis.generators-gui.border.name"),
    GUIS_UPGRADE_GUI("guis.upgrade-gui"),
    GUIS_UPGRADE_GUI_ENABLED("guis.upgrade-gui.enabled"),
    GUIS_UPGRADE_GUI_UPGRADE_ONE("guis.upgrade-gui.upgradeOne"),
    GUIS_UPGRADE_GUI_UPGRADE_ONE_FIRST_LINE("guis.upgrade-gui.upgradeOne.first-line"),
    GUIS_UPGRADE_GUI_UPGRADE_ONE_LORE("guis.upgrade-gui.upgradeOne.lore"),
    GUIS_UPGRADE_GUI_UPGRADE_ALL("guis.upgrade-gui.upgradeAll"),
    GUIS_UPGRADE_GUI_UPGRADE_ALL_FIRST_LINE("guis.upgrade-gui.upgradeAll.first-line"),
    GUIS_UPGRADE_GUI_UPGRADE_ALL_LORE("guis.upgrade-gui.upgradeAll.lore"),
    GUIS_UPGRADE_GUI_TITLE("guis.upgrade-gui.title"),
    GUIS_UPGRADE_GUI_ROWS("guis.upgrade-gui.rows"),
    HOLOGRAMS("holograms"),
    HOLOGRAMS_ENABLED("holograms.enabled"),
    HOLOGRAMS_VIEW_DISTANCE("holograms.view-distance"),
    EVENTS("events"),
    EVENTS_TIME_BETWEEN_EVENTS("events.time-between-events"),
    EVENTS_EVENT_DURATION("events.event-duration"),
    EVENTS_BROADCAST("events.broadcast"),
    EVENTS_BROADCAST_ENABLED("events.broadcast.enabled"),
    EVENTS_DROP_EVENT("events.drop-event"),
    EVENTS_DROP_EVENT_ENABLED("events.drop-event.enabled"),
    EVENTS_DROP_EVENT_NAME("events.drop-event.name"),
    EVENTS_DROP_EVENT_MULTIPLIER("events.drop-event.multiplier"),
    EVENTS_SELL_EVENT("events.sell-event"),
    EVENTS_SELL_EVENT_ENABLED("events.sell-event.enabled"),
    EVENTS_SELL_EVENT_NAME("events.sell-event.name"),
    EVENTS_SELL_EVENT_MULTIPLIER("events.sell-event.multiplier"),
    EVENTS_SPEED_EVENT("events.speed-event"),
    EVENTS_SPEED_EVENT_ENABLED("events.speed-event.enabled"),
    EVENTS_SPEED_EVENT_NAME("events.speed-event.name"),
    EVENTS_SPEED_EVENT_MULTIPLIER("events.speed-event.multiplier"),
    PARTICLES("particles"),
    PARTICLES_ENABLED("particles.enabled"),
    PARTICLES_TYPE("particles.type"),
    PARTICLES_SOUND("particles.sound"),
    DEFAULT_LORE("default-lore"),
    DEFAULT_ITEM_SPAWN_LORE("default-item-spawn-lore"),
    DEFAULT_HOLOGRAM_LINES("default-hologram-lines"),
    DEVELOPER_OPTIONS("developer-options"),
    GENERATORS("generators");

    private final String path;

    ConfigPaths(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }
  }

  @NotNull
  @Override
  public String saveToString() {
    return null;
  }

  @Override
  public void loadFromString(@NotNull String s) throws InvalidConfigurationException {

  }

  /**
   * Loads the specified configuration file.
   *
   * @param resourcePath The path to the configuration file.
   * @param replace      Whether to replace the configuration file if it already exists.
   */
  public void saveResourceIfNotExists(String resourcePath, boolean replace) {
    File file = new File(instance.getDataFolder(), resourcePath);
    if (!file.exists()) {
      instance.saveResource(resourcePath, replace);
    }
  }
}
