package xyz.arcadiadevs.gensplus.utils;

import com.cryptomorin.xseries.XMaterial;
import java.util.List;
import java.util.Random;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.utils.config.ConfigPaths;
import xyz.arcadiadevs.guilib.Gui;
import xyz.arcadiadevs.guilib.GuiItem;
import xyz.arcadiadevs.guilib.GuiItemType;
import xyz.arcadiadevs.guilib.ItemBuilder;

/**
 * The GuiUtil class provides utility methods for GUI-related operations.
 */
public class GuiUtil {

  /**
   * Adds a border to the specified SGMenu with the given number of rows.
   *
   * @param menu The SGMenu to add the border to.
   */
  public static void addBorder(Gui menu, String material) {
    ItemBuilder itemBuilder = new ItemBuilder(XMaterial.matchXMaterial(material)
        .orElse(XMaterial.WHITE_STAINED_GLASS_PANE)
        .parseItem());
    if (!material.equals("AIR")) {
      itemBuilder.name(GensPlus.getInstance().getConfig()
          .getString(ConfigPaths.GUIS_GENERATORS_GUI_BORDER_NAME.getPath()));
    }

    for (int i = 0; i < 9; i++) {
      menu.setItem(
          i,
          new GuiItem(
              GuiItemType.BORDER,
              itemBuilder.build(),
              null
          )
      );
    }

    for (int i = 0; i < menu.getRows(); i++) {
      menu.setItem(
          i * 9,
          new GuiItem(
              GuiItemType.BORDER,
              XMaterial.matchXMaterial(material)
                  .orElse(XMaterial.WHITE_STAINED_GLASS_PANE)
                  .parseItem(),
              null
          )
      );
    }

    for (int i = 0; i < menu.getRows(); i++) {
      menu.setItem(
          (i * 9) + 8,
          new GuiItem(
              GuiItemType.BORDER,
              XMaterial.matchXMaterial(material)
                  .orElse(XMaterial.WHITE_STAINED_GLASS_PANE)
                  .parseItem(),
              null
          )
      );
    }

    for (int i = (menu.getRows() - 1) * 9; i < ((menu.getRows() - 1) * 9) + 9; i++) {
      menu.setItem(
          i,
          new GuiItem(
              GuiItemType.BORDER,
              XMaterial.matchXMaterial(material)
                  .orElse(XMaterial.WHITE_STAINED_GLASS_PANE)
                  .parseItem(),
              null
          )
      );
    }
  }

  /**
   * Fills the specified SGMenu with the given material.
   *
   * @param menu     The SGMenu to fill.
   * @param materials The material to fill the SGMenu with.
   */
  @SafeVarargs
  public static void fillWithRandom(Gui menu, List<String>... materials) {
    Random random = new Random();
    int rows = menu.getRows();

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < 9; j++) {
        menu.setItem(
            i * 9 + j,
            getRandomItem(materials[random.nextInt(materials.length)])
        );
      }
    }
  }

  private static GuiItem getRandomItem(List<String> materials) {
    Random random = new Random();
    String randomMaterial = materials.get(random.nextInt(materials.size()));

    ItemStack itemstack = new ItemBuilder(XMaterial.matchXMaterial(randomMaterial)
        .orElseThrow()
        .parseItem())
        .name(" ")
        .build();
    // Replace this with your logic to parse the material string into an actual item.
    // Here, we're assuming the material string is a valid material name.
    return new GuiItem(GuiItemType.BORDER, itemstack, null);
  }

}
