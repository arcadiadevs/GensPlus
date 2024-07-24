package xyz.arcadiadevs.gensplus.events;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.utils.config.Config;

/**
 * A listener class for handling events related to crafting tables.
 * This class is responsible for checking the items in a crafting matrix
 * and performing certain actions based on their attributes.
 */
public class CraftItem implements Listener {

  /**
   * Called when a crafting event occurs in a crafting table.
   * This method checks the items in the crafting matrix and
   * performs actions based on their attributes.
   *
   * @param event The CraftingInventory event representing the crafting action.
   */

  @EventHandler
  public void onItemCraft(CraftItemEvent event) {
    if (Config.CAN_DROPS_BE_USED_IN_CRAFTING.getBoolean()) {
      return;
    }

    ItemStack[] item = event.getInventory().getMatrix();
    for (ItemStack stack : item) {
      if (stack == null) {
        continue;
      }

      // Check if the item has specific attributes using NBTEditor
      if (NBTEditor.contains(stack, NBTEditor.CUSTOM_DATA, "gensplus", "spawnitem", "tier")
          || NBTEditor.contains(stack, NBTEditor.CUSTOM_DATA, "gensplus", "blocktype", "tier")) {
        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
        return;
      }
    }
  }
}