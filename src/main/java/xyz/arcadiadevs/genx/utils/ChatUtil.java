package xyz.arcadiadevs.genx.utils;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {

  public static String translate(String s) {
    return ChatColor.translateAlternateColorCodes('&', s);
  }

  public static List<String> translate(List<String> list) {
    return list.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(
        Collectors.toList());
  }

  public static void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(translate(message));
  }

}
