package xyz.arcadiadevs.genx.objects;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public record Generator(String name, int tier, int speed, ItemStack spawnItem, ItemStack blockType) {

  public void giveItem(Player player) {
    player.getInventory().addItem(blockType);
  }
  public void dropItem(Player player, BlockBreakEvent event) {
    player.getWorld().dropItemNaturally(event.getBlock().getLocation(), blockType);
  }

}
