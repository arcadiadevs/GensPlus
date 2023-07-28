package xyz.arcadiadevs.gensplus.utils;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.WandData;
import xyz.arcadiadevs.gensplus.utils.formatter.Formatter;
import xyz.arcadiadevs.guilib.ItemBuilder;

/**
 * The WandsUtil class provides utility methods for working with wands in GensPlus.
 */
public class WandsUtil {


  private static final FileConfiguration config = GensPlus.getInstance().getConfig();

  /**
   * Returns the ItemStack representing the sell wand.
   *
   * @return The ItemStack representing the sell wand.
   */
  public static ItemStack getSellWand(int uses, double multiplier) {
    WandData wandData = GensPlus.getInstance().getWandData();
    WandData.Wand wand = wandData.create(WandData.Wand.WandType.SELL_WAND, uses, multiplier, 0);

    List<String> lore = Formatter.format(wand, config.getStringList("wands.sell-wand.lore"));

    final Material material = XMaterial.matchXMaterial(config.getString("wands.sell-wand.material"))
        .orElseThrow()
        .parseMaterial();

    ItemBuilder itemBuilder = new ItemBuilder(material)
        .name(ChatUtil.translate(config.getString("wands.sell-wand.name")))
        .lore(lore);

    ItemStack item = itemBuilder.build();

    item = NBTEditor.set(item, UUID.randomUUID().toString(), "sell-wand");

    return item;
  }

  /**
   * Returns the ItemStack representing the upgrade wand.
   *
   * @return The ItemStack representing the upgrade wand.
   */
  public static ItemStack getUpgradeWand(int uses, double multiplier, int radius) {
    WandData wandData = GensPlus.getInstance().getWandData();
    WandData.Wand wand =
        wandData.create(WandData.Wand.WandType.UPGRADE_WAND, uses, multiplier, radius);

    List<String> lore = Formatter.format(wand, config.getStringList("wands.upgrade-wand.lore"));

    final Material material =
        XMaterial.matchXMaterial(config.getString("wands.upgrade-wand.material"))
            .orElseThrow()
            .parseMaterial();

    ItemBuilder itemBuilder = new ItemBuilder(material)
        .name(ChatUtil.translate(config.getString("wands.upgrade-wand.name")))
        .lore(lore);

    ItemStack item = itemBuilder.build();

    item = NBTEditor.set(item, UUID.randomUUID().toString(), "upgrade-wand");

    return item;
  }
}
