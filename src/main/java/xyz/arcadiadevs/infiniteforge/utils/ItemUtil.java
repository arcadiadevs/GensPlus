package xyz.arcadiadevs.infiniteforge.utils;

import com.cryptomorin.xseries.XMaterial;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;

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
    return itemName.contains("custom:")
        ? OraxenItems.getItemById(itemName.replaceAll("custom:", "")).build()
        : XMaterial.matchXMaterial(itemName).orElseThrow().parseItem();
  }

}
