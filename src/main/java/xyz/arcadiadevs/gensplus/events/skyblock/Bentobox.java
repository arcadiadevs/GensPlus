package xyz.arcadiadevs.gensplus.events.skyblock;

import java.util.List;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import world.bentobox.bentobox.api.events.island.IslandDeleteEvent;
import xyz.arcadiadevs.gensplus.models.LocationsData;

@AllArgsConstructor
public class Bentobox implements Listener {

  private LocationsData locationsData;

  @EventHandler
  public void onIslandRemoveBentoBox(IslandDeleteEvent event) {
    List<LocationsData.GeneratorLocation> locations = locationsData.locations();
    locations.removeIf(location ->
        location.getIslandId().equals(event.getIsland().getUniqueId()));
  }

}
