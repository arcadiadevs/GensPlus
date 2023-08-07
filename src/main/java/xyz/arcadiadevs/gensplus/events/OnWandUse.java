package xyz.arcadiadevs.gensplus.events;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.gensplus.models.WandData;
import xyz.arcadiadevs.gensplus.utils.SellUtil;
import xyz.arcadiadevs.gensplus.utils.formatter.Formatter;
import xyz.arcadiadevs.gensplus.utils.message.Messages;

/**
 * The OnWandUse class implements the Listener interface to handle events related to the use of
 * wands in GensPlus.
 */
@AllArgsConstructor
public class OnWandUse implements Listener {

  private final WandData wandData;
  private final FileConfiguration config;

  /**
   * Handles the PlayerInteractEvent triggered when a player right clicks with a wand.
   *
   * @param event The PlayerInteractEvent object representing the player interact event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onWandUse(PlayerInteractEvent event) {

    final Player player = event.getPlayer();

    if (event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    Block clickedBlock = event.getClickedBlock();
    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

    if (clickedBlock == null) {
      return;
    }

    if (NBTEditor.contains(itemInMainHand, "sell-wand-uuid")) {
      onSellWandUse(player, itemInMainHand, clickedBlock);
    }
  }

  public void onSellWandUse(Player player, ItemStack itemInMainHand, Block clickedBlock) {
    WandData.Wand wand =
        wandData.getWand(UUID.fromString(NBTEditor.getString(itemInMainHand, "sell-wand-uuid")));

    if (player.isSneaking() && (clickedBlock.getType() == Material.CHEST
        || clickedBlock.getType() == Material.HOPPER)) {

      // Look into the chest or hopper and sell all items
      Inventory inventory = null;
      if (clickedBlock.getType() == Material.CHEST) {
        Chest chest = (Chest) clickedBlock.getState();
        inventory = chest.getBlockInventory();
      } else if (clickedBlock.getType() == Material.HOPPER) {
        Hopper hopper = (Hopper) clickedBlock.getState();
        inventory = hopper.getInventory();
      }

      if (inventory == null) {
        return;
      }

      if (inventory.getContents().length == 0) {
        Messages.NOTHING_TO_SELL.format().send(player);
        return;
      }

      SellUtil.sellWand(player, inventory, wand.getMultiplier(), wand);

      if (wand.getUses() <= 0) {
        player.getInventory().remove(itemInMainHand);
        wandData.remove(wand.getUuid());
        Messages.WAND_BROKE.format().send(player);
        return;
      }

      List<String> lore = Formatter.format(wand, config.getStringList("wands.sell-wand.lore"));

      ItemMeta meta = itemInMainHand.getItemMeta();
      meta.setLore(lore);
      itemInMainHand.setItemMeta(meta);
    }
  }
}
