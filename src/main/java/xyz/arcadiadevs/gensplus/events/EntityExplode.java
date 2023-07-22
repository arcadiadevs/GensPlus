package xyz.arcadiadevs.gensplus.events;

import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;

/**
 * Handles the EntityExplodeEvent triggered when an entity explodes.
 */
public record EntityExplode(LocationsData locationsData, GeneratorsData generatorsData)
    implements Listener {

  /**
   * Handles the EntityExplodeEvent triggered when an entity explodes.
   *
   * @param event The EntityExplodeEvent object representing the entity explode event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityExplode(EntityExplodeEvent event) {
    final List<Block> eventBlock = event.blockList();

    if (eventBlock.stream().anyMatch(block -> locationsData.getGeneratorLocation(block) != null)) {
      event.setCancelled(true);
    }
  }
}
