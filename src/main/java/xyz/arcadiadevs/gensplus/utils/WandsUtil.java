package xyz.arcadiadevs.gensplus.utils;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
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
  public static ItemStack getSellWand() {

    WandData wandData = GensPlus.getInstance().getWandData();
    WandData.Wand wand = wandData.create(WandData.Wand.WandType.SELL_WAND, 200, 0, 10);

    List<String> lore = Formatter.format(wand, config.getStringList("wands.sell-wand.lore"));

    final Material material = XMaterial.matchXMaterial(config.getString("wands.sell-wand.material"))
        .orElseThrow()
        .parseMaterial();

    ItemBuilder itemBuilder = new ItemBuilder(material)
        .name(ChatUtil.translate(config.getString("wands.sell-wand.name")))
        .lore(lore);

    return itemBuilder.build();
  }

  /**
   * Returns the ItemStack representing the upgrade wand.
   *
   * @return The ItemStack representing the upgrade wand.
   */
  public static ItemStack getUpgradeWand() {
    WandData wandData = GensPlus.getInstance().getWandData();
    WandData.Wand wand = wandData.create(WandData.Wand.WandType.UPGRADE_WAND, 10, 0, 0);

    List<String> lore = Formatter.format(wand, config.getStringList("wands.upgrade-wand.lore"));

    final Material material =
        XMaterial.matchXMaterial(config.getString("wands.upgrade-wand.material"))
            .orElseThrow()
            .parseMaterial();

    ItemBuilder itemBuilder = new ItemBuilder(material)
        .name(ChatUtil.translate(config.getString("wands.upgrade-wand.name")))
        .lore(lore);

    return itemBuilder.build();
  }
}
