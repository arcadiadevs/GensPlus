package xyz.arcadiadevs.infiniteforge.guis;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Gui implements Listener {

  private final String title;
  private final int rows;
  private final ArrayList<GuiItem> items;
  private ArrayList<Inventory> inventory;

  public Gui(String title, int rows) {
    this(title, rows, null);
  }

  public Gui(String title, int rows, ArrayList<GuiItem> items) {
    this.title = title;
    this.items = items;
    this.rows = rows;
    this.inventory = new ArrayList<>();
  }

  public void setItem(int slot, GuiItem item) {
    items.set(slot, item);
  }

  public void removeItem(int slot) {
    items.remove(slot);
  }

  public void clearItems() {
    items.clear();
  }

  public void addItem(GuiItem item, boolean firstEmpty) {
    if (firstEmpty) {
      for (int i = 0; i < items.size(); i++) {
        if (items.get(i) == null) {
          items.set(i, item);
          return;
        }
      }
    }

    items.add(item);
  }

  public void addItem(GuiItem item) {
    addItem(item, false);
  }

  public void addItems(ArrayList<GuiItem> items) {
    this.items.addAll(items);
  }

  public enum GuiItemType {
    ITEM,
    CLOSE,
    NEXT,
    PREVIOUS,
  }

  public record GuiItem(GuiItemType type, ItemStack item, Runnable listener) {

  }

  public void init(Plugin plugin) {
    int itemsPerInventory = rows * 9;

    int inventories = (int) (double) items.size() / itemsPerInventory;

    for (int i = 0; i < inventories; i++) {
      inventory.add(i, Bukkit.createInventory(null, rows * 9, title));
    }

    for (int i = 0; i < inventories; i++) {
      Inventory inv = inventory.get(i);
      inv.clear();

      for (int j = 0; j < itemsPerInventory; j++) {
        inv.setItem(j, items.get(i * itemsPerInventory + j).item);
      }
    }

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  public Inventory getInventory() {
    return inventory.stream().findFirst().orElse(Bukkit.createInventory(null, rows * 9, title));
  }

  public void onButtonClick(InventoryClickEvent event) {
    if (inventory.stream().noneMatch(inv -> inv.equals(event.getClickedInventory()))) {
      return;
    }

    GuiItem item = items.get(event.getSlot());

    if (item == null) {
      return;
    }

    switch (item.type) {
      case CLOSE -> event.getWhoClicked().closeInventory();
      case NEXT -> {
        int index = inventory.indexOf(event.getClickedInventory());

        if (index == inventory.size() - 1) {
          return;
        }

        event.getWhoClicked().openInventory(inventory.get(index + 1));
      }
      case PREVIOUS -> {
        int index = inventory.indexOf(event.getClickedInventory());

        if (index == 0) {
          return;
        }

        event.getWhoClicked().openInventory(inventory.get(index - 1));
      }
      case ITEM -> {
        if (item.listener == null) {
          throw new NullPointerException("GuiItem listener is null");
        }

        item.listener.run();
      }
      default -> throw new IllegalStateException("Unexpected value: " + item.type);
    }
  }

}
