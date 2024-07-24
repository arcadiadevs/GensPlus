package xyz.arcadiadevs.gensplus.events;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.utils.config.Config;

/**
 * A listener class for handling events related to enchanting items.
 * This class is responsible for checking the items in an enchanting table
 * and performing certain actions based on their attributes.
 */
public class EnchantItem implements Listener {

  /**
   * Called when an enchanting event occurs in an enchanting table.
   * This method checks the items in the enchanting table and
   * performs actions based on their attributes.
   *
   * @param event The EnchantItemEvent event representing the enchanting action.
   */
  @EventHandler
  public void onItemEnchant(EnchantItemEvent event) {
    if (Config.CAN_DROPS_BE_USED_IN_ENCHANTING.getBoolean()) {
      return;
    }

    ItemStack item = event.getItem();
    if (NBTEditor.contains(item, NBTEditor.CUSTOM_DATA, "gensplus", "spawnitem", "tier")
        || NBTEditor.contains(item, NBTEditor.CUSTOM_DATA, "gensplus", "blocktype", "tier")) {
      event.setCancelled(true);
    }

    ItemStack secondItem = event.getView().getItem(1);
    if (secondItem != null && (NBTEditor.contains(secondItem, NBTEditor.CUSTOM_DATA, "gensplus", "spawnitem", "tier")
        || NBTEditor.contains(secondItem, NBTEditor.CUSTOM_DATA, "gensplus", "blocktype", "tier"))) {
      event.setCancelled(true);
    }
  }
}
