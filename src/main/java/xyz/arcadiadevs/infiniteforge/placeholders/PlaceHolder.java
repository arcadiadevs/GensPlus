package xyz.arcadiadevs.infiniteforge.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.infiniteforge.objects.events.ActiveEvent;
import xyz.arcadiadevs.infiniteforge.tasks.EventLoop;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

public class PlaceHolder extends PlaceholderExpansion {

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "infiniteforge";
  }

  @Override
  public @NotNull String getName() {
    return "placeholders";
  }

  @Override
  public @NotNull String getAuthor() {
    return "OpenSource/Cuftica";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  @Override
  public String onRequest(OfflinePlayer player, String params) {
    final ActiveEvent activeEvent = EventLoop.getActiveEvent();

    return switch (params) {
      case "event_timer" -> {
        final long time = activeEvent.getEndTime() - System.currentTimeMillis();
        yield TimeUtil.millisToTime(time);
      }
      case "event_name" ->
          activeEvent.getEvent() == null ? "No Events" : activeEvent.getEvent().getName();
      default -> throw new IllegalStateException("Unexpected value: " + params);
    };
  }


}
