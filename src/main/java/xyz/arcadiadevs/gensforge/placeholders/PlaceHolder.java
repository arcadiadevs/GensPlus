package xyz.arcadiadevs.gensforge.placeholders;

import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.gensforge.models.LocationsData;
import xyz.arcadiadevs.gensforge.models.events.ActiveEvent;
import xyz.arcadiadevs.gensforge.tasks.EventLoop;
import xyz.arcadiadevs.gensforge.utils.PlayerUtil;
import xyz.arcadiadevs.gensforge.utils.TimeUtil;

/**
 * The PlaceHolder class is a placeholder expansion for GensForge. It provides placeholders that
 * can be used in other plugins or systems to retrieve dynamic information.
 */
@AllArgsConstructor
public class PlaceHolder extends PlaceholderExpansion {

  private final LocationsData locationsData;
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
    return "gensforge";
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

    return switch (params) {
      case "event_timer" -> {
        final long time = activeEvent.endTime() - System.currentTimeMillis();
        yield TimeUtil.millisToTime(time);
      }
      case "event_name" ->
          activeEvent.event() == null ? "No Events" : activeEvent.event().getName();
      case "gen_limit" -> config.getBoolean("limit-settings.enabled")
          ? PlayerUtil.getGeneratorLimit(player.getPlayer()).toString() : "Unlimited";
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
