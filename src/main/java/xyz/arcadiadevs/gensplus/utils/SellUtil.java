package xyz.arcadiadevs.gensplus.utils;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.HashMap;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.WandData;
import xyz.arcadiadevs.gensplus.models.events.ActiveEvent;
import xyz.arcadiadevs.gensplus.models.events.SellEvent;
import xyz.arcadiadevs.gensplus.tasks.EventLoop;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;

/**
 * The SellUtil class provides utility methods for selling generator drops in GensPlus.
 * It contains a static method to sell generator drops for a player.
 */
public class SellUtil {

  /**
   * Sells generator drops for a player.
   *
   * @param player The player who wants to sell their generator drops.
   */
  public static void sellAll(Player player) {
    int totalSellAmount = 0;
    final ActiveEvent event = EventLoop.getActiveEvent();

    final double multiplier = (event.event() instanceof SellEvent
        ? event.event().getMultiplier() * PlayerUtil.getMultiplier(player)
        : 1.0 * PlayerUtil.getMultiplier(player));

    // Iterate through the player's inventory to find generator drops
    for (int i = 0; i < player.getInventory().getSize(); i++) {
      ItemStack item = player.getInventory().getItem(i);

      if (item == null) {
        continue;
      }

      if (NBTEditor.contains(item, "gensplus", "spawnitem", "tier")) {
        int tier = NBTEditor.getInt(item, "gensplus", "spawnitem", "tier");
        final GeneratorsData generatorsData = GensPlus.getInstance().getGeneratorsData();
        final GeneratorsData.Generator generator = generatorsData.getGenerator(tier);

        final double itemAmount = item.getAmount();
        final double sellPrice = generator.sellPrice();
        double sellAmount = (sellPrice * itemAmount * multiplier);
        totalSellAmount += sellAmount;
        player.getInventory().setItem(i, null);
      }
    }

    // Perform the selling operation if there are generator drops to sell
    if (totalSellAmount <= 0) {
      Messages.NOTHING_TO_SELL.format().send(player);
      return;
    }

    final Economy economy = GensPlus.getInstance().getEcon();

    economy.depositPlayer(player, totalSellAmount);

    Messages.SUCCESSFULLY_SOLD.format(
            "price", economy.format(totalSellAmount))
        .send(player);
  }

  /**
   * Sells generator drops for a player.
   *
   * @param player    The player who wants to sell their generator drops.
   * @param inventory The inventory to sell from.
   */
  public static void sellWand(Player player, Inventory inventory, double wandMultiplier,
                              WandData.Wand wand) {
    int totalSellAmount = 0;
    final ActiveEvent event = EventLoop.getActiveEvent();

    final double multiplier = (event.event() instanceof SellEvent
        ? event.event().getMultiplier() * PlayerUtil.getMultiplier(player)
        : 1.0 * PlayerUtil.getMultiplier(player)) * wandMultiplier;

    long totalItems = 0;

    for (int i = 0; i < inventory.getSize(); i++) {
      ItemStack item = inventory.getItem(i);

      if (item == null) {
        continue;
      }

      if (!NBTEditor.contains(item, "gensplus", "spawnitem", "tier")) {
        continue;
      }

      int tier = NBTEditor.getInt(item, "gensplus", "spawnitem", "tier");
      final GeneratorsData generatorsData = GensPlus.getInstance().getGeneratorsData();
      final GeneratorsData.Generator generator = generatorsData.getGenerator(tier);

      long itemAmount = item.getAmount();
      totalItems += itemAmount;
      final double sellPrice = generator.sellPrice();
      double sellAmount = (sellPrice * itemAmount * multiplier);
      totalSellAmount += (int) sellAmount;
      inventory.removeItem(item);
    }

    if (totalSellAmount <= 0) {
      Messages.NOTHING_TO_SELL.format().send(player);
      return;
    }

    final Economy economy = GensPlus.getInstance().getEcon();
    wand.setUses(wand.getUses() <= -1 ? -1 : wand.getUses() - 1);

    wand.setTotalEarned(wand.getTotalEarned() + totalSellAmount);
    wand.setTotalItemsSold(wand.getTotalItemsSold() + totalItems);

    economy.depositPlayer(player, totalSellAmount);

    Messages.SUCCESSFULLY_SOLD.format(
            "price", economy.format(totalSellAmount),
              "amount", String.valueOf(totalItems)
        )
        .send(player);

    Messages.SUCCESSFULLY_SOLD.format(
            "price", economy.format(totalSellAmount),
            "amount", String.valueOf(totalItems)
        )
        .sendInActionBar(player);
  }

  /**
   * Sells the generator drop the player is holding.
   *
   * @param player The player who wants to sell their generator drop.
   */
  public static void sellHand(Player player) {

    final ActiveEvent event = EventLoop.getActiveEvent();

    final double multiplier = (event.event() instanceof SellEvent
        ? event.event().getMultiplier() * PlayerUtil.getMultiplier(player)
        : 1.0 * PlayerUtil.getMultiplier(player));

    ItemStack item = player.getInventory().getItemInMainHand();
    final boolean isAir = item.getType() == Material.AIR;

    if (!NBTEditor.contains(item, "gensplus", "spawnitem", "tier") || isAir) {
      Messages.NOTHING_TO_SELL.format().send(player);
      return;
    }

    int tier = NBTEditor.getInt(item, "gensplus", "spawnitem", "tier");
    final GeneratorsData generatorsData = GensPlus.getInstance().getGeneratorsData();
    final GeneratorsData.Generator generator = generatorsData.getGenerator(tier);

    final double itemAmount = item.getAmount();
    final double sellPrice = generator.sellPrice();
    double sellAmount = (sellPrice * itemAmount * multiplier);

    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);

    final Economy economy = GensPlus.getInstance().getEcon();

    economy.depositPlayer(player, sellAmount);

    Messages.SUCCESSFULLY_SOLD.format(
            "price", economy.format(sellAmount))
        .send(player);

  }
}
