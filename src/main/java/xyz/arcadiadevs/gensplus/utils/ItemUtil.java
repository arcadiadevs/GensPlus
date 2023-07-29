package xyz.arcadiadevs.gensplus.utils;

import com.cryptomorin.xseries.XMaterial;
import dev.lone.itemsadder.api.CustomStack;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import io.th0rgal.oraxen.api.OraxenItems;
import java.util.List;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * The ItemUtils class provides utility methods for handling item-related operations.
 */
public class ItemUtil {

  /**
   * Returns an ItemStack representing a universal multi-version supported item,
   * or Oraxen's custom item.
   *
   * @return The universal item.
   */
  public static ItemStack getUniversalItem(String itemName, boolean enableItemsAdder) {
    // Format: oraxen:ITEM_NAME
    if (itemName.toLowerCase().startsWith("oraxen:")) {
      itemName = itemName.substring(7);
      return OraxenItems.getItemById(itemName).build();
    }

    // Format: itemsadder:ITEM_NAME
    if (enableItemsAdder && itemName.toLowerCase().startsWith("itemsadder:")) {
      itemName = itemName.substring(11);

      CustomStack customStack = CustomStack.getInstance(itemName);

      if (customStack == null) {
        return null;
      }

      return customStack.getItemStack();
    }

    // Format: customId:123;ITEM_NAME
    if (itemName.toLowerCase().startsWith("customid:")) {
      itemName = itemName.substring(9);

      String[] idNameSplit = itemName.split(";");

      String id = idNameSplit[0];
      String name = idNameSplit[1];

      ItemStack item = XMaterial.matchXMaterial(name).orElseThrow().parseItem();
      ItemMeta meta = item.getItemMeta();
      meta.setCustomModelData(Integer.parseInt(id));
      item.setItemMeta(meta);

      return item;
    }

    return XMaterial.matchXMaterial(itemName).orElseThrow().parseItem();
  }

  public static void upgradeGens(Inventory inventory) {
    for (int i = 0; i < inventory.getSize(); i++) {
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

        lore.remove(0);
        meta.setLore(lore);
        item.setItemMeta(meta);

        inventory.setItem(i, item);

        continue;
      }

      if (firstLine.contains("Generator tier")) {
        int tier = Integer.parseInt(firstLine.split(" ")[2]);
        item = NBTEditor.set(item, tier, "gensplus", "blocktype", "tier");

        lore.remove(0);
        meta.setLore(lore);
        item.setItemMeta(meta);

        inventory.setItem(i, item);
      }
    }
  }

}
