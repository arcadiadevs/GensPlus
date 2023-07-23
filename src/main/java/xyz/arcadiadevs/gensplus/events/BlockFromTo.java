package xyz.arcadiadevs.gensplus.events;

import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import xyz.arcadiadevs.gensplus.models.LocationsData;

@AllArgsConstructor
public class BlockFromTo implements Listener {

  private final LocationsData locationsData;

  @EventHandler
  public void blockFromTo(BlockFromToEvent event) {
    System.out.println("heloooooooooooooo");

    final LocationsData.GeneratorLocation location =
        locationsData.getGeneratorLocation(event.getBlock());

    if (location == null) {
      return;
    }

    event.setCancelled(true);
  }

}
