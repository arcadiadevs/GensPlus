package xyz.arcadiadevs.infiniteforge.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

public class PlaceHolder extends PlaceholderExpansion {

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "InfiniteForge";
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
    return switch (params) {
      case "timer_events" -> {
        yield String.valueOf(TimeUtil.getNewTime());
      }


      default -> throw new IllegalStateException("Unexpected value: " + params);
    };
  }



}
