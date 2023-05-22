package xyz.arcadiadevs.infiniteforge.utils;

import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.objects.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.objects.events.SellEvent;
import xyz.arcadiadevs.infiniteforge.tasks.EventLoop;

public class SellUtil {

  private final GeneratorsData generatorsData;
  private final Player player;

  public SellUtil(GeneratorsData generatorsData, Player player) {
    this.generatorsData = generatorsData;
    this.player = player;
  }

  public void sell() {
    int totalSellAmount = 0;
    final HashMap<Player, Integer> sellAmounts = new HashMap<>();
    long multiplier = (long) (EventLoop.getActiveEvent() instanceof SellEvent
        ? EventLoop.getActiveEvent().getMultiplier()
        : 1.0);
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

      if (firstLine.contains("Generator drop tier")) {

        int tier = Integer.parseInt(firstLine.split(" ")[3]);

        final var generator = generatorsData.getGenerator(tier);

        final int itemAmount = item.getAmount();
        final double sellPrice = generator.sellPrice();
        int sellAmount = (int) (sellPrice * itemAmount * multiplier);
        totalSellAmount += sellAmount;
        player.getInventory().setItem(i, null);
        sellAmounts.put(player, sellAmounts.getOrDefault(player, 0) + sellAmount);
      }
    }

    if (totalSellAmount > 0) {
      final var economy = InfiniteForge.getInstance().getEcon();

      economy.depositPlayer(player, totalSellAmount * multiplier);
      ChatUtil.sendMessage(player, "&9InfiniteForge> &aYou sold generator drops for " + (economy.format(totalSellAmount * multiplier)) + "!");

    } else {
      ChatUtil.sendMessage(player, "&9InfiniteForge> &cYou do not have anything to sell!");
    }

    sellAmounts.remove(player);

  }

}
