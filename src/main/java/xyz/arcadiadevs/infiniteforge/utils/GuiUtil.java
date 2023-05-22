package xyz.arcadiadevs.infiniteforge.utils;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;

public class GuiUtil {

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

  public static void fillInventory(SGMenu menu, int rows) {
    for (int i = 0; i < rows * 9; i++) {
      menu.setButton(
          i,
          new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).build())
      );
    }
  }

  public static void fillHalfInventory(SGMenu menu, int rows) {
    int half = (int) Math.floor((double) rows * 9 / 2);
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
