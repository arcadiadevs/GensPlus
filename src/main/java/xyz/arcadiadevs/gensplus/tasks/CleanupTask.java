package xyz.arcadiadevs.gensplus.tasks;

import lombok.AllArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;

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

      for (Block block : location.getBlockLocations()) {
        if (block == null || block.getType() != generator.blockType().getType()) {
          location.removeBlock(block);
        }
      }
    }
  }
}
