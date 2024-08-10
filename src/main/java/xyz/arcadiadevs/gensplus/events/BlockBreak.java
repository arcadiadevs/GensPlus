package xyz.arcadiadevs.gensplus.events;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.utils.ServerVersion;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.Permissions;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;

import java.util.ArrayList;

/**
 * Handles the BlockBreakEvent triggered when a player breaks a block.
 */
@AllArgsConstructor
public class BlockBreak implements Listener {

  private LocationsData locationsData;
  private GeneratorsData generatorsData;

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
        && !event.getPlayer().hasPermission(Permissions.ADMIN.getPermission())) {
      Messages.NOT_YOUR_GENERATOR_UPGRADE.format().send(event.getPlayer());
      event.setCancelled(true);
      return;
    }

    GeneratorsData.Generator generator =
        generatorsData.getGenerator(generatorLocation.getGenerator());

    final OfflinePlayer player = generatorLocation.getPlacedBy();
    final int tier = generatorLocation.getGenerator();
    ArrayList<Block> blocks = generatorLocation.getBlockLocations();

    // Generate and drop the generator item for the player
    if (GensPlus.getInstance().getConfig().getBoolean(Config.INSTANT_PICKUP.getPath())) {
      generator.giveItem((Player) player);
    } else {
      generator.dropItem(event.getPlayer(), eventBlock.getLocation());
    }

    blocks.remove(eventBlock);

    locationsData.removeLocation(generatorLocation);

    if (Config.DEVELOPER_OPTIONS.getBoolean()) {
      GensPlus.getInstance().getLogger().info("[BLOCKBREAK] 3. Removing location: " + generatorLocation);
    }

    blocks.forEach(block -> {
      LocationsData.GeneratorLocation loc = locationsData.getGeneratorLocation(block);

      if (loc != null) {
        return;
      }

      locationsData.createLocation(player, tier, block);
    });

    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)) {
      event.setDropItems(false);
    } else {
      event.setCancelled(true);
      event.getBlock().setType(XMaterial.AIR.parseMaterial());
    }

    // Send a notification to the player
    Messages.SUCCESSFULLY_DESTROYED.format().send(event.getPlayer());
  }

}
