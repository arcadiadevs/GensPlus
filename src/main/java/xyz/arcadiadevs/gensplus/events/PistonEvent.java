package xyz.arcadiadevs.gensplus.events;

import lombok.AllArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import xyz.arcadiadevs.gensplus.models.LocationsData;

import java.util.List;

@AllArgsConstructor
public class PistonEvent implements Listener {

  private final LocationsData locationsData;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPistonExtend(BlockPistonExtendEvent event) {
    List<Block> blocks = event.getBlocks();
    for (Block block : blocks) {
      LocationsData.GeneratorLocation generatorLocation = locationsData.getGeneratorLocation(block);
      if (generatorLocation != null) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPistonRetract(BlockPistonRetractEvent event) {
    List<Block> blocks = event.getBlocks();
    for (Block block : blocks) {
      LocationsData.GeneratorLocation generatorLocation = locationsData.getGeneratorLocation(block);
      if (generatorLocation != null) {
        event.setCancelled(true);
        return;
      }
    }
  }

}
