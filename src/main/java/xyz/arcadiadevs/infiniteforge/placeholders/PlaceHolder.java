package xyz.arcadiadevs.infiniteforge.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;
import xyz.arcadiadevs.infiniteforge.models.events.ActiveEvent;
import xyz.arcadiadevs.infiniteforge.tasks.EventLoop;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

/**
 * The PlaceHolder class is a placeholder expansion for InfiniteForge. It provides placeholders that
 * can be used in other plugins or systems to retrieve dynamic information.
 */
public class PlaceHolder extends PlaceholderExpansion {

  private final LocationsData locationsData;

  public PlaceHolder(LocationsData locationsData) {
    this.locationsData = locationsData;
  }

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
    return "infiniteforge";
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
    final FileConfiguration config = InfiniteForge.getInstance().getConfig();

    return switch (params) {
      case "event_timer" -> {
        final long time = activeEvent.endTime() - System.currentTimeMillis();
        yield TimeUtil.millisToTime(time);
      }
      case "event_name" ->
          activeEvent.event() == null ? "No Events" : activeEvent.event().getName();
      case "gen_limit" -> String.valueOf(config.getInt("limit-settings.limit"));
      case "gen_placed" -> String.valueOf(
              locationsData.getPlacedGeneratorsByPlayer(player.getUniqueId()).size());
      default -> throw new IllegalStateException("Unexpected value: " + params);
    };
  }

}
