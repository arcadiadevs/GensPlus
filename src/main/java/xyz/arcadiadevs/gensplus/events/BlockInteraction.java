package xyz.arcadiadevs.gensplus.events;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.guis.UpgradeGui;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.utils.ServerVersion;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.Permissions;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;

import java.util.Arrays;

@AllArgsConstructor
public class BlockInteraction implements Listener {

  private final LocationsData locationsData;
  private final FileConfiguration config;

  /**
   * Handles the PlayerInteractEvent triggered when a player interacts with a block. If the block is
   * a generator block and the player is sneaking and right-clicks the block, it opens the upgrade
   * GUI.
   *
   * @param event The PlayerInteractEvent object representing the player's interaction event.
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockClick(PlayerInteractEvent event) {
    if (ServerVersion.isServerVersionAbove(ServerVersion.V1_8)
        && event.getHand() != EquipmentSlot.HAND) {
      return;
    }

    Block block = event.getClickedBlock();
    Player player = event.getPlayer();

    if (block == null) {
      return;
    }

    LocationsData.GeneratorLocation generatorLocation = locationsData.getGeneratorLocation(block);

    if (generatorLocation == null) {
      return;
    }

    final boolean needsSneak = Config.GENERATOR_UPGRADE_SNEAK.getBoolean();
    final String actionValue = Config.GENERATOR_UPGRADE_ACTION.getString();

    if ((needsSneak && !player.isSneaking())
        || event.getAction() != Action.valueOf(actionValue)) {
      return;
    }

    GeneratorsData.Generator generator = generatorLocation.getGeneratorObject();

    if (generator == null) {
      return;
    }

    event.setCancelled(true);

    if (Config.GUIS_UPGRADE_GUI_ENABLED.getBoolean()) {
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
        && event.getClickedBlock().getType() == XMaterial.DRAGON_EGG.parseMaterial()
        && location.getGeneratorObject().blockType().getType()
        == XMaterial.DRAGON_EGG.parseMaterial()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onItemUse(PlayerInteractEvent event) {
    final LocationsData.GeneratorLocation location =
        locationsData.getGeneratorLocation(event.getClickedBlock());

    if (location == null) {
      return;
    }

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    ItemStack[] shovels = {
      XMaterial.WOODEN_SHOVEL.parseItem(),
      XMaterial.STONE_SHOVEL.parseItem(),
      XMaterial.IRON_SHOVEL.parseItem(),
      XMaterial.GOLDEN_SHOVEL.parseItem(),
      XMaterial.DIAMOND_SHOVEL.parseItem(),
      XMaterial.NETHERITE_SHOVEL.parseItem()
    };

    ItemStack[] hoes = {
      XMaterial.WOODEN_HOE.parseItem(),
      XMaterial.STONE_HOE.parseItem(),
      XMaterial.IRON_HOE.parseItem(),
      XMaterial.GOLDEN_HOE.parseItem(),
      XMaterial.DIAMOND_HOE.parseItem(),
      XMaterial.NETHERITE_HOE.parseItem()
    };

    if (Arrays.asList(shovels).contains(event.getItem())
        || Arrays.asList(hoes).contains(event.getItem())) {
      event.setCancelled(true);
    }
  }

  /**
   * Handles the BlockFormEvent triggered when a player interacts with a concrete powder block.
   *
   * @param event The PlayerInteractEvent object representing the player interact event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onConcreteForm(BlockFormEvent event) {
    if (event.getNewState().getType().name().contains("CONCRETE")) {
      final LocationsData.GeneratorLocation location =
          locationsData.getGeneratorLocation(event.getBlock());

      if (location == null) {
        return;
      }

      event.setCancelled(true);
    }
  }

  /**
   * Handles the BlockFadeEvent triggered when a player interacts with a coral block.
   *
   * @param event The PlayerInteractEvent object representing the player interact event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onCoralForm(BlockFadeEvent event) {
    if (event.getNewState().getType().name().contains("CORAL")) {
      final LocationsData.GeneratorLocation location =
          locationsData.getGeneratorLocation(event.getBlock());

      if (location == null) {
        return;
      }

      event.setCancelled(true);
    }
  }

}
