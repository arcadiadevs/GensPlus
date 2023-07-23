package xyz.arcadiadevs.gensplus.events;

import java.sql.SQLOutput;
import lombok.AllArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import xyz.arcadiadevs.gensplus.models.LocationsData;

@AllArgsConstructor
public class BlockPhysics implements Listener {

  private final LocationsData locationsData;

  @EventHandler
  public void pistonExtend(BlockPistonExtendEvent event) {
    if (event.getBlocks().stream().anyMatch(b -> locationsData.getGeneratorLocation(b) != null)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void pistonRetract(BlockPistonRetractEvent event) {
    if (event.getBlocks().stream().anyMatch(b -> locationsData.getGeneratorLocation(b) != null)) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void blockFall(EntityChangeBlockEvent event) {
    if (locationsData.getGeneratorLocation(event.getBlock()) != null) {
      event.setCancelled(true);
    }
  }
}
