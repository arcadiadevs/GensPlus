package xyz.arcadiadevs.infiniteforge.utils;

import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.models.events.ActiveEvent;
import xyz.arcadiadevs.infiniteforge.models.events.SellEvent;
import xyz.arcadiadevs.infiniteforge.statics.Messages;
import xyz.arcadiadevs.infiniteforge.tasks.EventLoop;

/**
 * The SellUtil class provides utility methods for selling generator drops in InfiniteForge.
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
    final HashMap<Player, Integer> sellAmounts = new HashMap<>();
    final ActiveEvent event = EventLoop.getActiveEvent();

    // Determine the sell multiplier based on the active event
    long multiplier = (long) (event.event() instanceof SellEvent
        ? event.event().getMultiplier()
        : 1.0);

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
        final GeneratorsData generatorsData = InfiniteForge.getInstance().getGeneratorsData();
        final var generator = generatorsData.getGenerator(tier);

        final int itemAmount = item.getAmount();
        final double sellPrice = generator.sellPrice();
        int sellAmount = (int) (sellPrice * itemAmount * multiplier);
        totalSellAmount += sellAmount;
        player.getInventory().setItem(i, null);
        sellAmounts.put(player, sellAmounts.getOrDefault(player, 0) + sellAmount);
      }
    }

    // Perform the selling operation if there are generator drops to sell
    if (totalSellAmount > 0) {
      final var economy = InfiniteForge.getInstance().getEcon();

      economy.depositPlayer(player, totalSellAmount * multiplier);
      ChatUtil.sendMessage(player,
          Messages.SUCCESSFULLY_SOLD.replace("%price%", (economy.format(
              totalSellAmount * multiplier))));

    } else {
      ChatUtil.sendMessage(player, Messages.NOTHING_TO_SELL);
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
    long multiplier = (long) (event.event() instanceof SellEvent
        ? event.event().getMultiplier()
        : 1.0);

    ItemStack item = player.getInventory().getItemInMainHand();
    final boolean isAir = item.getType().isAir();

    ItemMeta meta = item.getItemMeta();

    if (isAir || meta == null || !meta.hasLore() || meta.getLore() == null) {
      ChatUtil.sendMessage(player, Messages.NOTHING_TO_SELL);
      return;
    }

    List<String> lore = meta.getLore();

    if (lore == null) {
      ChatUtil.sendMessage(player, Messages.NOTHING_TO_SELL);
      return;
    }

    String firstLine = lore.get(0);

    if (!firstLine.contains("Generator drop tier")) {
      ChatUtil.sendMessage(player, Messages.NOTHING_TO_SELL);
      return;
    }

    int tier = Integer.parseInt(firstLine.split(" ")[3]);
    final GeneratorsData generatorsData = InfiniteForge.getInstance().getGeneratorsData();
    final GeneratorsData.Generator generator = generatorsData.getGenerator(tier);

    final int itemAmount = item.getAmount();
    final double sellPrice = generator.sellPrice();
    double sellAmount = (int) (sellPrice * itemAmount * multiplier);
    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);

    final Economy economy = InfiniteForge.getInstance().getEcon();

    economy.withdrawPlayer(player, sellAmount * multiplier);
    ChatUtil.sendMessage(player,
        Messages.SUCCESSFULLY_SOLD.replace("%price%", (economy.format(
            sellAmount * multiplier))));
  }
}
