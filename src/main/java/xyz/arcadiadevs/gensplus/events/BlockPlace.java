package xyz.arcadiadevs.gensplus.events;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.utils.config.ConfigPaths;
import xyz.arcadiadevs.gensplus.utils.message.Messages;
import xyz.arcadiadevs.gensplus.utils.PlayerUtil;

/**
 * The BlockPlace class provides functionality for handling the BlockPlaceEvent in GensPlus.
 * It handles the placement of generator blocks.
 */
public class BlockPlace implements Listener {

  private final LocationsData locationsData;

  /**
   * Constructs a BlockPlace object with the specified LocationsData.
   *
   * @param locationsData The LocationsData object containing information about block locations.
   */
  public BlockPlace(LocationsData locationsData) {
    this.locationsData = locationsData;
  }

  /**
   * Handles the BlockPlaceEvent triggered when a player places a block.
   *
   * @param event The BlockPlaceEvent object representing the block place event.
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockPlace(BlockPlaceEvent event) {
    ItemStack item = event.getItemInHand();
    ItemMeta meta = item.getItemMeta();

    if (meta == null) {
      return;
    }

    if (!meta.hasLore()) {
      return;
    }

    List<String> lore = meta.getLore();

    if (lore == null || lore.size() < 1) {
      return;
    }

    String firstLine = lore.get(0);

    final FileConfiguration config = GensPlus.getInstance().getConfig();

    if (firstLine.contains("Generator drop tier") &&
        !config.getBoolean(ConfigPaths.CAN_DROPS_BE_PLACED.getPath())) {
      event.setCancelled(true);
      return;
    }

    if (!firstLine.contains("Generator tier")) {
      return;
    }

    final List<String> disabledWorlds = config.getStringList(ConfigPaths.DISABLED_WORLDS.getPath());

    for (String world : disabledWorlds) {
      if (event.getBlockPlaced().getWorld().getName().equals(world)) {
        Messages.CANNOT_PLACE_IN_WORLD.format("world", world).send(event.getPlayer());
        event.setCancelled(true);
        return;
      }
    }

    //IridiumSkyblock.getInstance().getIslandManager().getTeamViaNameOrPlayer("").get().getName();

    final Player player = event.getPlayer();

    int tier = Integer.parseInt(firstLine.split(" ")[2]);
    final int limit = PlayerUtil.getGeneratorLimit(player);
    final boolean enabled = config.getBoolean(ConfigPaths.LIMIT_SETTINGS_ENABLED.getPath());

    if (locationsData.getGeneratorsCountByPlayer(player) >= limit
        && enabled) {
      Messages.LIMIT_REACHED.format("limit", limit).send(player);
      event.setCancelled(true);
      return;
    }

    locationsData.createLocation(player, tier, event.getBlockPlaced());

    // Send a notification to the player
    Messages.SUCCESSFULLY_PLACED.format("tier", tier).send(player);
  }

}
