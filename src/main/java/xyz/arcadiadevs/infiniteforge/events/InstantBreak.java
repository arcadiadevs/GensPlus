package xyz.arcadiadevs.infiniteforge.events;

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
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;
import xyz.arcadiadevs.infiniteforge.statics.Messages;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;

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
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {

    String version = Bukkit.getBukkitVersion();
    final boolean is1_8 = version.contains("1.8");
    final Player player = event.getPlayer();

    if (!is1_8 && event.getHand().toString().equals("OFF_HAND")) {
      return;
    }

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

    if (generatorLocation.getPlacedBy() != event.getPlayer()) {
      ChatUtil.sendMessage(event.getPlayer(), Messages.NOT_YOUR_GENERATOR);
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
    if (InfiniteForge.getInstance().getConfig().getBoolean("instant-pickup")) {
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
    ChatUtil.sendMessage(event.getPlayer(), Messages.SUCCESSFULLY_DESTROYED);
    event.setCancelled(true);
  }
}
