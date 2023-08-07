package xyz.arcadiadevs.gensplus.events;

import lombok.AllArgsConstructor;
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
import xyz.arcadiadevs.gensplus.guis.UpgradeGui;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.utils.Config;
import xyz.arcadiadevs.gensplus.utils.message.Messages;
import xyz.arcadiadevs.gensplus.utils.Permissions;

@AllArgsConstructor
public class BlockInteraction implements Listener {

  private final LocationsData locationsData;

  /**
   * Handles the PlayerInteractEvent triggered when a player interacts with a block. If the block is
   * a generator block and the player is sneaking and right-clicks the block, it opens the upgrade
   * GUI.
   *
   * @param event The PlayerInteractEvent object representing the player's interaction event.
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockClick(PlayerInteractEvent event) {
    if (event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    Block block = event.getClickedBlock();
    Player player = event.getPlayer();

    if (block == null) {
      return;
    }

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !player.isSneaking()) {
      return;
    }

    LocationsData.GeneratorLocation generatorLocation = locationsData.getGeneratorLocation(block);

    if (generatorLocation == null) {
      return;
    }

    GeneratorsData.Generator generator = generatorLocation.getGeneratorObject();

    if (generator == null) {
      return;
    }

    if (GensPlus.getInstance().getConfig()
        .getBoolean(Config.GUIS_UPGRADE_GUI_ENABLED.getPath())) {
      UpgradeGui.open(player, generatorLocation, block);
    } else {
      if (generatorLocation.getPlacedBy() != player
          && !player.hasPermission(Permissions.ADMIN.getPermission())
          && !player.isOp()) {
        Messages.NOT_YOUR_GENERATOR_UPGRADE.format().send(player);
        return;
      }
      UpgradeGui.upgradeGenerator(player, generatorLocation, block);
    }
  }

  /**
   * Handles the PlayerInteractEvent with a beacon block.
   *
   * @param event The PlayerInteractEvent object representing the player interact event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void beaconInteract(PlayerInteractEvent event) {
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

  /**
   * Handles the PlayerInteractEvent triggered when a player interacts with a dragon egg.
   *
   * @param event The PlayerInteractEvent object representing the player interact event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void eggInteract(PlayerInteractEvent event) {
    final LocationsData.GeneratorLocation location =
        locationsData.getGeneratorLocation(event.getClickedBlock());

    if (location == null) {
      return;
    }

    if (event.getClickedBlock() != null
        && event.getClickedBlock().getType() == Material.DRAGON_EGG
        && location.getGeneratorObject().blockType().getType() == Material.DRAGON_EGG) {
      event.setCancelled(true);
    }
  }

}
