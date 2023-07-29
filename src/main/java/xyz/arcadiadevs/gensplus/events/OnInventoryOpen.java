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
    Inventory inventory = player.getInventory();

    for (int i = 0; i <= inventory.getSize(); i++) {
      ItemStack item = inventory.getItem(i);

      if (item == null) {
        continue;
      }

      ItemMeta meta = item.getItemMeta();

      if (meta == null) {
        continue;
      }

      if (!meta.hasLore()) {
        continue;
      }

      List<String> lore = meta.getLore();

      if (lore == null) {
        continue;
      }

      String firstLine = lore.get(0);

      if (firstLine.contains("Generator drop tier")) {
        int tier = Integer.parseInt(firstLine.split(" ")[3]);
        item = NBTEditor.set(item, tier, "gensplus", "spawnitem", "tier");
        player.getInventory().setItem(i, item);
        continue;
      }

      if (firstLine.contains("Generator tier")) {
        int tier = Integer.parseInt(firstLine.split(" ")[2]);
        item = NBTEditor.set(item, tier, "gensplus", "blocktype", "tier");
        player.getInventory().setItem(i, item);
        continue;
      }
    }
  }
}
