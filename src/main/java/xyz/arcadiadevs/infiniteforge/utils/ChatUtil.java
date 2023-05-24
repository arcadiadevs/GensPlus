package xyz.arcadiadevs.infiniteforge.utils;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;

/**
 * The ChatUtil class provides utility methods for handling chat-related operations.
 */
public class ChatUtil {

  /**
   * Translates color codes in a string by replacing '&' with the section symbol (§).
   *
   * @param s The string to translate.
   * @return The translated string with color codes.
   */
  public static String translate(String s) {
    return ChatColor.translateAlternateColorCodes('&', s);
  }

  /**
   * Translates color codes in a list of strings by replacing '&' with the section symbol (§).
   *
   * @param list The list of strings to translate.
   * @return The translated list of strings with color codes.
   */
  public static List<String> translate(List<String> list) {
    return list.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s))
        .collect(Collectors.toList());
  }

  /**
   * Sends a translated message to a command sender.
   *
   * @param sender  The command sender to send the message to.
   * @param message The message to send.
   */
  public static void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(translate(message));
  }

  /**
   * Sends a translated broadcast message to all online players.
   *
   * @param message The message to broadcast.
   */
  public static void sendBroadcast(String message, boolean force) {
    if (force || (InfiniteForge.getInstance().getConfig().getBoolean("events.broadcast.enabled"))) {
      Bukkit.getServer().broadcastMessage(translate(message));
    }
  }

}

