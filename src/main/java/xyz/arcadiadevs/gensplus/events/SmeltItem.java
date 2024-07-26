package xyz.arcadiadevs.gensplus.events;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.utils.PlayerUtil;
import xyz.arcadiadevs.gensplus.utils.ServerVersion;
import xyz.arcadiadevs.gensplus.utils.config.Config;

import java.util.Arrays;

/**
 * A listener class for handling events related to smelting items.
 */
public class SmeltItem implements Listener {

  /**
   * Handles inventory click events, preventing interaction with certain items.
   *
   * @param event The inventory click event.
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (Config.CAN_DROPS_BE_USED_IN_SMELTING.getBoolean()) {
      return;
    }

    if (event.getClickedInventory() == null) {
      return;
    }

    if (!isGensItem(event.getCurrentItem())) {
      return;
    }

    InventoryType[] inventoryTypes;

    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
      inventoryTypes = new InventoryType[]{
          InventoryType.FURNACE,
          InventoryType.BLAST_FURNACE,
          InventoryType.SMOKER
      };
    } else {
      inventoryTypes = new InventoryType[]{InventoryType.FURNACE};
    }

    if (!Arrays.asList(inventoryTypes).contains(event.getView().getTopInventory().getType())) {
      return;
    }

    event.setCancelled(true);
  }

  /**
   * Handles hopper transfer events, preventing certain items from being transferred.
   *
   * @param event The inventory move item event.
   */
  @EventHandler
  public void onHopperTransfer(InventoryMoveItemEvent event) {
    if (Config.CAN_DROPS_BE_USED_IN_SMELTING.getBoolean()) {
      return;
    }

    if (event.getSource().getType() == InventoryType.PLAYER) {

      InventoryType[] inventoryTypes;

      if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
        inventoryTypes = new InventoryType[]{
            InventoryType.FURNACE,
            InventoryType.BLAST_FURNACE,
            InventoryType.SMOKER
        };
      } else {
        inventoryTypes = new InventoryType[]{InventoryType.FURNACE};
      }

      if (!Arrays.asList(inventoryTypes).contains(event.getDestination().getType())) {
        return;
      }

      InventoryHolder eventHolder = event.getDestination().getHolder();
      if (!(eventHolder instanceof BlockState)) {
        return;
      }

      if (!isGensItem(event.getItem())) {
        return;
      }

      event.setCancelled(true);
    }

    if (event.getSource().getType() == InventoryType.HOPPER) {
      InventoryType[] inventoryTypes;

      if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
        inventoryTypes = new InventoryType[]{
            InventoryType.FURNACE,
            InventoryType.BLAST_FURNACE,
            InventoryType.SMOKER
        };
      } else {
        inventoryTypes = new InventoryType[]{InventoryType.FURNACE};
      }

      if (!Arrays.asList(inventoryTypes).contains(event.getDestination().getType())) {
        return;
      }

      event.setCancelled(true);
    }
  }

  /**
   * Handles inventory drag events, preventing certain items from being dragged.
   *
   * @param event The inventory drag event.
   */
  @EventHandler
  public void onInventoryDrag(InventoryDragEvent event) {
    if (Config.CAN_DROPS_BE_USED_IN_SMELTING.getBoolean()) {
      return;
    }

    InventoryType[] inventoryTypes;

    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
      inventoryTypes = new InventoryType[]{
          InventoryType.FURNACE,
          InventoryType.BLAST_FURNACE,
          InventoryType.SMOKER
      };
    } else {
      inventoryTypes = new InventoryType[]{InventoryType.FURNACE};
    }

    if (!Arrays.asList(inventoryTypes).contains(event.getView().getTopInventory().getType())) {
      return;
    }

    event.getRawSlots().forEach(slot -> {
      if (slot < event.getView().getTopInventory().getSize()) {
        if (!isGensItem(event.getOldCursor())) {
          return;
        }

        event.setCancelled(true);
      }
    });

  }

  /**
   * Handles campfire click events, preventing interaction with certain items.
   *
   * @param event The player interact event.
   */
  @EventHandler
  public void onCampfireClick(PlayerInteractEvent event) {
    if (Config.CAN_DROPS_BE_USED_IN_SMELTING.getBoolean()) {
      return;
    }

    if (ServerVersion.isServerVersionBelow(ServerVersion.V1_14)
        || event.getClickedBlock() == null
        || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (event.getClickedBlock().getType() == XMaterial.SHORT_GRASS.parseMaterial()) {
      return;
    }

    if (XMaterial.CAMPFIRE.parseMaterial()
        != XMaterial.matchXMaterial(event.getClickedBlock().getType()).parseMaterial()) {
      return;
    }

    Player player = event.getPlayer();
    if (isGensItem(PlayerUtil.getHeldItem(player))
        && PlayerUtil.getHeldItem(player) != null
        && PlayerUtil.getHeldItem(player) != XMaterial.AIR.parseItem()) {
      event.setCancelled(true);
    } else if (PlayerUtil.getOffHeldItem(player) != null
        && PlayerUtil.getOffHeldItem(player) != XMaterial.AIR.parseItem()) {
      event.setCancelled(true);
    }
  }

  /**
   * Checks if an item is a gens item based on its NBT tags.
   *
   * @param item The item to check.
   * @return True if the item is a gens item, otherwise false.
   */
  private boolean isGensItem(ItemStack item) {
    if (item == null || item.getType() == XMaterial.AIR.parseMaterial()) {
      return false;
    }

    return NBTEditor.contains(item, NBTEditor.CUSTOM_DATA, "gensplus", "spawnitem", "tier")
        || NBTEditor.contains(item, NBTEditor.CUSTOM_DATA, "gensplus", "blocktype", "tier");
  }

}
