package xyz.arcadiadevs.infiniteforge.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.arcadiadevs.infiniteforge.guis.UpgradeGui;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;

/**
 * The ClickEvent class is responsible for handling player click events related to generator blocks
 * in InfiniteForge. It listens for PlayerInteractEvents and triggers the opening of the upgrade GUI
 * when a generator block is right-clicked while sneaking.
 */
public class ClickEvent implements Listener {

  private final LocationsData locationsData;
  private final GeneratorsData generatorsData;

  /**
   * Constructs a ClickEvent object with the specified LocationsData and GeneratorsData.
   *
   * @param locationsData  The LocationsData object containing information about block locations.
   * @param generatorsData The GeneratorsData object containing information about generators.
   */
  public ClickEvent(LocationsData locationsData, GeneratorsData generatorsData) {
    this.locationsData = locationsData;
    this.generatorsData = generatorsData;
  }

  /**
   * Handles the PlayerInteractEvent triggered when a player interacts with a block. If the block is
   * a generator block and the player is sneaking and right-clicks the block, it opens the upgrade
   * GUI.
   *
   * @param event The PlayerInteractEvent object representing the player's interaction event.
   */
  @EventHandler
  public void onBlockClick(PlayerInteractEvent event) {
    Block block = event.getClickedBlock();
    Player player = event.getPlayer();

    if (block == null) {
      return;
    }

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !player.isSneaking()) {
      return;
    }

    LocationsData.GeneratorLocation generatorLocation = locationsData.getLocationData(block);

    if (generatorLocation == null) {
      return;
    }

    GeneratorsData.Generator generator =
        generatorsData.getGenerator(generatorLocation.getGenerator());

    if (generator == null) {
      return;
    }

    // Open the upgrade GUI for the generator block
    UpgradeGui.open(player, generatorLocation);
  }
}
