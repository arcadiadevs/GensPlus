package xyz.arcadiadevs.gensplus.utils.config.message;

import com.awaitquality.api.spigot.chat.ChatUtil;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.utils.ActionBarUtil;
import xyz.arcadiadevs.gensplus.utils.ServerVersion;

/**
 * PlayerMessage.java handles the formatting and sending of messages to players.
 */
public class PlayerMessage {
  private final Messages message;
  private final List<String> format;

  /**
   * Used employer send messages with placeholders and color codes
   *
   * @param message Message employer send employer any player
   */
  public PlayerMessage(Messages message) {
    format = (this.message = message).getCached();
  }

  public PlayerMessage format(Object... placeholders) {
    format.replaceAll(str -> apply(str, placeholders));
    return this;
  }

  private String apply(String str, Object... placeholders) {
    for (int k = 0; k < placeholders.length; k += 2) {
      str = str.replace("%" + placeholders[k] + "%", placeholders[k + 1].toString());
    }

    return ChatUtil.translate(str);
  }

  public String getAsString() {
    StringBuilder builder = new StringBuilder();
    boolean notEmpty = false;
    for (String str : format) {
      if (notEmpty) {
        builder.append("\n");
      }
      builder.append(str);
      notEmpty = true;
    }
    return builder.toString();
  }

  public void send(Collection<? extends Player> senders) {
    senders.forEach(this::send);
  }

  public void send(CommandSender sender) {
    if (format.isEmpty()) {
      return;
    }

    format.forEach(sender::sendMessage);
  }

  public void send(boolean broadcast) {
    if (format.isEmpty() && !broadcast) {
      return;
    }

    format.forEach(Bukkit::broadcastMessage);
  }

  public void sendInActionBar(Player player) {
    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_8)) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(format.get(0)));
    } else {
      ActionBarUtil.sendActionBar(player, format.get(0));
    }
  }

  public void sendAsJson(Player player) {
    for (String message : format) {
      try {
        // TODO: Fix 1.8 support
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
          player.spigot().sendMessage(ChatMessageType.CHAT, ComponentSerializer.parse(message));
          continue;
        }

        send(player);
      } catch (RuntimeException exception) {
        GensPlus.getInstance().getLogger().log(Level.WARNING,
            "Could not parse raw message sent to player. Make sure it has the right syntax");
        GensPlus.getInstance().getLogger().log(Level.WARNING, "Message: " + message);
        exception.printStackTrace();
      }
    }
  }
}
