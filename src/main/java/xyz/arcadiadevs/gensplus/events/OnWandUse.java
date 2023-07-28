package xyz.arcadiadevs.gensplus.events;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.models.WandData;
import xyz.arcadiadevs.gensplus.utils.SellUtil;

/**
 * The OnWandUse class implements the Listener interface to handle events related to the use of
 * wands in GensPlus.
 */
@AllArgsConstructor
public class OnWandUse implements Listener {

  private final WandData wandData;

  /**
   * Handles the PlayerInteractEvent triggered when a player right clicks with a wand.
   *
   * @param event The PlayerInteractEvent object representing the player interact event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onSellWandUse(PlayerInteractEvent event) {
    // Check if player right shift click on a chest or hopper
    Player player = event.getPlayer();
    Block clickedBlock = event.getClickedBlock();

    if (clickedBlock == null) {
      return;
    }

    if (player.isSneaking() && (clickedBlock.getType() == Material.CHEST
        || clickedBlock.getType() == Material.HOPPER)) {

      // Look into the chest or hopper and sell all items
      Inventory inventory = null;
      if (clickedBlock.getType() == Material.CHEST) {
        Chest chest = (Chest) clickedBlock.getState();
        inventory = chest.getBlockInventory();
      } else if (clickedBlock.getType() == Material.HOPPER) {
        Hopper hopper = (Hopper) clickedBlock.getState();
        inventory = hopper.getInventory();
      }

      if (inventory == null) {
        return;
      }
    }
    // TODO: Implement sell wand functionality (see SellUtil.java)

  }

  @EventHandler
  public void onUpgradeWandUse(PlayerInteractEvent event) {
    // TODO: Implement upgrade wand functionality
  }
}
