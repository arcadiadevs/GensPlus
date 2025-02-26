package xyz.arcadiadevs.gensplus.events;

import lombok.AllArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.PlayerData;
import xyz.arcadiadevs.gensplus.utils.ItemUtil;
import xyz.arcadiadevs.gensplus.utils.config.Config;

/**
 * Handles the PlayerJoinEvent triggered when a player joins the server.
 */
@AllArgsConstructor
public class OnJoin implements Listener {

  private final GeneratorsData generatorsData;
  private final PlayerData playerData;
  private final FileConfiguration config;

  /**
   * Handles the PlayerJoinEvent triggered when a player joins the server.
   *
   * @param event The PlayerJoinEvent object representing the player's join event.
   */
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    ItemUtil.upgradeGens(event.getPlayer().getInventory());

    if (playerData.getData(event.getPlayer().getUniqueId()) == null) {
      playerData.create(event.getPlayer().getUniqueId(),
          Config.LIMIT_PER_PLAYER_DEFAULT_LIMIT.getInt());
    }

    if (!config.getBoolean(Config.ON_JOIN_ENABLED.getPath())) {
      return;
    }

    if (event.getPlayer().hasPlayedBefore()) {
      return;
    }

    final Player player = event.getPlayer();
    final int tier = Config.ON_JOIN_GENERATOR_TIER.getInt();
    final int amount = Config.ON_JOIN_GENERATOR_AMOUNT.getInt();
    generatorsData.giveItemByTier(player, tier, amount);
  }
}
