package xyz.arcadiadevs.gensplus.utils;

import com.awaitquality.api.spigot.chat.formatter.Formatter;
import com.cryptomorin.xseries.XMaterial;
import dev.lone.itemsadder.api.CustomStack;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import io.th0rgal.oraxen.api.OraxenItems;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.WandData;
import xyz.arcadiadevs.guilib.ItemBuilder;

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

      // TODO fix 1.8
      if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) {
        meta.setCustomModelData(Integer.parseInt(id));
      }

      item.setItemMeta(meta);

      return item;
    }

    XMaterial material = XMaterial.matchXMaterial(itemName).orElse(null);

    if (material == null) {
      return null;
    }

    return material.parseItem();
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
        final int tier = Integer.parseInt(firstLine.split(" ")[3]);

        lore.remove(0);
        meta.setLore(lore);
        item.setItemMeta(meta);

        item = NBTEditor.set(item, tier, "gensplus", "spawnitem", "tier");

        inventory.setItem(i, item);

        continue;
      }

      if (firstLine.contains("Generator tier")) {
        final int tier = Integer.parseInt(firstLine.split(" ")[2]);

        lore.remove(0);
        meta.setLore(lore);
        item.setItemMeta(meta);

        item = NBTEditor.set(item, tier, "gensplus", "blocktype", "tier");

        inventory.setItem(i, item);
      }
    }
  }

  public static ItemStack getWand(WandData.Wand.WandType type, int uses, double multiplier) {
    WandData wandData = GensPlus.getInstance().getWandData();
    WandData.Wand wand = wandData.create(type, uses, multiplier);

    String configPrefix = "wands.sell-wand";

    FileConfiguration config = GensPlus.getInstance().getConfig();

    List<String> lore = config.getStringList(configPrefix + ".lore");

    List<String> formattedLore = Formatter.format(wand, lore);

    final Material material = XMaterial.matchXMaterial(config.getString(configPrefix + ".material"))
        .orElseThrow()
        .parseMaterial();

    final String formattedName = Formatter.format(wand, config.getString(configPrefix + ".name"));

    ItemBuilder itemBuilder = new ItemBuilder(material)
        .name(formattedName)
        .lore(formattedLore);

    ItemStack item = itemBuilder.build();

    String uuid = "sell-wand-uuid";

    item = NBTEditor.set(item, wand.getUuid().toString(), uuid);

    return item;
  }

  public static ItemStack getSellWand(int uses, double multiplier) {
    return getWand(WandData.Wand.WandType.SELL_WAND, uses, multiplier);
  }

}
