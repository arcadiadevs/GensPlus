package xyz.arcadiadevs.infiniteforge.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.objects.events.DropEvent;
import xyz.arcadiadevs.infiniteforge.tasks.EventLoop;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

/**
 * The LocationsData class represents the data for generator locations in InfiniteForge. It contains
 * a list of generator locations and provides methods to add, remove, and retrieve location
 * information.
 */
public record LocationsData(@Getter List<GeneratorLocation> generators) {

  /**
   * Adds a generator location to the list.
   *
   * @param generator The generator location to add.
   */
  public void addLocation(GeneratorLocation generator) {
    generators.add(generator);
  }

  /**
   * Removes a generator location from the list.
   *
   * @param generator The generator location to remove.
   */
  public void remove(GeneratorLocation generator) {
    generators.remove(generator);
  }

  /**
   * Retrieves the generator location data for the specified block.
   *
   * @param block The block to find the generator location for.
   * @return The GeneratorLocation object associated with the block, or null if not found.
   */
  @Nullable
  public GeneratorLocation getLocationData(Block block) {
    return generators.stream()
        .filter(b -> b.x() == block.getX()
            && b.y() == block.getY()
            && b.z() == block.getZ()
            && b.world().equals(block.getWorld().getName()))
        .findFirst()
        .orElse(null);
  }

  /**
   * The GeneratorLocation record represents the location data for a generator in InfiniteForge. It
   * contains properties such as player ID, generator tier, coordinates, and world.
   */
  public record GeneratorLocation(String playerId, int generator, int x, int y, int z,
                                  String world) {

    /**
     * Spawns the generator at the location. This method drops the generator items naturally in the
     * world and removes them after a certain time.
     */
    public void spawn() {
      Location location = new Location(Bukkit.getWorld(world), x, y, z);
      Player player = Bukkit.getPlayer(UUID.fromString(playerId));

      if (player == null) {
        return;
      }

      List<Item> items = new ArrayList<>();

      long itemsToDrop = EventLoop.getActiveEvent().event() instanceof DropEvent event
          ? event.getMultiplier() : 1;

      for (int i = 0; i < itemsToDrop; i++) {
        Item item = location.getWorld().dropItemNaturally(
            location.clone().add(0.5, 1, 0.5),
            getGeneratorObject().spawnItem()
        );
        items.add(item);
      }

      Bukkit.getScheduler()
          .runTaskLater(InfiniteForge.getInstance(), () -> items.forEach(Item::remove),
              TimeUtil.parseTime(
                  InfiniteForge.getInstance().getConfig().getString("item-despawn-time")));
    }

    /**
     * Retrieves the generator object associated with this location.
     *
     * @return The Generator object corresponding to the generator tier.
     */
    public GeneratorsData.Generator getGeneratorObject() {
      return InfiniteForge.getInstance().getGeneratorsData().getGenerator(generator);
    }

    /**
     * Retrieves the next tier generator location for upgrading.
     *
     * @return The GeneratorLocation object representing the next tier generator location.
     */
    public GeneratorLocation getNextTier() {
      return new GeneratorLocation(playerId, generator + 1, x, y, z, world);
    }

    /**
     * Retrieves the block at the generator location.
     *
     * @return The Block object representing the generator block.
     */
    public Block getBlock() {
      return Bukkit.getWorld(world).getBlockAt(x, y, z);
    }

  }

}
