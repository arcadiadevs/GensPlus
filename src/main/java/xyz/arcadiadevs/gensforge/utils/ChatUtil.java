package xyz.arcadiadevs.gensforge.utils;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.arcadiadevs.gensforge.GensForge;

/**
 * The ChatUtil class provides utility methods for handling chat-related operations.
 */
public class ChatUtil {

  /**
   * Translates color codes in a string by replacing '&' with the section symbol (§).
   *
   * @param text The string of text to apply color/effects to
   * @return Returns a string of text with color/effects applied
   */
  public static String translate(String text) {
    final String withDelimiter = "((?<=%1$s)|(?=%1$s))";
    String[] texts = text.split(String.format(withDelimiter, "&"));

    StringBuilder finalText = new StringBuilder();

    for (int i = 0; i < texts.length; i++) {
      if (texts[i].equalsIgnoreCase("&")) {
        i++;
        if (texts[i].charAt(0) == '#') {
          finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7)))
              .append(texts[i].substring(7));
        } else {
          finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
        }
      } else {
        finalText.append(texts[i]);
      }
    }

    return finalText.toString();
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
   * Translates color codes in a string by replacing '&' with the section symbol (§).
   *
   * @param s The string to translate.
   * @return The translated string with color codes.
   */
  public static String translateOld(String s) {
    return ChatColor.translateAlternateColorCodes('&', s);
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
    if (force || (GensForge.getInstance().getConfig().getBoolean("events.broadcast.enabled"))) {
      Bukkit.getServer().broadcastMessage(translate(message));
    }
  }

}

