package xyz.arcadiadevs.gensplus.tasks;

import lombok.AllArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.models.SimplifiedLocation;
import xyz.arcadiadevs.gensplus.utils.skyblock.SkyblockUtil;

@AllArgsConstructor
public class CleanupTask extends BukkitRunnable {

  private final LocationsData locationsData;

  /**
   * Runs this operation.
   */
  @Override
  public void run() {
    for (LocationsData.GeneratorLocation location : locationsData.locations()) {
      GeneratorsData.Generator generator = location.getGeneratorObject();

      for (SimplifiedLocation simplifiedLocation : location.getSimplifiedBlockLocations()) {
        if (simplifiedLocation.getLocation() == null
            || simplifiedLocation.getLocation().getBlock().getType() != generator.blockType().getType()) {
          location.removeSimpleBlock(simplifiedLocation);
        }
      }
    }

    updateGens();
  }

  private void updateGens() {
    for (LocationsData.GeneratorLocation location : locationsData.locations()) {
      if (location.getBlockLocations().size() == 0) {
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
