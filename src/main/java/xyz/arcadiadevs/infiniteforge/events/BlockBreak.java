package xyz.arcadiadevs.infiniteforge.events;

import com.github.unldenis.hologram.IHologramPool;
import java.util.ArrayList;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;
import xyz.arcadiadevs.infiniteforge.statics.Messages;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;

public class BlockBreak implements Listener {

  private final LocationsData locationsData;
  private final GeneratorsData generatorsData;

  /**
   * Constructs a BlockBreak object with the specified LocationsData and GeneratorsData.
   *
   * @param locationsData  The LocationsData object containing information about block locations.
   * @param generatorsData The GeneratorsData object containing information about generators.
   */
  public BlockBreak(LocationsData locationsData, GeneratorsData generatorsData) {
    this.locationsData = locationsData;
    this.generatorsData = generatorsData;
  }

  /**
   * Handles the BlockBreakEvent triggered when a player breaks a block.
   *
   * @param event The BlockBreakEvent object representing the block break event.
   */
  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    final Block eventBlock = event.getBlock();
    final LocationsData.GeneratorLocation generatorLocation =
        locationsData.getGeneratorLocation(eventBlock);

    if (generatorLocation == null) {
      return;
    }

    GeneratorsData.Generator generator =
        generatorsData.getGenerator(generatorLocation.getGenerator());

    Player player = generatorLocation.getPlacedBy();
    int tier = generatorLocation.getGenerator();
    ArrayList<Block> blocks = generatorLocation.getBlockLocations();

    // Generate and drop the generator item for the player
    generator.dropItem(event.getPlayer(), event);

    blocks.remove(eventBlock);

    locationsData.removeLocation(generatorLocation);

    blocks.forEach(block -> {
      LocationsData.GeneratorLocation loc = locationsData.getGeneratorLocation(block);

      if (loc != null) {
        return;
      }

      locationsData.createLocation(player, tier, block);
    });

    // Send a notification to the player
    ChatUtil.sendMessage(event.getPlayer(), Messages.SUCCESSFULLY_DESTROYED);
  }

}
