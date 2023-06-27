package xyz.arcadiadevs.infiniteforge.utils;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.statics.Permissions;

/**
 * Utility class for handling player-related operations and configurations.
 */
public class PlayerUtil {

  private static final FileConfiguration config = InfiniteForge.getInstance().getConfig();

  /**
   * Retrieves the limit value for a player.
   *
   * @param player The player for which to retrieve the limit.
   * @return The limit value.
   */
  private static Integer getLimit(Player player, String permissionPrefix, String use, String configKey) {
    if (config.getBoolean(use + ".use-permissions")) {

      List<String> permissions = player.getEffectivePermissions().stream()
          .map(permission -> permission.getPermission().toLowerCase())
          .filter(permission -> permission.startsWith(permissionPrefix))
          .toList();

      if (permissions.isEmpty()) {
        return config.getInt(configKey);
      }

      return permissions.stream()
          .map(permission -> permission.substring(permissionPrefix.length()))
          .map(Integer::parseInt)
          .max(Integer::compareTo)
          .orElse(config.getInt(configKey));

    }

    return config.getInt(configKey);
  }

  /**
   * Retrieves the multiplier value for a player.
   *
   * @param player The player for which to retrieve the multiplier.
   * @return The multiplier value.
   */
  public static Integer getMultiplier(Player player) {
    return getLimit(player, Permissions.SELL_MULTIPLIER, "multiplier",
        "multiplier.default-multiplier");
  }

  /**
   * Retrieves the generator limit for a player.
   *
   * @param player The player for which to retrieve the generator limit.
   * @return The generator limit value.
   */
  public static Integer getGeneratorLimit(Player player) {
    return getLimit(player, Permissions.GENERATOR_LIMIT, "limit-settings",
        "limit-settings.default-limit");
  }
}
