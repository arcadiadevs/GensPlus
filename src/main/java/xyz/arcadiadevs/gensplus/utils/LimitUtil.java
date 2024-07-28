package xyz.arcadiadevs.gensplus.utils;

import org.bukkit.OfflinePlayer;
import xyz.arcadiadevs.gensplus.models.PlayerData;
import xyz.arcadiadevs.gensplus.utils.config.Config;

public class LimitUtil {

  public static int calculateCombinedLimit(OfflinePlayer player, PlayerData playerData) {
    int limitPerPlayer = 0;
    final boolean useCommands = Config.LIMIT_PER_PLAYER_USE_COMMANDS.getBoolean();
    final boolean usePermissions = Config.LIMIT_PER_PLAYER_USE_PERMISSIONS.getBoolean();

    if (usePermissions) {
      limitPerPlayer = PlayerUtil.getGeneratorLimitPerPlayer(player.getPlayer());
    }

    if (useCommands) {
      int commandLimit = playerData.getData(player.getUniqueId()).getLimit();

      if (usePermissions) {
        limitPerPlayer += commandLimit;
      } else {
        limitPerPlayer = commandLimit;
      }
    }

    return limitPerPlayer;
  }
}
