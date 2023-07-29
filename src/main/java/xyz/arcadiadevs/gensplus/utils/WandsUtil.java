package xyz.arcadiadevs.gensplus.utils;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.List;
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

  public static ItemStack getWand(WandData.Wand.WandType type, int uses, double multiplier) {
    WandData wandData = GensPlus.getInstance().getWandData();
    WandData.Wand wand = wandData.create(type, uses, multiplier);

    String configPrefix = "wands.sell-wand";

    List<String> lore = config.getStringList(configPrefix + ".lore");

    List<String> formattedLore = Formatter.format(wand, lore);

    final Material material = XMaterial.matchXMaterial(config.getString(configPrefix + ".material"))
        .orElseThrow()
        .parseMaterial();

    ItemBuilder itemBuilder = new ItemBuilder(material)
        .name(ChatUtil.translate(config.getString(configPrefix + ".name")))
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
