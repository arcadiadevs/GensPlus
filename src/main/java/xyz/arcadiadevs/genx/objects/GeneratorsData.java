package xyz.arcadiadevs.genx.objects;

import java.util.List;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public record GeneratorsData(@Getter List<Generator> generators) {

  public Generator getGenerator(int tier) {
    return generators.stream()
        .filter(generator -> generator.tier() == tier)
        .findFirst()
        .orElse(null);
  }

  public double getUpgradePrice(Generator generator, int newTier) {
    return generators.stream()
        .filter(g -> g.tier() == newTier)
        .findFirst()
        .map(nextTierGen -> nextTierGen.price() - generator.price())
        .orElseThrow();
  }

  public record Generator(String name, int tier, double price, int speed, ItemStack spawnItem, ItemStack blockType, List<String> lore) {

    public void giveItem(Player player) {
      player.getInventory().addItem(blockType);
    }

    public void dropItem(Player player, BlockBreakEvent event) {
      player.getWorld().dropItemNaturally(event.getBlock().getLocation(), blockType);
    }

  }

}
