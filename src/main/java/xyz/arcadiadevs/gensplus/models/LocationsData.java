package xyz.arcadiadevs.gensplus.models;

import com.awaitquality.api.spigot.chat.ChatUtil;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.holoeasy.hologram.Hologram;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.events.DropEvent;
import xyz.arcadiadevs.gensplus.models.location.SimplifiedLocation;
import xyz.arcadiadevs.gensplus.tasks.EventLoop;
import xyz.arcadiadevs.gensplus.utils.HologramsUtil;
import xyz.arcadiadevs.gensplus.utils.PlayerUtil;
import xyz.arcadiadevs.gensplus.utils.SkyblockUtil;
import xyz.arcadiadevs.gensplus.utils.TimeUtil;
import xyz.arcadiadevs.gensplus.utils.config.Config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents the data of all the generator locations in the server.
 */
public record LocationsData(CopyOnWriteArrayList<GeneratorLocation> locations) {

  private static final Map<Block, GeneratorLocation> locationMap = new ConcurrentHashMap<>();

  public LocationsData(CopyOnWriteArrayList<GeneratorLocation> locations) {
    this.locations = locations;
    // Initialize the locationMap
    locations.forEach(location -> location.getBlockLocations().forEach(block -> locationMap.put(block, location)));
  }

  public Integer getGeneratorsCountByPlayer(Player player) {
    return (int) locations.stream()
        .filter(l -> l.getPlacedBy().equals(player))
        .mapToLong(l -> l.getBlockLocations().size())
        .sum();
  }

  public Integer getGeneratorsCountByIsland(String id) {
    return (int) locations.stream()
        .filter(l -> l.islandId != null && l.islandId.equals(id))
        .mapToLong(l -> l.getBlockLocations().size())
        .sum();
  }

  /**
   * Creates a new generator location for generator.
   *
   * @param player        The player who placed the generator.
   * @param generator     The generator tier.
   * @param blockLocation The location of the generator.
   */
  public GeneratorLocation createLocation(OfflinePlayer player, int generator,
                                          Block blockLocation) {
    if (Config.DEVELOPER_OPTIONS.getBoolean()) {
      GensPlus.getInstance().getLogger().info("[DEBUG] Creating generator location:");
      GensPlus.getInstance().getLogger().info("[DEBUG] Player: " + player.getName());
      GensPlus.getInstance().getLogger().info("[DEBUG] Generator tier: " + generator);
      GensPlus.getInstance().getLogger().info("[DEBUG] Location: " + blockLocation.getLocation());
    }

    GeneratorLocation[] surroundingBlocks = {
        getGeneratorLocation(blockLocation.getRelative(0, 1, 0)),
        getGeneratorLocation(blockLocation.getRelative(0, -1, 0)),
        getGeneratorLocation(blockLocation.getRelative(-1, 0, 0)),
        getGeneratorLocation(blockLocation.getRelative(1, 0, 0)),
        getGeneratorLocation(blockLocation.getRelative(0, 0, -1)),
        getGeneratorLocation(blockLocation.getRelative(0, 0, 1))
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

    generatorBlocks.add(blockLocation);

    GeneratorLocation newLocation = new GeneratorLocation(
        player.getUniqueId().toString(),
        SkyblockUtil.getIslandId(blockLocation.getLocation()),
        generator,
        new ArrayList<>(generatorBlocks)
    );

    addLocation(newLocation);

    return newLocation;
  }

  public void addLocation(GeneratorLocation location) {
    locations.add(location);
    location.getBlockLocations().forEach(block -> locationMap.put(block, location));
  }

  /**
   * Removes generator location.
   *
   * @param location The generator location to remove.
   */
  public void removeLocation(GeneratorLocation location) {
    try {
      location.getBlockLocations().forEach(locationMap::remove);

      if (Config.HOLOGRAMS_ENABLED.getBoolean()) {
        HologramsUtil.removeHologram(location.getHologram());
      }
      locations.remove(location);
      location.getBlockLocations().forEach(locationMap::remove);
    } catch (Exception e) {
      if (Config.DEVELOPER_OPTIONS.getBoolean()) {
        e.printStackTrace();
      }
    }
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
    return locationMap.get(location);
  }

  /**
   * Represents a generator location.
   */
  @Getter
  public static class GeneratorLocation {

    private final String playerId;

    @Setter
    private String islandId;

    private final Integer generator;

    private final ArrayList<SimplifiedLocation> blockLocations;

    @Setter
    private transient String hologramId;

    /**
     * Represents a generator location.
     */
    @SuppressWarnings("unchecked")
    public GeneratorLocation(String playerId, String islandId, Integer generator,
                             List<?> blockLocations) {
      this.playerId = playerId;
      this.generator = generator;
      this.islandId = islandId;

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
          ? GensPlus.getInstance().getConfig()
          .getStringList(Config.DEFAULT_HOLOGRAM_LINES.getPath())
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

      if (!Config.HOLOGRAMS_ENABLED.getBoolean()) {
        return;
      }

      try {
        Hologram hologram = HologramsUtil.createHologram(getCenter(), lines, material);
        if (hologram != null) {
          this.hologramId = hologram.getId().toString();
        }
      } catch (Exception e) {
        if (Config.DEVELOPER_OPTIONS.getBoolean()) {
          e.printStackTrace();
        }
      }
    }

    public GeneratorLocation(String playerId, Integer generator, List<?> blockLocations) {
      this(playerId, null, generator, blockLocations);
    }

    public void removeBlock(Block block) {
      blockLocations.remove(SimplifiedLocation.fromLocation(block.getLocation()));

      if (blockLocations.isEmpty()) {
        GensPlus.getInstance().getLocationsData().removeLocation(this);
      }
    }

    public void removeSimpleBlock(SimplifiedLocation location) {
      blockLocations.remove(location);

      if (blockLocations.isEmpty()) {
        GensPlus.getInstance().getLocationsData().removeLocation(this);
      }
    }

    public OfflinePlayer getPlacedBy() {
      return Bukkit.getOfflinePlayer(UUID.fromString(playerId));
    }

    public World getWorld() {
      return blockLocations.stream().findFirst().orElseThrow().getLocation().getWorld();
    }

    /**
     * Gets the center of the generator.
     *
     * @return The center of the generator.
     */
    public ArrayList<Block> getBlockLocations() {
      return blockLocations.stream()
          .map(SimplifiedLocation::getLocation)
          .map(location -> {
            if (location == null) {
              return null;
            }

            return location.getBlock();
          })
          .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<SimplifiedLocation> getSimplifiedBlockLocations() {
      return blockLocations;
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
    @SuppressWarnings("SuspiciousMethodCalls")
    public void spawn() {
      Location location = getCenter();

      OfflinePlayer player = getPlacedBy();

      if (Config.DISABLE_GENERATORS_WHEN_OFFLINE.getBoolean() && !player.isOnline()) {
        return;
      }

      if (Config.CHUNK_RADIUS_ENABLED.getBoolean() && !hasPlayer()) {
        return;
      }

      List<Item> items = new ArrayList<>();

      long multiplier = (EventLoop.getActiveEvent().event() instanceof DropEvent event
          ? event.getMultiplier() : 1);

      if (Config.HOLOGRAMS_ENABLED.getBoolean()) {
        // Drop items naturally at a single location
        for (int i = 0; i < multiplier * blockLocations.size(); i++) {
          Item item = location.getWorld().dropItemNaturally(
              location.clone().add(0.5, 1, 0.5),
              getGeneratorObject().spawnItem()
          );
          items.add(item);
        }
      } else {
        // Drop items at multiple locations with zero velocity
        blockLocations.forEach(loc -> IntStream.range(0, (int) multiplier)
            .mapToObj(i -> loc.getLocation().getWorld().dropItem(
                loc.getLocation().clone().add(0.5, 1, 0.5),
                getGeneratorObject().spawnItem()
            ))
            .forEach(item -> {
              item.setVelocity(new Vector(0, 0, 0));
              items.add(item);
            }));
      }


      final long ticks = TimeUtil.parseTime(
          GensPlus.getInstance().getConfig().getString(Config.ITEM_DESPAWN_TIME.getPath()));

      Bukkit.getScheduler()
          .runTaskLater(GensPlus.getInstance(), () -> getWorld().getEntities().stream()
              .filter(entity -> entity instanceof Item)
              .filter(items::contains)
              .forEach(Entity::remove), ticks);

    }

    /**
     * Gets the center of the generator.
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
      int r = PlayerUtil.getRadius(getPlacedBy().getPlayer());

      for (SimplifiedLocation simplifiedLocation : blockLocations) {
        Location location = simplifiedLocation.getLocation();

        // Check if there are any players in 10 block radius from the location
        int chunkRadius = (int) Math.ceil((double) r / 16); // Convert blocks to chunks (assuming each chunk is 16x16)
        for (Entity entity : location.getWorld().getNearbyEntities(location, chunkRadius * 16, 256, chunkRadius * 16)) { // Adjust the distance to chunks
          if (entity instanceof Player) {
            return true;
          }
        }
      }

      return false;
    }

    public Hologram getHologram() {
      return HologramsUtil.getHologram(hologramId);
    }

    public void setHologram(Hologram hologram) {
      if (hologram != null) {
        this.hologramId = hologram.getId().toString();
      }
    }

    @Override
    public String toString() {
      return "GeneratorLocation{" +
          "playerId='" + playerId + '\'' +
          ", islandId='" + islandId + '\'' +
          ", generator=" + generator +
          ", blockLocations=" + blockLocations +
          ", hologramId='" + hologramId + '\'' +
          '}';
    }
  }

}
