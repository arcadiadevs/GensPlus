package xyz.arcadiadevs.infiniteforge.objects;

import com.github.unldenis.hologram.Hologram;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * The HologramsData class is responsible for storing hologram data for InfiniteForge.
 */
public class HologramsData {

  @Getter
  private final List<IfHologram> holograms;

  public HologramsData(List<IfHologram> holograms) {
    this.holograms = holograms;
  }

  /**
   * Adds a hologram data to the list.
   *
   * @param hologramData The hologram data to add.
   */
  public void addHologramData(IfHologram hologramData) {
    holograms.add(hologramData);
  }

  /**
   * Removes a hologram data from the list.
   *
   * @param hologramData The hologram data to remove.
   */
  public void removeHologramData(IfHologram hologramData) {
    holograms.remove(hologramData);
  }

  /**
   * Retrieves the hologram data for the specified block.
   *
   * @param block The block to find the hologram data for.
   * @return The HologramsData.Holograms object associated with the block, or null if not found.
   */
  @Nullable
  public IfHologram getHologramData(Block block) {
    return holograms.stream()
        .filter(h -> h.getX() == block.getX()
            && h.getY() == block.getY()
            && h.getZ() == block.getZ()
            && h.getWorld().equals(block.getWorld().getName()))
        .findFirst()
        .orElse(null);
  }

  /**
   * The Holograms class is responsible for storing hologram data for InfiniteForge.
   */
  @Getter
  @Setter
  @SuppressWarnings("checkstyle:MemberName")
  public static class IfHologram {

    private final UUID uuid;
    private final String name;
    private final int x;
    private final int y;
    private final int z;
    private final String world;
    private final String itemStack;
    private Hologram hologram;

    /**
     * Constructs a Holograms object with the specified name, x, y, z, world, itemStack, and
     * hologram.
     *
     * @param name The name of the hologram.
     * @param x The x coordinate of the hologram.
     * @param y The y coordinate of the hologram.
     * @param z The z coordinate of the hologram.
     * @param world The world of the hologram.
     * @param itemStack The itemStack of the hologram.
     * @param hologram The hologram.
     */
    public IfHologram(String name, int x, int y, int z, String world, String itemStack,
        Hologram hologram) {
      this.uuid = UUID.randomUUID();
      this.name = name;
      this.x = x;
      this.y = y;
      this.z = z;
      this.world = world;
      this.itemStack = itemStack;
      this.hologram = hologram;
    }

    public Location getLocation() {
      return new Location(Bukkit.getWorld(world), x, y, z);
    }

  }

}
