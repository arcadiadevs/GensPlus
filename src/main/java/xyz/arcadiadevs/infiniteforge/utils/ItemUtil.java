package xyz.arcadiadevs.infiniteforge.utils;

import com.cryptomorin.xseries.XMaterial;
import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenItems;
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
  public static ItemStack getUniversalItem(String itemName) {
    // Format: oraxen:ITEM_NAME
    if (itemName.toLowerCase().startsWith("oraxen:")) {
      itemName = itemName.substring(7);
      return OraxenItems.getItemById(itemName).build();
    }

    // Format: itemsadder:ITEM_NAME
    if (itemName.toLowerCase().startsWith("itemsadder:")) {
      itemName = itemName.substring(11);

      CustomStack customStack = CustomStack.getInstance(itemName);

      if (customStack == null) {
        return null;
      }

      return customStack.getItemStack();
    }

    // Format: customId:123;ITEM_NAME
    if (itemName.contains("customId:")) {
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

}
