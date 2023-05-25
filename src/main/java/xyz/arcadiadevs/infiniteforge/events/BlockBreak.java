package xyz.arcadiadevs.infiniteforge.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.arcadiadevs.infiniteforge.objects.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.objects.HologramsData;
import xyz.arcadiadevs.infiniteforge.objects.HologramsData.IfHologram;
import xyz.arcadiadevs.infiniteforge.objects.LocationsData;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;

/**
 * The BlockBreak class is responsible for handling block break events related to generator blocks
 * in InfiniteForge. It listens for BlockBreakEvents and triggers generator drop item generation,
 * removal of the block location, and sending a notification to the player.
 */
public class BlockBreak implements Listener {

  private final LocationsData locationsData;
  private final GeneratorsData generatorsData;
  private final HologramsData hologramsData;

  /**
   * Constructs a BlockBreak object with the specified LocationsData and GeneratorsData.
   *
   * @param locationsData  The LocationsData object containing information about block locations.
   * @param generatorsData The GeneratorsData object containing information about generators.
   */
  public BlockBreak(LocationsData locationsData, GeneratorsData generatorsData, HologramsData hologramsData) {
    this.locationsData = locationsData;
    this.generatorsData = generatorsData;
    this.hologramsData = hologramsData;
  }

  /**
   * Handles the BlockBreakEvent triggered when a player breaks a block.
   *
   * @param event The BlockBreakEvent object representing the block break event.
   */
  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    LocationsData.GeneratorLocation block = locationsData.getLocationData(event.getBlock());
    IfHologram holograms = hologramsData.getHologramData(event.getBlock());

    if (block == null) {
      return;
    }

    GeneratorsData.Generator generator = generatorsData.getGenerator(block.generator());

    // Generate and drop the generator item for the player
    generator.dropItem(event.getPlayer(), event);

    // Remove the block location from the data
    locationsData.remove(block);
    hologramsData.removeHologramData(holograms);

    // Send a notification to the player
    ChatUtil.sendMessage(event.getPlayer(), "&aYou have broken a generator block!");
  }
}
