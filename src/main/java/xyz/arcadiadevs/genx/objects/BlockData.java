package xyz.arcadiadevs.genx.objects;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import xyz.arcadiadevs.genx.GenX;

public record BlockData(String playerId, int generator, int x, int y, int z, String world) {

  public void spawn() {
    Location location = new Location(Bukkit.getWorld(world), x, y, z);
    Player player = Bukkit.getPlayer(UUID.fromString(playerId));

    if (player == null) {
      return;
    }

    Item item = location.getWorld().dropItemNaturally(
        location.clone().add(0.5, 1, 0.5),
        getGenerator().spawnItem()
    );

    // TODO: remove item in 5 minutes
  }

  public Generator getGenerator() {
    return GenX.getInstance().getGeneratorsData().getGenerator(generator);
  }

}
