package xyz.arcadiadevs.gensplus.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.arcadiadevs.gensplus.guis.SellGui;
import xyz.arcadiadevs.gensplus.utils.SellUtil;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.Permissions;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;

import java.util.List;

public class SellCommandListener implements Listener {
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    Player player = event.getPlayer();
    String[] args = event.getMessage().split(" ");
    args[0] = args[0].toLowerCase();


    if (!Config.SELL_COMMAND_ENABLED.getBoolean()) {
      return;
    }

    List<String> commands = Config.SELL_COMMAND_ALLIASES.getStringList();
    String sentCommand = args[0].replace("/", "");

    if (!commands.contains(sentCommand)) {
      return;
    }

    if (args.length < 2) {
      Messages.NOT_ENOUGH_ARGUMENTS.format().send(player);
      event.setCancelled(true);
      return;
    }

    if (args[1].equalsIgnoreCase("all")) {
      if (!player.hasPermission(Permissions.GENERATOR_DROPS_SELL_ALL.getPermission())) {
        Messages.NO_PERMISSION.format().send(player);
        return;
      }

      SellUtil.sellAll(player, player.getInventory());
      event.setCancelled(true);
      return;
    }

    if (args[1].equalsIgnoreCase("hand")) {
      if (!player.hasPermission(Permissions.GENERATOR_DROPS_SELL_HAND.getPermission())) {
        Messages.NO_PERMISSION.format().send(player);
        return;
      }

      SellUtil.sellHand(player);
      event.setCancelled(true);
      return;
    }

    if (args[1].equalsIgnoreCase("gui")) {
      if (!player.hasPermission(Permissions.GENERATOR_DROPS_SELL_GUI.getPermission())) {
        Messages.NO_PERMISSION.format().send(player);
        return;
      }

      SellGui.open(player);
      event.setCancelled(true);
    }
  }
}
