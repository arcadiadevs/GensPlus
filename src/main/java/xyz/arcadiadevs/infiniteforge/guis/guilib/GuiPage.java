package xyz.arcadiadevs.infiniteforge.guis.guilib;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class GuiPage {

  private Inventory inventory;

  @Getter
  private final int rows;

  @Getter
  private final GuiItem[][] items;

  public GuiPage(int rows) {
    this.inventory = null;
    this.rows = rows;
    this.items = new GuiItem[rows][9];
  }

  public Inventory getInventory(GuiPageType type) {
    if (inventory == null) {
      inventory = Bukkit.createInventory(null, rows * 9, "");

      for (int i = 0; i < items.length; i++) {
        for (int j = 0; j < items[i].length; j++) {
          GuiItem item = items[i][j];

          if (item == null) {
            continue;
          }

          if (type == GuiPageType.LAST && item.type() == GuiItemType.NEXT) {
            continue;
          }

          if (type == GuiPageType.FIRST && item.type() == GuiItemType.PREVIOUS) {
            continue;
          }

          inventory.setItem(i * 9 + j, items[i][j].item());
        }
      }
    }

    return inventory;
  }

  public Inventory getInventory() {
    return inventory;
  }

}
