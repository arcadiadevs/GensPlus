package xyz.arcadiadevs.gensplus.utils;

import com.cryptomorin.xseries.XMaterial;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.GensPlus;
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

    List<String> lore = config.getStringList("wands.sell-wand.lore");

    lore = lore.stream()
        .map(s -> s.replace("%uses%", "uses"))
        .map(s -> s.replace("%multiplier%", "multiplier"))
        .map(ChatUtil::translate)
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

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
    List<String> lore = config.getStringList("wands.upgrade-wand.lore");

    lore = lore.stream()
        .map(s -> s.replace("%uses%", "uses"))
        .map(s -> s.replace("%radius%", "radius"))
        .map(s -> s.replace("%multiplier%", "multiplier"))
        .map(ChatUtil::translate)
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    final Material material = XMaterial.matchXMaterial(config.getString("wands.upgrade-wand.material"))
        .orElseThrow()
        .parseMaterial();

    ItemBuilder itemBuilder = new ItemBuilder(material)
        .name(ChatUtil.translate(config.getString("wands.upgrade-wand.name")))
        .lore(lore);

    return itemBuilder.build();
  }
}
