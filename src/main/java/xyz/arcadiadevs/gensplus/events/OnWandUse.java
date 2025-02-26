package xyz.arcadiadevs.gensplus.events;

import com.awaitquality.api.spigot.chat.formatter.Formatter;
import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.gensplus.models.WandData;
import xyz.arcadiadevs.gensplus.utils.PlayerUtil;
import xyz.arcadiadevs.gensplus.utils.SellUtil;
import xyz.arcadiadevs.gensplus.utils.ServerVersion;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;

import java.util.List;
import java.util.UUID;

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

    if (ServerVersion.isServerVersionAbove(ServerVersion.V1_8)
        && event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    Block clickedBlock = event.getClickedBlock();
    ItemStack itemInMainHand = PlayerUtil.getHeldItem(player);

    if (clickedBlock == null) {
      return;
    }

    if (itemInMainHand == null
        || XMaterial.AIR.isSimilar(itemInMainHand)
        || itemInMainHand.getType() == Material.AIR) {
      return;
    }

    if (NBTEditor.contains(itemInMainHand, NBTEditor.CUSTOM_DATA, "sell-wand-uuid")) {
      final boolean needsSneak = Config.SELL_WAND_ACTION_SNEAK.getBoolean();
      final String actionValue = Config.SELL_WAND_ACTION.getString();

      if ((needsSneak && !player.isSneaking())
          || event.getAction() != Action.valueOf(actionValue)) {
        return;
      }

      onSellWandUse(player, itemInMainHand, clickedBlock);
    }
  }

  public void onSellWandUse(Player player, ItemStack itemInMainHand, Block clickedBlock) {
    WandData.Wand wand =
        wandData.getWand(UUID.fromString(NBTEditor.getString(itemInMainHand, NBTEditor.CUSTOM_DATA, "sell-wand-uuid")));

    if ((clickedBlock.getType() == Material.CHEST
        || clickedBlock.getType() == Material.HOPPER)) {

      // Look into the chest or hopper and sell all items
      Inventory inventory = null;
      if (clickedBlock.getType() == Material.CHEST) {
        Chest chest = (Chest) clickedBlock.getState();
        inventory = chest.getInventory();
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

      if (wand.getUses() == 0) {
        player.getInventory().remove(itemInMainHand);
        wandData.remove(wand.getUuid());
        Messages.WAND_BROKE.format().send(player);
        return;
      }

      List<String> lore = Formatter.format(wand, config.getStringList("wands.sell-wand.lore"));
      String name = Formatter.format(wand, config.getString("wands.sell-wand.name"));

      ItemMeta meta = itemInMainHand.getItemMeta();
      meta.setLore(lore);
      meta.setDisplayName(name);
      itemInMainHand.setItemMeta(meta);
    }
  }
}
