package xyz.arcadiadevs.infiniteforge.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.events.DropEvent;
import xyz.arcadiadevs.infiniteforge.tasks.EventLoop;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

/**
 * The LocationsData class represents the data for generator locations in InfiniteForge. It contains
 * a list of generator locations and provides methods to add, remove, and retrieve location
 * information.
 */
public record LocationsData(@Getter List<GeneratorLocation> locations) {

  /**
   * Adds a generator location to the list.
   *
   * @param generator The generator location to add.
   */
  public void addLocation(GeneratorLocation generator) {
    locations.add(generator);
  }

  /**
   * Removes a generator location from the list.
   *
   * @param generator The generator location to remove.
   */
  public void remove(GeneratorLocation generator) {
    locations.remove(generator);
  }

  /**
   * Retrieves the generator location data for the specified block.
   *
   * @param block The block to find the generator location for.
   * @return The GeneratorLocation object associated with the block, or null if not found.
   */
  @Nullable
  public GeneratorLocation getLocationData(Block block) {
    return locations.stream()
        .filter(b -> b.getX() == block.getX()
            && b.getY() == block.getY()
            && b.getZ() == block.getZ()
            && b.getWorld().equals(block.getWorld().getName()))
        .findFirst()
        .orElse(null);
  }

  /**
   * Retrieves the generator location data for the specified block.
   *
   * @param location The block to find the generator location for.
   * @return The GeneratorLocation object associated with the block, or null if not found.
   */
  public Location getCenter(GeneratorLocation location) {
    HashSet<Block> connectedBlocks = new HashSet<>();

    traverseBlocks(location.getBlock(), location.getGenerator(), connectedBlocks, 0);

    return getCenter(location.getLocation().getWorld(), connectedBlocks);
  }

  /**
   * Retrieves the generator location data for the specified block.
   *
   * @param connectedBlocks The block to find the generator location for.
   * @return The GeneratorLocation object associated with the block, or null if not found.
   */
  public Location getCenter(World world, Set<Block> connectedBlocks) {
    double minX = Integer.MAX_VALUE;
    double minZ = Integer.MAX_VALUE;
    double maxX = Integer.MIN_VALUE;
    double maxZ = Integer.MIN_VALUE;

    for (Block block : connectedBlocks) {
      Location loc = block.getLocation();
      int x = loc.getBlockX();
      int z = loc.getBlockZ();

      minX = Math.min(minX, x);
      minZ = Math.min(minZ, z);
      maxX = Math.max(maxX, x);
      maxZ = Math.max(maxZ, z);
    }

    double centerX = (minX + maxX) / 2;
    double centerZ = (minZ + maxZ) / 2;

    // I need to block with the highest Y value
    double centerY = connectedBlocks.stream()
        .map(Block::getY)
        .max(Integer::compareTo)
        .orElse(0);

    // Retrieve the block at the calculated center coordinates
    return new Location(world, centerX + 0.5, centerY + 2, centerZ + 0.5);
  }

  public void traverseBlocks(Block block, int tier, Set<Block> connectedBlocks) {
    traverseBlocks(block, tier, connectedBlocks, null, 0);
  }

  public void traverseBlocks(Block block, int tier, Set<Block> connectedBlocks, int depth) {
    traverseBlocks(block, tier, connectedBlocks, null, depth);
  }

  /**
   * Retrieves the generator location data for the specified block.
   *
   * @param block The block to find the generator location for.
   */
  public void traverseBlocks(Block block, int tier, Set<Block> connectedBlocks, Block filter,
                             int depth) {
    if (depth++ > 1000) {
      System.out.println("Depth is greater than 1000!");
      return;
    }

    if (block == null) {
      return;
    }

    if (block.equals(filter)) {
      System.out.println("Filter match");
      return;
    }

    if (connectedBlocks.contains(block)) {
      return;
    }

    boolean found = locations.stream()
        .anyMatch(location -> location.getGenerator() == tier
            && location.getLocation().equals(block.getLocation()));

    if (!found) {
      return;
    }

    // Add the block to the set of connected blocks
    connectedBlocks.add(block);

    // Check adjacent blocks
    traverseBlocks(block.getRelative(1, 0, 0), tier, connectedBlocks, filter, depth);
    traverseBlocks(block.getRelative(-1, 0, 0), tier, connectedBlocks, filter, depth);
    traverseBlocks(block.getRelative(0, 1, 0), tier, connectedBlocks, filter, depth);
    traverseBlocks(block.getRelative(0, -1, 0), tier, connectedBlocks, filter, depth);
    traverseBlocks(block.getRelative(0, 0, 1), tier, connectedBlocks, filter, depth);
    traverseBlocks(block.getRelative(0, 0, -1), tier, connectedBlocks, filter, depth);
  }

  /**
   * The GeneratorLocation record represents the location data for a generator in InfiniteForge. It
   * contains properties such as player ID, generator tier, coordinates, and world.
   */
  @SuppressWarnings("checkstyle:MemberName")
  @Getter
  @AllArgsConstructor
  public static class GeneratorLocation {

    private final String playerId;
    private final Integer generator;
    private final int x;
    private final int y;
    private final int z;
    private final String world;

    @Setter
    private UUID hologramUuid;

    /**
     * Spawns the generator at the location. This method drops the generator items naturally in the
     * world and removes them after a certain time.
     */
    public void spawn() {
      HologramsData.IfHologram hologram = getHologram();

      Location location = hologram == null ? getLocation() : hologram.getLocation();

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
     * @throws NullPointerException If the generator is not found.
     */
    public GeneratorsData.Generator getGeneratorObject() {
      GeneratorsData.Generator generatorObject = InfiniteForge.getInstance()
          .getGeneratorsData()
          .getGenerator(generator);

      if (generatorObject == null) {
        throw new NullPointerException("Generator not found");
      }

      return generatorObject;
    }

    /**
     * Retrieves the hologram associated with this location.
     *
     * @return The IfHologram object corresponding to the hologram UUID.
     * @throws NullPointerException If the hologram is not found.
     */
    public HologramsData.IfHologram getHologram() {
      if (InfiniteForge.getInstance().getHologramPool() == null) {
        return null;
      }

      HologramsData.IfHologram hologram =
          InfiniteForge.getInstance().getHologramsData().getHologramData(hologramUuid);

      if (hologram == null) {
        throw new NullPointerException("Hologram not found");
      }

      return hologram;
    }

    /**
     * Retrieves the next tier generator location for upgrading.
     *
     * @return The GeneratorLocation object representing the next tier generator location.
     */
    public GeneratorLocation getNextTier() {
      return new GeneratorLocation(playerId, generator + 1, x, y, z, world, hologramUuid);
    }

    /**
     * Retrieves the block at the generator location.
     *
     * @return The Block object representing the generator block.
     */
    public Block getBlock() {
      return Bukkit.getWorld(world).getBlockAt(x, y, z);
    }

    /**
     * Retrieves the location of the generator.
     *
     * @return The Location object representing the generator location.
     */
    public Location getLocation() {
      return new Location(Bukkit.getWorld(world), x, y, z);
    }

  }

}
