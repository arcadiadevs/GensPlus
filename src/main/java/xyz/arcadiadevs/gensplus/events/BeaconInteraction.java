package xyz.arcadiadevs.gensplus.events;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.arcadiadevs.gensplus.models.LocationsData;

/**
 * Handles the PlayerInteractEvent triggered when a player interacts with a block.
 */
@AllArgsConstructor
public class BeaconInteraction implements Listener {

  private final LocationsData locationsData;

  /**
   * Handles the PlayerInteractEvent triggered when a player interacts with a block.
   *
   * @param event The PlayerInteractEvent object representing the player interact event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    final LocationsData.GeneratorLocation location =
        locationsData.getGeneratorLocation(event.getClickedBlock());

    if (location == null) {
      return;
    }

    if (event.getClickedBlock() != null
        && event.getClickedBlock().getType() == Material.BEACON
        && location.getGeneratorObject().blockType().getType() == Material.BEACON
        && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      event.setCancelled(true);
    }
  }
}
