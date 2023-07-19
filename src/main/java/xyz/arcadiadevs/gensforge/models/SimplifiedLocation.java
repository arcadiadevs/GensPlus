package xyz.arcadiadevs.gensforge.models;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public record SimplifiedLocation(UUID world, double x, double y, double z) {

  public Location getLocation() {
    return new Location(Bukkit.getWorld(world), x, y, z);
  }

  public static SimplifiedLocation fromLocation(Location location) {
    return new SimplifiedLocation(location.getWorld().getUID(), location.getX(),
        location.getY(), location.getZ());
  }

}
