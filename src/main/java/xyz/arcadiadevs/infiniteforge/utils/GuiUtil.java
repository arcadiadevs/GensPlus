package xyz.arcadiadevs.infiniteforge.utils;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.infiniteforge.guis.guilib.Gui;
import xyz.arcadiadevs.infiniteforge.guis.guilib.GuiItem;
import xyz.arcadiadevs.infiniteforge.guis.guilib.GuiItemType;

import java.util.List;
import java.util.Random;

/**
 * The GuiUtil class provides utility methods for GUI-related operations.
 */
public class GuiUtil {

  /**
   * Adds a border to the specified SGMenu with the given number of rows.
   *
   * @param menu The SGMenu to add the border to.
   * @param rows The number of rows in the menu.
   */
  public static void addBorder(Gui menu, int rows, String material) {
    for (int i = 0; i < 9; i++) {
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

    for (int i = 0; i < rows; i++) {
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

    for (int i = 0; i < rows; i++) {
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

    for (int i = (rows - 1) * 9; i < ((rows - 1) * 9) + 9; i++) {
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
   * Fills the specified SGMenu with gray stained glass panes.
   *
   * @param menu     The SGMenu to fill.
   * @param rows     The number of rows in the menu.
   * @param material The material to fill the menu with.
   */
  public static void fillInventory(SGMenu menu, int rows, String material, String displayName) {
    for (int i = 0; i < rows * 9; i++) {
      ItemStack itemStack = new ItemBuilder(XMaterial
          .matchXMaterial(material)
          .orElseThrow()
          .parseItem())
          .name(displayName)
          .build();

      menu.setButton(i, new SGButton(itemStack));
    }
  }

  // Make a function that fills the inventory with random items


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


  /**
   * Fills half of the specified SGMenu with green and red stained glass panes. The left half of the
   * menu will have red panes, the right half will have green panes, and the top and bottom rows
   * will have a combination of red and green panes.
   *
   * @param menu The SGMenu to fill.
   * @param rows The number of rows in the menu.
   */
  public static void fillHalfInventory(SGMenu menu, int rows) {
    final var greenGlassPane = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
    final var redGlassPane = XMaterial.RED_STAINED_GLASS_PANE.parseItem();

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < 9; col++) {
        int index = row * 9 + col;
        if (col < 4) {
          menu.setButton(index, new SGButton(new ItemBuilder(redGlassPane).build()));
        } else if (col > 4) {
          menu.setButton(index, new SGButton(new ItemBuilder(greenGlassPane).build()));
        } else if (row == 0) {
          menu.setButton(index, new SGButton(new ItemBuilder(greenGlassPane).build()));
        } else if (row == rows - 1) {
          menu.setButton(index, new SGButton(new ItemBuilder(redGlassPane).build()));
        } else {
          menu.setButton(index,
              new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).build()));
        }
      }
    }
  }

}
