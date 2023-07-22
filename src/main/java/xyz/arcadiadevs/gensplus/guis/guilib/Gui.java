package xyz.arcadiadevs.gensplus.guis.guilib;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import xyz.arcadiadevs.gensplus.GensPlus;

public class Gui implements Listener {

  private final String title;

  @Getter
  private final int rows;
  private final GensPlus instance;
  private final ArrayList<GuiPage> pages;

  public Gui(String title, int rows, GensPlus instance) {
    this.title = title;
    this.rows = rows;
    this.pages = new ArrayList<>();
    addPage();
    this.instance = instance;

    instance.getServer().getPluginManager().registerEvents(this, instance);
  }

  public void addItem(GuiItem item) {
    for (GuiPage page : pages) {
      for (int i = 0; i < page.getItems().length; i++) {
        for (int j = 0; j < page.getItems()[i].length; j++) {
          if (page.getItems()[i][j] == null) {
            page.getItems()[i][j] = item;
            return;
          }
        }
      }
    }

    GuiPage page = addPage();

    for (int i = 0; i < page.getItems().length; i++) {
      for (int j = 0; j < page.getItems()[i].length; j++) {
        if (page.getItems()[i][j] == null) {
          page.getItems()[i][j] = item;
          return;
        }
      }
    }
  }

  public void setItem(int slot, GuiItem item) {
    int page = slot / (rows * 9);
    int row = (slot - page * rows * 9) / 9;
    int column = slot - page * rows * 9 - row * 9;

    if (item.type() != GuiItemType.ITEM && page != 0) {
      throw new IllegalArgumentException(
          "Only items can be set on pages other than the first one.");
    }

    pages.get(page).getItems()[row][column] = item;
  }

  public GuiPage addPage() {
    GuiPage page = new GuiPage(rows, title);

    if (pages.size() > 0) {
      GuiPage firstPage = pages.get(0);

      for (int i = 0; i < firstPage.getItems().length; i++) {
        for (int j = 0; j < firstPage.getItems()[i].length; j++) {
          GuiItem item = firstPage.getItems()[i][j];

          if (item.type() != GuiItemType.ITEM) {
            page.getItems()[i][j] = item;
          }
        }
      }
    }

    pages.add(page);

    return page;
  }

  public Inventory getInventory() {
    return getInventory(0);
  }

  public Inventory getInventory(int page) {
    return pages.get(page)
        .getInventory(pages.size() == 1
            ? GuiPageType.SINGLE
            : page == pages.size() - 1
            ? GuiPageType.LAST
            : page == 0
            ? GuiPageType.FIRST
            : GuiPageType.NORMAL
        );
  }

  private GuiItem[] getItems() {
    return pages.stream()
        .flatMap(page -> Arrays.stream(page.getItems()))
        .flatMap(Arrays::stream)
        .toArray(GuiItem[]::new);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getClickedInventory() == null) {
      return;
    }

    if (event.getCurrentItem() == null) {
      return;
    }

    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }

    Inventory inventory = event.getClickedInventory();

    GuiPage page = pages.stream()
        .filter(guiPage -> guiPage.getInventory() != null && guiPage.getInventory().equals(inventory))
        .findFirst()
        .orElse(null);

    if (page == null) {
      return;
    }

    GuiItem item = page.getItems()[event.getSlot() / 9][event.getSlot() % 9];

    if (item == null) {
      return;
    }

    event.setCancelled(true);

    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
      return;
    }

    switch (item.type()) {
      case ITEM, BORDER -> {
        if (item.action() == null) {
          return;
        }

        item.action().run();
      }
      case NEXT -> {
        int nextPage = pages.indexOf(page) + 1;

        if (nextPage >= pages.size()) {
          return;
        }

        player.openInventory(getInventory(nextPage));
      }
      case PREVIOUS -> {
        int previousPage = pages.indexOf(page) - 1;

        if (previousPage < 0) {
          return;
        }

        player.openInventory(getInventory(previousPage));
      }
      case CLOSE -> player.closeInventory();
      default -> throw new IllegalStateException("Unexpected value: " + item.type());
    }
  }

}
