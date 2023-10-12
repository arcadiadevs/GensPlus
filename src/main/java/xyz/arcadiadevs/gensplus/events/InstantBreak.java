package xyz.arcadiadevs.gensplus.events;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.utils.ServerVersion;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;

/**
 * Handles the PlayerInteractEvent triggered when a player interacts with a block. If the block is
 * a generator block and the player is sneaking and right-clicks the block, it opens the upgrade
 * GUI.
 */
@AllArgsConstructor
public class InstantBreak implements Listener {

  private final LocationsData locationsData;
  private final GeneratorsData generatorsData;

  /**
   * Handles the PlayerInteractEvent triggered when a player interacts with a block. If the block is
   * a generator block and the player is sneaking and right-clicks the block, it opens the upgrade
   * GUI.
   *
   * @param event The PlayerInteractEvent object representing the player's interaction event.
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (ServerVersion.isServerVersionAbove(ServerVersion.V1_8)
        && event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    final Player player = event.getPlayer();
    Block block = event.getClickedBlock();

    if (block == null) {
      return;
    }

    if (event.getAction() != Action.LEFT_CLICK_BLOCK || !player.isSneaking()) {
      return;
    }

    final LocationsData.GeneratorLocation generatorLocation =
        locationsData.getGeneratorLocation(block);

    if (generatorLocation == null) {
      return;
    }

    if (generatorLocation.getPlacedBy() != event.getPlayer() && !player.isOp()) {
      Messages.NOT_YOUR_GENERATOR_DESTROY.format().send(event.getPlayer());
      return;
    }

    GeneratorsData.Generator generator =
        generatorsData.getGenerator(generatorLocation.getGenerator());

    if (generator == null) {
      return;
    }

    if (!generator.instantBreak()) {
      return;
    }

    final int tier = generatorLocation.getGenerator();
    ArrayList<Block> blocks = generatorLocation.getBlockLocations();

    block.setType(Material.AIR);

    // Generate and drop the generator item for the player
    if (GensPlus.getInstance().getConfig().getBoolean(Config.INSTANT_PICKUP.getPath())) {
      generator.giveItem(player);
    } else {
      generator.dropItem(event.getPlayer(), block.getLocation());
    }

    blocks.remove(block);

    locationsData.removeLocation(generatorLocation);

    blocks.forEach(generatorBlock -> {
      LocationsData.GeneratorLocation loc = locationsData.getGeneratorLocation(generatorBlock);

      if (loc != null) {
        return;
      }

      locationsData.createLocation(player, tier, generatorBlock);
    });

    // Send a notification to the player
    Messages.SUCCESSFULLY_DESTROYED.format().send(event.getPlayer());
    event.setCancelled(true);
  }
}
