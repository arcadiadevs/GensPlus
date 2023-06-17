package xyz.arcadiadevs.infiniteforge.events;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;

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
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {

    String version = Bukkit.getBukkitVersion();
    final boolean is1_19 = version.contains("1.19");

    if (is1_19 && event.getHand().toString().equals("OFF_HAND")) {
      return;
    }

    final Player player = event.getPlayer();

    if (event.getAction() != Action.LEFT_CLICK_BLOCK && !player.isSneaking()) {
      return;
    }

    Block block = event.getClickedBlock();
    final LocationsData.GeneratorLocation generatorLocation =
        locationsData.getGeneratorLocation(block);

    if (generatorLocation == null) {
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
    generator.dropItem(event.getPlayer(), block.getLocation());

    blocks.remove(block);

    locationsData.removeLocation(generatorLocation);

    blocks.forEach(generatorBlock -> {
      LocationsData.GeneratorLocation loc = locationsData.getGeneratorLocation(generatorBlock);

      if (loc != null) {
        return;
      }

      locationsData.createLocation(player, tier, generatorBlock);
    });

    event.setCancelled(true);
  }
}
