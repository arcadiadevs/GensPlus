package xyz.arcadiadevs.gensplus.events;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.gensplus.utils.ItemUtil;

/**
 * The OnInventoryOpen class listens for InventoryOpenEvents.
 */
public class OnInventoryOpen implements Listener {

  /**
   * Sets the tier of a generator drop item in a player's inventory.
   *
   * @param event The InventoryOpenEvent.
   */
  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    Player player = (Player) event.getPlayer();

    ItemUtil.upgradeGens(player.getInventory());
    ItemUtil.upgradeGens(event.getInventory());
  }

}
