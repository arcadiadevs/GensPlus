package xyz.arcadiadevs.gensplus.tasks;

import lombok.AllArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.utils.SkyblockUtil;

/**
 * This class represents a Bukkit task responsible for cleaning up generator locations.
 * It extends BukkitRunnable to allow for asynchronous execution.
 */
@AllArgsConstructor
public class CleanupTask extends BukkitRunnable {

  private final LocationsData locationsData;

  /**
   * Runs the cleanup operation, removing invalid block locations for each generator.
   * Also updates the generator's island ID based on the remaining block locations.
   */
  @Override
  public void run() {
    for (LocationsData.GeneratorLocation location : locationsData.locations()) {
      if (location.getWorld().isChunkLoaded(0, 0)) {
        continue;
      }

      if (location.getBlockLocations().isEmpty()) {
        locationsData.removeLocation(location);
        continue;
      }

      GeneratorsData.Generator generator = location.getGeneratorObject();

      location.getSimplifiedBlockLocations()
          .removeIf(simplifiedLocation -> simplifiedLocation.getLocation() == null
              || simplifiedLocation.getLocation().getBlock().getType()
              != generator.blockType().getType());
    }

    updateGens();
  }

  private void updateGens() {
    for (LocationsData.GeneratorLocation location : locationsData.locations()) {
      if (location.getBlockLocations().isEmpty()) {
        continue;
      }

      Block block = location.getBlockLocations().stream().findAny().orElse(null);

      if (block == null) {
        continue;
      }

      String id = SkyblockUtil.getIslandId(block.getLocation());

      if (id == null) {
        continue;
      }

      location.setIslandId(id);
    }
  }
}
