package xyz.arcadiadevs.gensplus.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import xyz.arcadiadevs.gensplus.utils.SellUtil;

public class SellCommandListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    Player player = event.getPlayer();
    String[] args = event.getMessage().toLowerCase().split(" ");
    if (args.length > 0 && args[0].equalsIgnoreCase("/sell")) {
      SellUtil.sellAll(player);
      event.setCancelled(true);
    }
  }

}
