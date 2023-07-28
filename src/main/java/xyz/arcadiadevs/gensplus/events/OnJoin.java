package xyz.arcadiadevs.gensplus.events;

import lombok.AllArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.utils.config.ConfigPaths;

/**
 * Handles the PlayerJoinEvent triggered when a player joins the server.
 */
@AllArgsConstructor
public class OnJoin implements Listener {

  private final GeneratorsData generatorsData;
  private final FileConfiguration config;

  /**
   * Handles the PlayerJoinEvent triggered when a player joins the server.
   *
   * @param event The PlayerJoinEvent object representing the player's join event.
   */
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    if (!config.getBoolean(ConfigPaths.ON_JOIN_ENABLED.getPath())) {
      return;
    }

    if (event.getPlayer().hasPlayedBefore()) {
      return;
    }

    final Player player = event.getPlayer();
    final int tier = config.getInt(ConfigPaths.ON_JOIN_GENERATOR_TIER.getPath());
    final int amount = config.getInt(ConfigPaths.ON_JOIN_GENERATOR_AMOUNT.getPath());
    generatorsData.giveItemByTier(player, tier, amount);
  }
}
