package xyz.arcadiadevs.genx.objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record Generator(String name, int tier, int speed, ItemStack spawnItem, ItemStack blockType) {

  public void giveItem(Player player) {
    player.getInventory().addItem(blockType);
  }

}
