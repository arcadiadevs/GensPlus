package xyz.arcadiadevs.gensplus.events;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.models.PlayerData;
import xyz.arcadiadevs.gensplus.utils.PlayerUtil;
import xyz.arcadiadevs.gensplus.utils.SkyblockUtil;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;

import java.util.List;

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
    final boolean enabled = Config.LIMIT_PER_PLAYER_ENABLED.getBoolean();
    final boolean useCommands = Config.LIMIT_PER_PLAYER_USE_COMMANDS.getBoolean();
    final boolean usePermissions = Config.LIMIT_PER_PLAYER_USE_PERMISSIONS.getBoolean();

    int limitPerPlayer = PlayerUtil.getGeneratorLimitPerPlayer(player);

    if (Config.LIMIT_PER_ISLAND_ENABLED.getBoolean()) {
      int limitPerIsland = (int) SkyblockUtil.calculateLimit(player);
      String islandId = SkyblockUtil.getIslandId(event.getBlock().getLocation());

      if (locationsData.getGeneratorsCountByIsland(islandId) >= limitPerIsland) {
        Messages.LIMIT_REACHED.format("limit", limitPerIsland).send(player);
        event.setCancelled(true);
        return;
      }
    }

    if (useCommands && !usePermissions) {
      limitPerPlayer = playerData.getData(player.getUniqueId()).getLimit();
    }

    if (Config.LIMIT_PER_ISLAND_ENABLED.getBoolean()) {
      limitPerPlayer = (int) SkyblockUtil.calculateLimit(player);
    }

    if (enabled && locationsData.getGeneratorsCountByPlayer(player) >= limitPerPlayer) {
      Messages.LIMIT_REACHED.format("limit", limitPerPlayer).send(player);
      event.setCancelled(true);
      return;
    }

    locationsData.createLocation(player, tier, event.getBlockPlaced());

    // Send a notification to the player
    Messages.SUCCESSFULLY_PLACED.format("tier", tier).send(player);
  }

}
