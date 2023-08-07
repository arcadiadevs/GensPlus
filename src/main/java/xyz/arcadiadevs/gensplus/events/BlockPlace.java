package xyz.arcadiadevs.gensplus.events;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import java.util.List;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.models.PlayerData;
import xyz.arcadiadevs.gensplus.utils.PlayerUtil;
import xyz.arcadiadevs.gensplus.utils.Config;
import xyz.arcadiadevs.gensplus.utils.message.Messages;

/**
 * The BlockPlace class provides functionality for handling the BlockPlaceEvent in GensPlus.
 * It handles the placement of generator blocks.
 */
@AllArgsConstructor
public class BlockPlace implements Listener {

  private final LocationsData locationsData;
  private final PlayerData playerData;
  private final FileConfiguration config;

  /**
   * Handles the BlockPlaceEvent triggered when a player places a block.
   * TODO: IridiumSkyblock.getInstance().getIslandManager().getTeamViaNameOrPlayer("").get().getName();
   *
   * @param event The BlockPlaceEvent object representing the block place event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    final Player player = event.getPlayer();
    final ItemStack item = event.getItemInHand();

    final List<String> disabledWorlds = config.getStringList(Config.DISABLED_WORLDS.getPath());

    for (String world : disabledWorlds) {
      if (event.getBlockPlaced().getWorld().getName().equals(world)) {
        Messages.CANNOT_PLACE_IN_WORLD.format("world", world).send(event.getPlayer());
        event.setCancelled(true);
        return;
      }
    }

    if (NBTEditor.contains(item, "gensplus", "spawnitem", "tier")
        && !Config.CAN_DROPS_BE_PLACED.getBoolean()) {
      event.setCancelled(true);
      return;
    }

    if (!NBTEditor.contains(item, "gensplus", "blocktype", "tier")) {
      return;
    }

    final int tier = NBTEditor.getInt(item, "gensplus", "blocktype", "tier");
    final boolean enabled = Config.LIMIT_SETTINGS_ENABLED.getBoolean();
    final boolean useCommands = Config.LIMIT_SETTINGS_USE_COMMANDS.getBoolean();
    final boolean usePermissions = Config.LIMIT_SETTINGS_USE_PERMISSIONS.getBoolean();

    if (!enabled) {
      return;
    }

    int limit = PlayerUtil.getGeneratorLimit(player);

    if (useCommands && !usePermissions) {
      limit = playerData.getData(player.getUniqueId()).getLimit();
    }

    if (locationsData.getGeneratorsCountByPlayer(player) >= limit) {
      Messages.LIMIT_REACHED.format("limit", limit).send(player);
      event.setCancelled(true);
      return;
    }

    locationsData.createLocation(player, tier, event.getBlockPlaced());

    // Send a notification to the player
    Messages.SUCCESSFULLY_PLACED.format("tier", tier).send(player);
  }

}
