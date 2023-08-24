package xyz.arcadiadevs.gensplus.models.location;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record SimplifiedLocation(UUID world, double x, double y, double z) {

  public Location getLocation() {
    World w = Bukkit.getWorld(world);

    if (w == null) {
      return null;
    }

    return new Location(w, x, y, z);
  }

  public static SimplifiedLocation fromLocation(Location location) {
    return new SimplifiedLocation(location.getWorld().getUID(), location.getX(),
        location.getY(), location.getZ());
  }

}
