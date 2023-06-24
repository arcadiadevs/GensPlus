package xyz.arcadiadevs.infiniteforge.guis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Gui implements Listener {

  private final String title;
  private final int rows;
  private final HashMap<Integer, GuiItem> items;
  private ArrayList<Inventory> inventory;

  public Gui(String title, int rows) {
    this(title, rows, new HashMap<>());
  }

  public Gui(String title, int rows, HashMap<Integer, GuiItem> items) {
    this.title = title;
    this.items = items;
    this.rows = rows;
    this.inventory = new ArrayList<>();
  }

  public void setItem(int slot, GuiItem item) {
    items.put(slot, item);
  }

  public void removeItem(int slot) {
    items.remove(slot);
  }

  public void clearItems() {
    items.clear();
  }

  public void addItem(GuiItem item, boolean firstEmpty) {
    int maxSlot = items.keySet().stream().max(Integer::compareTo).orElse(-1);

    if (!firstEmpty && maxSlot != -1) {
      System.out.println("Adding item to first empty slot");

      for (int i = 0; i < maxSlot; i++) {
        System.out.println("Checking slot " + i);
        if (items.get(i) == null) {
          System.out.println("Slot " + i + " is empty");
          items.put(i, item);
          return;
        }
      }
    }

    System.out.printf("Adding item to slot %d%n", maxSlot + 1);

    items.put(maxSlot + 1, item);
  }

  public void addItem(GuiItem item) {
    addItem(item, false);
  }

  public enum GuiItemType {
    ITEM,
    CLOSE,
    NEXT,
    PREVIOUS,
    BORDER,
  }

  public record GuiItem(GuiItemType type, ItemStack item, Runnable listener) {

  }

  public static class GuiInventory {

    private final ArrayList<GuiItem> items;

    public GuiInventory() {
      this.items = new ArrayList<>();
    }

  }

  public void init(Plugin plugin) {
    int itemsPerInventory = rows * 9;

    // inv.size() / itemsPerInventory but round up to int
    int inventories = (int) Math.ceil((double) items.size() / itemsPerInventory);

    for (int i = 0; i < inventories; i++) {
      inventory.add(i, Bukkit.createInventory(null, rows * 9, title));
    }

    for (int i = 0; i < inventories; i++) {
      Inventory inv = inventory.get(i);
      inv.clear();

      int firstItem = i * itemsPerInventory;
      int lastItem = Math.min(firstItem + itemsPerInventory, items.size());

      for (int j = firstItem; j < lastItem; j++) {
        GuiItem item = items.get(j);

        if (item != null) {
          inv.setItem(j - firstItem, item.item);
        }
      }
    }

    for (int i = 0; i < items.size(); i++) {
      GuiItem item = items.get(i);

      if (item == null) {
        continue;
      }

      if (item.type == GuiItemType.ITEM) {
        continue;
      }

      for (Inventory inv : inventory) {
        inv.setItem(i, item.item);
      }
    }

    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  public Inventory getInventory() {
    return inventory.stream().findFirst().orElse(Bukkit.createInventory(null, rows * 9, title));
  }

  private boolean isItemOnSlot(int slot) {
    if (items.get(slot) != null) {
      return true;
    }

    int inventory = slot / (rows * 9);

    // Get all items that are not of type item.
    HashMap<Integer, GuiItem> replicatableItems = this.items.entrySet().stream()
        .filter(entry -> entry.getValue().type != GuiItemType.ITEM)
        .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);

    if (replicatableItems.entrySet().stream().anyMatch(entry -> entry.getKey() * (inventory + 1) == slot)) {
      return true;
    }

    return false;
  }

  @EventHandler
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
