package xyz.arcadiadevs.gensplus.models;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.utils.ActionBarUtil;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * The GeneratorsData class represents the data for generators in GensPlus. It contains a list
 * of generators and provides methods to retrieve generator information and calculate upgrade
 * prices.
 */
public record GeneratorsData(@Getter List<Generator> generators) {

  /**
   * Retrieves the generator with the specified tier.
   *
   * @param tier The tier of the generator.
   * @return The Generator object corresponding to the specified tier, or null if not found.
   */
  public Generator getGenerator(int tier) {
    return generators.stream()
        .filter(generator -> generator.tier() == tier)
        .findFirst()
        .orElse(null);
  }

  /**
   * Calculates the upgrade price from the current generator to the specified new tier.
   *
   * @param generator The current generator.
   * @param newTier   The new tier to upgrade to.
   * @return The upgrade price from the current generator to the new tier.
   * @throws NoSuchElementException if no generator with the new tier is found.
   */
  public double getUpgradePrice(Generator generator, int newTier) throws NoSuchElementException {
    return generators.stream()
        .filter(g -> g.tier() == newTier)
        .findFirst()
        .map(nextTierGen -> nextTierGen.price() - generator.price())
        .orElseThrow();
  }

  /**
   * Calculates the sell price of the specified generator.
   *
   * @param player The player who is selling the generator.
   * @param tier   The tier of the generator.
   */
  public void giveItemByTier(Player player, int tier, int amount) {
    Generator generator = getGenerator(tier);
    ItemStack item = new ItemStack(generator.blockType());
    item.setAmount(amount);
    player.getInventory().addItem(item);
  }

  /**
   * The Generator record represents a generator in GensPlus. It contains various properties
   * such as name, tier, price, sell price, speed, items, and lore.
   */
  public record Generator(String name, int tier, double price, double sellPrice, int speed,
                          ItemStack spawnItem, ItemStack blockType, List<String> lore,
                          boolean instantBreak) {

    /**
     * Gives the generator's block item to the specified player.
     *
     * @param player The player to give the item to.
     */
    public void giveItem(Player player) {
      if (player.getInventory().firstEmpty() == -1) {
        ActionBarUtil.sendActionBar(player, "&cYour inventory is full!");
        player.getWorld().dropItemNaturally(player.getLocation(), blockType);
        return;
      }

      player.getInventory().addItem(blockType);
    }

    /**
     * Drops the generator's block item naturally at the location of the specified block break
     * event.
     *
     * @param player   The player who broke the block.
     * @param location The location of the block.
     */
    public void dropItem(Player player, Location location) {
      player.getWorld().dropItemNaturally(location, blockType);
    }

    @Override
    public String toString() {
      return "Generator{"
          + "name='" + name + '\''
          + ", tier=" + tier
          + '}';
    }
  }

}

