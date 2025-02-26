package xyz.arcadiadevs.gensplus.placeholders;

import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.models.PlayerData;
import xyz.arcadiadevs.gensplus.tasks.EventLoop;
import xyz.arcadiadevs.gensplus.utils.PlayerUtil;
import xyz.arcadiadevs.gensplus.utils.TimeUtil;
import xyz.arcadiadevs.gensplus.utils.config.Config;

/**
 * The PlaceHolder class is a placeholder expansion for GensPlus. It provides placeholders that
 * can be used in other plugins or systems to retrieve dynamic information.
 */
@AllArgsConstructor
public class PapiHandler extends PlaceholderExpansion {

  private final Plugin plugin;
  private final LocationsData locationsData;
  private final PlayerData playerData;

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
    return "GensPlus";
  }

  /**
   * Retrieves the author of the placeholder expansion.
   *
   * @return The author of the expansion.
   */
  @Override
  public @NotNull String getAuthor() {
    return plugin.getDescription().getAuthors().toString();
  }

  /**
   * Retrieves the version of the placeholder expansion.
   *
   * @return The version of the expansion.
   */
  @Override
  public @NotNull String getVersion() {
    return plugin.getDescription().getVersion();
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
    final boolean useCommands = Config.LIMIT_PER_PLAYER_USE_COMMANDS.getBoolean();
    final boolean usePermissions = Config.LIMIT_PER_PLAYER_USE_PERMISSIONS.getBoolean();

    return switch (params) {
      case "event_timer" -> {
        final long time = EventLoop.getActiveEvent().endTime() - System.currentTimeMillis();
        yield TimeUtil.millisToTime(time);
      }

      case "event_name" ->
          EventLoop.getActiveEvent().event() == null ? "No Events" : EventLoop.getActiveEvent().event().getName();

      case "gen_limit" -> {
        if (!Config.LIMIT_PER_PLAYER_ENABLED.getBoolean()) {
          yield Config.LIMIT_PER_PLAYER_UNLIMITED_PLACEHOLDER.getString();
        }

        if (usePermissions) {
          yield PlayerUtil.getGeneratorLimitPerPlayer(player.getPlayer()).toString();
        }
        if (useCommands) {
          yield String.valueOf(playerData.getData(player.getUniqueId()).getLimit());
        }

        yield Config.LIMIT_PER_PLAYER_DEFAULT_LIMIT.getString();
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
