package xyz.arcadiadevs.gensplus.events.skyblock;

import com.iridium.iridiumskyblock.api.IslandDeleteEvent;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.arcadiadevs.gensplus.models.LocationsData;

@AllArgsConstructor
public class IridiumSkyblock implements Listener {

  private LocationsData locationsData;

  @EventHandler
  public void onIslandRemoveIridium(IslandDeleteEvent event) {
    List<LocationsData.GeneratorLocation> locations = locationsData.locations();
    locations.removeIf(location ->
        location.getIslandId().equals(String.valueOf(event.getIsland().getId())));
  }

}
