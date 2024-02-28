package xyz.arcadiadevs.gensplus.models.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

/**
 * A simplified representation of a Bukkit Location.
 * This record represents a location in a specific world with x, y, and z coordinates.
 */
public record SimplifiedLocation(UUID world, double x, double y, double z) {

  /**
   * Retrieves the Bukkit Location corresponding to this simplified location.
   *
   * @return The Bukkit Location, or null if the world is not loaded.
   */
  public Location getLocation() {
    World w = Bukkit.getWorld(world);

    if (w == null) {
      Bukkit.getLogger().severe("=============================================");
      Bukkit.getLogger().severe("This is not a bug or crash. Please read below");
      Bukkit.getLogger().severe("And make sure the world exists or remove block");
      Bukkit.getLogger().severe("data/block_data.yml to reset your gens data.");
      Bukkit.getLogger().severe("=============================================");
      Bukkit.getLogger().severe("World is null for generator location "
          + world + ", did you remove or rename your world? | Shutting down server...");
      Bukkit.shutdown();
      return null;
    }

    return new Location(w, x, y, z);
  }

  /**
   * Creates a simplified location from a Bukkit Location.
   *
   * @param location The Bukkit Location to create the simplified location from.
   * @return The simplified location.
   */
  public static SimplifiedLocation fromLocation(Location location) {
    return new SimplifiedLocation(location.getWorld().getUID(), location.getX(),
        location.getY(), location.getZ());
  }

}
