package xyz.arcadiadevs.infiniteforge.utils;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;

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
  public static void addBorder(SGMenu menu, int rows) {

    for (int i = 0; i < 9; i++) {
      menu.setButton(
          i,
          new SGButton(new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()).build())
      );
    }

    for (int i = 0; i < rows; i++) {
      menu.setButton(
          i * 9,
          new SGButton(new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()).build())
      );
    }

    for (int i = 0; i < rows; i++) {
      menu.setButton(
          i * 9 + 8,
          new SGButton(new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()).build())
      );
    }

    for (int i = (rows - 1) * 9; i < ((rows - 1) * 9) + 9; i++) {
      menu.setButton(
          i,
          new SGButton(new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()).build())
      );
    }
  }

  /**
   * Fills the specified SGMenu with gray stained glass panes.
   *
   * @param menu The SGMenu to fill.
   * @param rows The number of rows in the menu.
   */
  public static void fillInventory(SGMenu menu, int rows) {
    for (int i = 0; i < rows * 9; i++) {
      menu.setButton(
          i,
          new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).build())
      );
    }
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
