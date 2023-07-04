package xyz.arcadiadevs.infiniteforge.events;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;
import xyz.arcadiadevs.infiniteforge.statics.Messages;
import xyz.arcadiadevs.infiniteforge.statics.Permissions;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;

/**
 * Handles the BlockBreakEvent triggered when a player breaks a block.
 */
@AllArgsConstructor
public class BlockBreak implements Listener {

  private final LocationsData locationsData;
  private final GeneratorsData generatorsData;

  /**
   * Handles the BlockBreakEvent triggered when a player breaks a block.
   *
   * @param event The BlockBreakEvent object representing the block break event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockBreak(BlockBreakEvent event) {
    final Block eventBlock = event.getBlock();
    final LocationsData.GeneratorLocation generatorLocation =
        locationsData.getGeneratorLocation(eventBlock);

    if (generatorLocation == null) {
      return;
    }

    if (generatorLocation.getPlacedBy() != event.getPlayer()
        && !event.getPlayer().isOp()
        && !event.getPlayer().hasPermission(Permissions.ADMIN)) {
      ChatUtil.sendMessage(event.getPlayer(), Messages.NOT_YOUR_GENERATOR_DESTROY);
      event.setCancelled(true);
      return;
    }

    GeneratorsData.Generator generator =
        generatorsData.getGenerator(generatorLocation.getGenerator());

    final OfflinePlayer player = generatorLocation.getPlacedBy();
    final int tier = generatorLocation.getGenerator();
    ArrayList<Block> blocks = generatorLocation.getBlockLocations();

    // Generate and drop the generator item for the player
    if (InfiniteForge.getInstance().getConfig().getBoolean("instant-pickup")) {
      generator.giveItem((Player) player);
    } else {
      generator.dropItem(event.getPlayer(), eventBlock.getLocation());
    }

    blocks.remove(eventBlock);

    locationsData.removeLocation(generatorLocation);

    blocks.forEach(block -> {
      LocationsData.GeneratorLocation loc = locationsData.getGeneratorLocation(block);

      if (loc != null) {
        return;
      }

      locationsData.createLocation(player, tier, block);
    });

    event.setDropItems(false);

    // Send a notification to the player
    ChatUtil.sendMessage(event.getPlayer(), Messages.SUCCESSFULLY_DESTROYED);
  }

}
