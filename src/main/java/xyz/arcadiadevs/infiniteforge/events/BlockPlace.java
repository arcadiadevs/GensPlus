package xyz.arcadiadevs.infiniteforge.events;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;
import xyz.arcadiadevs.infiniteforge.statics.Messages;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
import xyz.arcadiadevs.infiniteforge.utils.PlayerUtil;

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
  @EventHandler
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

    final FileConfiguration config = InfiniteForge.getInstance().getConfig();

    if (firstLine.contains("Generator drop tier") && !config.getBoolean("can-drops-be-placed")) {
      event.setCancelled(true);
      return;
    }

    if (!firstLine.contains("Generator tier")) {
      return;
    }

    final Player player = event.getPlayer();

    int tier = Integer.parseInt(firstLine.split(" ")[2]);
    final int limit = PlayerUtil.getGeneratorLimit(player);
    final boolean enabled = config.getBoolean("limit-settings.enabled");

    if (locationsData.getGeneratorsCountByPlayer(player) >= limit
        && enabled) {
      ChatUtil.sendMessage(event.getPlayer(), Messages.LIMIT_REACHED
          .replace("%limit%", String.valueOf(limit)));
      event.setCancelled(true);
      return;
    }

    locationsData.createLocation(player, tier, event.getBlockPlaced());

    // Send a notification to the player
    ChatUtil.sendMessage(event.getPlayer(), Messages.SUCCESSFULLY_PLACED.replace("%tier%",
        String.valueOf(tier)));
  }

}
