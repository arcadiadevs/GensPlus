package xyz.arcadiadevs.gensplus.models;

import com.cryptomorin.xseries.XMaterial;
import com.github.unldenis.hologram.Hologram;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.events.DropEvent;
import xyz.arcadiadevs.gensplus.tasks.EventLoop;
import xyz.arcadiadevs.gensplus.utils.ChatUtil;
import xyz.arcadiadevs.gensplus.utils.HologramsUtil;
import xyz.arcadiadevs.gensplus.utils.PlayerUtil;
import xyz.arcadiadevs.gensplus.utils.TimeUtil;

/**
 * Represents the data of all the generator locations in the server.
 */
public record LocationsData(CopyOnWriteArrayList<GeneratorLocation> locations) {

  /**
   * Represents a generator location.
   */
  public Integer getGeneratorsCountByPlayer(Player player) {
    return (int) locations.stream()
        .filter(l -> l.getPlacedBy().equals(player))
        .mapToLong(l -> l.getBlockLocations().size())
        .sum();
  }

  /**
   * Creates a new generator location for generator.
   *
   * @param player    The player who placed the generator.
   * @param generator The generator tier.
   * @param location  The location of the generator.
   */
  public GeneratorLocation createLocation(OfflinePlayer player, int generator, Block location) {
    GeneratorLocation[] surroundingBlocks = {
        getGeneratorLocation(location.getRelative(0, 1, 0)),
        getGeneratorLocation(location.getRelative(0, -1, 0)),
        getGeneratorLocation(location.getRelative(-1, 0, 0)),
        getGeneratorLocation(location.getRelative(1, 0, 0)),
        getGeneratorLocation(location.getRelative(0, 0, -1)),
        getGeneratorLocation(location.getRelative(0, 0, 1))
    };

    List<GeneratorLocation> surroundingLocations = Stream.of(surroundingBlocks)
        .filter(Objects::nonNull)
        .filter(l -> l.getGenerator() == generator)
        .filter(l -> l.getPlacedBy().equals(player))
        .toList();

    // remove all surrounding locations from the list
    removeAll(surroundingLocations);

    HashSet<Block> generatorBlocks = surroundingLocations.stream()
        .flatMap(l -> l.getBlockLocations().stream())
        .collect(Collectors.toCollection(HashSet::new));

    generatorBlocks.add(location);

    GeneratorLocation newLocation =
        new GeneratorLocation(player.getUniqueId().toString(), generator,
            new ArrayList<>(generatorBlocks));

    locations.add(newLocation);

    return newLocation;
  }

  public void addLocation(GeneratorLocation location) {
    locations.add(location);
  }

  /**
   * Removes generator location.
   *
   * @param location The generator location to remove.
   */
  public void removeLocation(GeneratorLocation location) {
    if (location.getHologram() != null) {
      HologramsUtil.removeHologram(location.getHologram());
    }
    locations.remove(location);
  }

  public void removeAll(List<GeneratorLocation> locations) {
    locations.forEach(this::removeLocation);
  }

  /**
   * Gets the generator location by the block location.
   *
   * @param location The block location.
   * @return The generator location.
   */
  public GeneratorLocation getGeneratorLocation(Block location) {
    return locations.stream()
        .filter(l -> l.getBlockLocations().contains(location))
        .findFirst()
        .orElse(null);
  }

  /**
   * Represents a generator location.
   */
  @Getter
  public static class GeneratorLocation {

    private final String playerId;
    private final Integer generator;
    private final ArrayList<SimplifiedLocation> blockLocations;

    @Setter
    private transient Hologram hologram;

    /**
     * Represents a generator location.
     */
    public GeneratorLocation(String playerId, Integer generator, List<?> blockLocations) {
      this.playerId = playerId;
      this.generator = generator;

      if (blockLocations.get(0) instanceof Block) {
        // Perform actions specific to Block
        this.blockLocations = blockLocations.stream()
            .map(b -> SimplifiedLocation.fromLocation(((Block) b).getLocation()))
            .collect(Collectors.toCollection(ArrayList::new));
      } else if (blockLocations.get(0) instanceof SimplifiedLocation) {
        // Perform actions specific to SimplifiedLocation
        this.blockLocations = (ArrayList<SimplifiedLocation>) blockLocations;
      } else {
        throw new IllegalArgumentException("Invalid blockLocations type");
      }

      Material material =
          XMaterial.matchXMaterial(getGeneratorObject().blockType().getType().toString())
              .orElseThrow(() -> new RuntimeException("Invalid item stack"))
              .parseItem()
              .getType();

      List<Map<?, ?>> generatorsConfig = GensPlus.getInstance()
          .getConfig().getMapList("generators");

      Map<?, ?> matchingGeneratorConfig = generatorsConfig.stream()
          .filter(generatorConfig -> generatorConfig.get("name")
              .equals(getGeneratorObject().name()))
          .findFirst()
          .orElse(null);

      if (matchingGeneratorConfig == null) {
        return;
      }

      List<String> lines = ((List<String>) matchingGeneratorConfig.get("hologramLines")).isEmpty()
          ? GensPlus.getInstance().getConfig().getStringList("default-hologram-lines")
          : (List<String>) matchingGeneratorConfig.get("hologramLines");

      lines = lines
          .stream()
          .map(line -> line.replace("%name%", getGeneratorObject().name()))
          .map(line -> line.replace("%tier%",
              String.valueOf(getGeneratorObject().tier())))
          .map(line -> line.replace("%speed%",
              String.valueOf(getGeneratorObject().speed())))
          .map(line -> line.replace("%spawnItem%",
              getGeneratorObject().spawnItem().getType().toString()))
          .map(line -> line.replace("%sellPrice%",
              String.valueOf(getGeneratorObject().sellPrice())))
          .map(ChatUtil::translate)
          .toList();

      this.hologram = HologramsUtil.createHologram(getCenter(), lines, material);
    }

    public void removeBlock(Block block) {
      blockLocations.remove(SimplifiedLocation.fromLocation(block.getLocation()));
    }

    public OfflinePlayer getPlacedBy() {
      return Bukkit.getOfflinePlayer(UUID.fromString(playerId));
    }

    public World getWorld() {
      return blockLocations.get(0).getLocation().getWorld();
    }

    /**
     * Gets the center of the generator.
     *
     * @return The center of the generator.
     */
    public ArrayList<Block> getBlockLocations() {
      return blockLocations.stream()
          .map(SimplifiedLocation::getLocation)
          .map(Location::getBlock)
          .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Gets the generator object.
     *
     * @return Generator object.
     */
    public GeneratorsData.Generator getGeneratorObject() {

      return GensPlus.getInstance()
          .getGeneratorsData()
          .getGenerator(generator);
    }

    public GeneratorLocation getNextTier() {
      return new GeneratorLocation(playerId, generator + 1, blockLocations);
    }

    /**
     * Spawns the items for generators.
     */
    public void spawn() {
      Location location = getCenter();

      OfflinePlayer player = getPlacedBy();

      if (!player.isOnline()) {
        return;
      }

      final GensPlus instance = GensPlus.getInstance();

      if (!hasPlayer() && instance.getConfig().getBoolean("chunk-radius.enabled")) {
        return;
      }

      List<Item> items = new ArrayList<>();

      long itemsToDrop = (EventLoop.getActiveEvent().event() instanceof DropEvent event
          ? event.getMultiplier() : 1) * getBlockLocations().size();

      if (GensPlus.getInstance().getConfig().getBoolean("holograms.enabled")) {
        for (int i = 0; i < itemsToDrop; i++) {
          Item item = location.getWorld().dropItemNaturally(
              location.clone().add(0, 1, 0),
              getGeneratorObject().spawnItem()
          );
          items.add(item);
        }
      } else {
        blockLocations.forEach(loc -> {
          Item item = loc.getLocation().getWorld().dropItem(
              loc.getLocation().clone().add(0.5, 1, 0.5),
              getGeneratorObject().spawnItem()
          );
          item.setVelocity(new Vector(0, 0, 0));
          items.add(item);
        });
      }

      final long ticks = TimeUtil.parseTime(
          GensPlus.getInstance().getConfig().getString("item-despawn-time"));

      Bukkit.getScheduler().runTaskLater(GensPlus.getInstance(), () -> {
        getWorld().getEntities().stream()
            .filter(entity -> entity instanceof Item)
            .filter(items::contains)
            .forEach(Entity::remove);
      }, ticks);

    }

    /**
     * Sells the items for generators.
     */
    public Location getCenter() {
      double minX = Integer.MAX_VALUE;
      double minZ = Integer.MAX_VALUE;
      double maxX = Integer.MIN_VALUE;
      double maxZ = Integer.MIN_VALUE;

      for (SimplifiedLocation location : blockLocations) {
        Location loc = location.getLocation();
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
      double centerY = blockLocations.stream()
          .map(SimplifiedLocation::getLocation)
          .map(Location::getBlockY)
          .max(Integer::compareTo)
          .orElse(0);

      // Retrieve the block at the calculated center coordinates
      return new Location(getWorld(), centerX + 0.5, centerY + 1, centerZ + 0.5);
    }

    private boolean hasPlayer() {
      HashSet<Chunk> chunks = new HashSet<>();
      for (SimplifiedLocation location : blockLocations) {
        Location loc = location.getLocation();
        Chunk centerChunk = loc.getChunk();
        World world = loc.getWorld();
        int radius = PlayerUtil.getChunkRadius(getPlacedBy().getPlayer());

        int cx = centerChunk.getX();
        int cz = centerChunk.getZ();

        chunks.add(loc.getChunk());

        // Add surrounding chunks
        chunks.add(world.getChunkAt(cx + radius, cz));
        chunks.add(world.getChunkAt(cx - radius, cz));
        chunks.add(world.getChunkAt(cx, cz + radius));
        chunks.add(world.getChunkAt(cx, cz - radius));

        // Add diagonal chunks
        chunks.add(world.getChunkAt(cx + radius, cz + radius));
        chunks.add(world.getChunkAt(cx + radius, cz - radius));
        chunks.add(world.getChunkAt(cx - radius, cz + radius));
        chunks.add(world.getChunkAt(cx - radius, cz - radius));

      }

      for (Chunk chunk : chunks) {
        for (Entity entity : chunk.getEntities()) {
          if (entity instanceof Player) {
            return true;
          }
        }
      }

      return false;
    }
  }

}
