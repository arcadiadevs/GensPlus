package xyz.arcadiadevs.gensplus.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * The OnWandUse class implements the Listener interface to handle events related to the use of
 * wands in GensPlus.
 */
public class OnWandUse implements Listener {

  @EventHandler
  public void onSellWandUse(PlayerInteractEvent event) {
    // TODO: Implement sell wand functionality
  }

  @EventHandler
  public void onUpgradeWandUse(PlayerInteractEvent event) {
    // TODO: Implement upgrade wand functionality
  }
}
