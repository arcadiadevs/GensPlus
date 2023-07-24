package xyz.arcadiadevs.gensplus.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import xyz.arcadiadevs.gensplus.GensPlus;

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
    text = ChatColor.translateAlternateColorCodes('&', text);

    Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
    Matcher matcher = hexPattern.matcher(text);
    StringBuilder buffer = new StringBuilder();

    while (matcher.find()) {
      String color = matcher.group(1);
      matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + color).toString());
    }

    return matcher.appendTail(buffer).toString();
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
    if (force || (GensPlus.getInstance().getConfig().getBoolean("events.broadcast.enabled"))) {
      Bukkit.getServer().broadcastMessage(translate(message));
    }
  }

}

