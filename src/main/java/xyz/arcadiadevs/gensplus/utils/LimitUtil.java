package xyz.arcadiadevs.gensplus.utils;

import org.bukkit.OfflinePlayer;
import xyz.arcadiadevs.gensplus.models.PlayerData;

public class LimitUtil {

  public static int calculateCombinedLimit(OfflinePlayer player, boolean usePermissions, boolean useCommands, PlayerData playerData) {
    int limitPerPlayer = 0;

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
