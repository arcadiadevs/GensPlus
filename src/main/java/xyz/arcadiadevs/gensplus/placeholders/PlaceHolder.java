package xyz.arcadiadevs.gensplus.placeholders;

import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.models.PlayerData;
import xyz.arcadiadevs.gensplus.models.events.ActiveEvent;
import xyz.arcadiadevs.gensplus.tasks.EventLoop;
import xyz.arcadiadevs.gensplus.utils.PlayerUtil;
import xyz.arcadiadevs.gensplus.utils.TimeUtil;
import xyz.arcadiadevs.gensplus.utils.Config;

/**
 * The PlaceHolder class is a placeholder expansion for GensPlus. It provides placeholders that
 * can be used in other plugins or systems to retrieve dynamic information.
 */
@AllArgsConstructor
public class PlaceHolder extends PlaceholderExpansion {

  private final LocationsData locationsData;
  private final PlayerData playerData;
  private final FileConfiguration config;

  /**
   * Checks if the placeholder expansion can be registered.
   *
   * @return true if the expansion can be registered, false otherwise.
   */
  @Override
  public boolean canRegister() {
    return true;
  }

  /**
   * Retrieves the identifier for the placeholder expansion.
   *
   * @return The identifier for the expansion.
   */
  @Override
  public @NotNull String getIdentifier() {
    return "gensplus";
  }

  /**
   * Retrieves the name of the placeholder expansion.
   *
   * @return The name of the expansion.
   */
  @Override
  public @NotNull String getName() {
    return "placeholders";
  }

  /**
   * Retrieves the author of the placeholder expansion.
   *
   * @return The author of the expansion.
   */
  @Override
  public @NotNull String getAuthor() {
    return "OpenSource/Cuftica";
  }

  /**
   * Retrieves the version of the placeholder expansion.
   *
   * @return The version of the expansion.
   */
  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  /**
   * Processes the placeholder request and returns the corresponding value.
   *
   * @param player The player for whom the placeholder is being requested.
   * @param params The parameters specifying the placeholder.
   * @return The value of the requested placeholder.
   * @throws IllegalStateException if an unexpected parameter value is encountered.
   */
  @Override
  public String onRequest(OfflinePlayer player, String params) {
    final ActiveEvent activeEvent = EventLoop.getActiveEvent();
    final boolean useCommands =
        config.getBoolean(Config.LIMIT_SETTINGS_USE_COMMANDS.getPath());
    final boolean usePermissions = config
        .getBoolean(Config.LIMIT_SETTINGS_USE_PERMISSIONS.getPath());

    return switch (params) {
      case "event_timer" -> {
        final long time = activeEvent.endTime() - System.currentTimeMillis();
        yield TimeUtil.millisToTime(time);
      }

      case "event_name" ->
          activeEvent.event() == null ? "No Events" : activeEvent.event().getName();

      case "gen_limit" -> {
        if (!config.getBoolean(Config.LIMIT_SETTINGS_ENABLED.getPath())) {
          yield config.getString("limit-settings.unlimited-placeholder");
        }

        if (usePermissions) {
          yield PlayerUtil.getGeneratorLimit(player.getPlayer()).toString();
        }

        if (useCommands) {
          yield String.valueOf(playerData.getData(player.getUniqueId()).getLimit());
        }

        yield config.getString(Config.LIMIT_SETTINGS_DEFAULT_LIMIT.getPath());
      }

      case "gen_placed" -> locationsData.getGeneratorsCountByPlayer(player.getPlayer()).toString();
      case "sell_multiplier" -> PlayerUtil.getMultiplier(player.getPlayer()).toString();
      default -> throw new IllegalStateException("Unexpected value: " + params);
    };
  }

  @Override
  public boolean persist() {
    return true;
  }

}
