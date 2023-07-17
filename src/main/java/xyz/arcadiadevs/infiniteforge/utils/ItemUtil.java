package xyz.arcadiadevs.infiniteforge.utils;

import com.cryptomorin.xseries.XMaterial;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;

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
    if (itemName.contains("oraxen:")) {
      itemName = itemName.replaceAll("oraxen:", "").trim();
      return OraxenItems.getItemById(itemName).build();
    }

    if (itemName.contains("itemsadder:")) {
      itemName = itemName.replaceAll("itemsadder:", "").trim();

      CustomStack customStack = CustomStack.getInstance(itemName);

      if (customStack == null) {
        return null;
      }

      return customStack.getItemStack();
    }

    return XMaterial.matchXMaterial(itemName).orElseThrow().parseItem();
  }

}
