package xyz.arcadiadevs.gensplus.events;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
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
import xyz.arcadiadevs.gensplus.utils.SellUtil;
import xyz.arcadiadevs.gensplus.utils.message.Messages;

/**
 * The OnWandUse class implements the Listener interface to handle events related to the use of
 * wands in GensPlus.
 */
public class OnWandUse implements Listener {

  /**
   * Handles the PlayerInteractEvent triggered when a player right clicks with a wand.
   *
   * @param event The PlayerInteractEvent object representing the player interact event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onSellWandUse(PlayerInteractEvent event) {

    String version = Bukkit.getBukkitVersion();
    final boolean is1_8 = version.contains("1.8");
    final Player player = event.getPlayer();

    if (!is1_8 && event.getHand().toString().equals("OFF_HAND")) {
      return;
    }

    Block clickedBlock = event.getClickedBlock();
    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

    if (clickedBlock == null) {
      return;
    }

    if (!NBTEditor.contains(itemInMainHand, "sell-wand")) {
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

      if (inventory.isEmpty()) {
        Messages.NOTHING_TO_SELL.format().send(player);
        return;
      }

      for (int i = 0; i < inventory.getSize(); i++) {
        ItemStack item = inventory.getItem(i);

        if (item == null) {
          continue;
        }

        SellUtil.sellWand(player, inventory);
      }
    }

  }

  @EventHandler
  public void onUpgradeWandUse(PlayerInteractEvent event) {
    // TODO: Implement upgrade wand functionality
  }
}
