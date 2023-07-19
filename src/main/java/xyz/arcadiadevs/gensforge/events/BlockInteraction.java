package xyz.arcadiadevs.gensforge.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.arcadiadevs.gensforge.GensForge;
import xyz.arcadiadevs.gensforge.guis.UpgradeGui;
import xyz.arcadiadevs.gensforge.models.GeneratorsData;
import xyz.arcadiadevs.gensforge.models.LocationsData;
import xyz.arcadiadevs.gensforge.statics.Messages;
import xyz.arcadiadevs.gensforge.utils.ChatUtil;

public class BlockInteraction implements Listener {

  private final LocationsData locationsData;
  private final GeneratorsData generatorsData;

  /**
   * Constructs a ClickEvent object with the specified LocationsData and GeneratorsData.
   *
   * @param locationsData  The LocationsData object containing information about block locations.
   * @param generatorsData The GeneratorsData object containing information about generators.
   */
  public BlockInteraction(LocationsData locationsData, GeneratorsData generatorsData) {
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
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onBlockClick(PlayerInteractEvent event) {
    Block block = event.getClickedBlock();
    Player player = event.getPlayer();

    String version = Bukkit.getBukkitVersion();
    final boolean is1_8 = version.contains("1.8");

    if (!is1_8 && event.getHand().toString().equals("OFF_HAND")) {
      return;
    }

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

    if (GensForge.getInstance().getConfig().getBoolean("guis.upgrade-gui.enabled")) {
      UpgradeGui.open(player, generatorLocation, block);
    } else {
      if (generatorLocation.getPlacedBy() != player
          && !player.hasPermission("gensforge.admin")
          && !player.isOp()) {
        ChatUtil.sendMessage(player, Messages.NOT_YOUR_GENERATOR_UPGRADE);
        return;
      }
      UpgradeGui.upgradeGenerator(player, generatorLocation, block);
    }
  }

}
