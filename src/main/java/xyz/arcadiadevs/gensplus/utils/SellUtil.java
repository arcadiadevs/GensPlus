package xyz.arcadiadevs.gensplus.utils;

import java.util.HashMap;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.events.ActiveEvent;
import xyz.arcadiadevs.gensplus.models.events.SellEvent;
import xyz.arcadiadevs.gensplus.utils.message.Messages;
import xyz.arcadiadevs.gensplus.tasks.EventLoop;

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
    final HashMap<Player, Double> sellAmounts = new HashMap<>();
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

      ItemMeta meta = item.getItemMeta();

      if (meta == null) {
        continue;
      }

      if (!meta.hasLore()) {
        continue;
      }

      List<String> lore = meta.getLore();

      if (lore == null) {
        continue;
      }

      String firstLine = lore.get(0);

      // Check if the item is a generator drop
      if (firstLine.contains("Generator drop tier")) {
        int tier = Integer.parseInt(firstLine.split(" ")[3]);
        final GeneratorsData generatorsData = GensPlus.getInstance().getGeneratorsData();
        final GeneratorsData.Generator generator = generatorsData.getGenerator(tier);

        final double itemAmount = item.getAmount();
        final double sellPrice = generator.sellPrice();
        double sellAmount = (sellPrice * itemAmount * multiplier);
        totalSellAmount += sellAmount;
        player.getInventory().setItem(i, null);
        sellAmounts.put(player, sellAmounts.getOrDefault(player, 0.0) + sellAmount);
      }
    }

    // Perform the selling operation if there are generator drops to sell
    if (totalSellAmount > 0) {
      final Economy economy = GensPlus.getInstance().getEcon();

      economy.depositPlayer(player, totalSellAmount);

      Messages.SUCCESSFULLY_SOLD.format(
              "price", economy.format(totalSellAmount))
          .send(player);

    } else {
      Messages.NOTHING_TO_SELL.format().send(player);
    }

    sellAmounts.remove(player);
  }

  /**
   * Sells generator drops for a player.
   *
   * @param player    The player who wants to sell their generator drops.
   * @param inventory The inventory to sell from.
   */
  public static void sellWand(Player player, Inventory inventory) {
    int totalSellAmount = 0;
    final HashMap<Player, Double> sellAmounts = new HashMap<>();
    final ActiveEvent event = EventLoop.getActiveEvent();

    final double multiplier = (event.event() instanceof SellEvent
        ? event.event().getMultiplier() * PlayerUtil.getMultiplier(player)
        : 1.0 * PlayerUtil.getMultiplier(player));

    for (int i = 0; i < inventory.getSize(); i++) {
      ItemStack item = inventory.getItem(i);

      if (item == null) {
        continue;
      }

      ItemMeta meta = item.getItemMeta();

      if (meta == null) {
        continue;
      }

      if (!meta.hasLore()) {
        continue;
      }

      List<String> lore = meta.getLore();

      if (lore == null) {
        continue;
      }

      String firstLine = lore.get(0);

      // Check if the item is a generator drop
      if (firstLine.contains("Generator drop tier")) {
        int tier = Integer.parseInt(firstLine.split(" ")[3]);
        final GeneratorsData generatorsData = GensPlus.getInstance().getGeneratorsData();
        final GeneratorsData.Generator generator = generatorsData.getGenerator(tier);

        final double itemAmount = item.getAmount();
        final double sellPrice = generator.sellPrice();
        double sellAmount = (sellPrice * itemAmount * multiplier);
        totalSellAmount += sellAmount;
        inventory.removeItem(item);
        sellAmounts.put(player, sellAmounts.getOrDefault(player, 0.0) + sellAmount);
      }
    }

    // Perform the selling operation if there are generator drops to sell
    if (totalSellAmount > 0) {
      final Economy economy = GensPlus.getInstance().getEcon();

      economy.depositPlayer(player, totalSellAmount);

      Messages.SUCCESSFULLY_SOLD.format(
              "price", economy.format(totalSellAmount))
          .send(player);

    } else {
      Messages.NOTHING_TO_SELL.format().send(player);
    }

    sellAmounts.remove(player);
  }


  /**
   * Sells the generator drop the player is holding.
   *
   * @param player The player who wants to sell their generator drop.
   */
  public static void sellHand(Player player) {

    final ActiveEvent event = EventLoop.getActiveEvent();

    // Determine the sell multiplier based on the active event
    final double multiplier = (event.event() instanceof SellEvent
        ? event.event().getMultiplier() * PlayerUtil.getMultiplier(player)
        : 1.0 * PlayerUtil.getMultiplier(player));

    ItemStack item = player.getInventory().getItemInMainHand();
    final boolean isAir = item.getType().isAir();

    ItemMeta meta = item.getItemMeta();

    if (isAir || meta == null || !meta.hasLore() || meta.getLore() == null) {
      Messages.NOTHING_TO_SELL.format().send(player);
      return;
    }

    List<String> lore = meta.getLore();

    if (lore == null) {
      Messages.NOTHING_TO_SELL.format().send(player);
      return;
    }

    String firstLine = lore.get(0);

    if (!firstLine.contains("Generator drop tier")) {
      Messages.NOTHING_TO_SELL.format().send(player);
      return;
    }

    int tier = Integer.parseInt(firstLine.split(" ")[3]);
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
