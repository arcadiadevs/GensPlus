package xyz.arcadiadevs.gensplus.utils;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.utils.config.ConfigPaths;
import xyz.arcadiadevs.gensplus.utils.permission.Permissions;

/**
 * Utility class for handling player-related operations and configurations.
 */
public class PlayerUtil {

  private static final FileConfiguration config = GensPlus.getInstance().getConfig();

  /**
   * Retrieves the limit value for a player.
   *
   * @param player The player for which to retrieve the limit.
   * @return The limit value.
   */
  private static Double getLimit(Player player, String permissionPrefix, String use,
                                 String configKey) {
    if (config.getBoolean(use + ".use-permissions")) {

      List<String> permissions = player.getEffectivePermissions().stream()
          .map(permission -> permission.getPermission().toLowerCase())
          .filter(permission -> permission.startsWith(permissionPrefix))
          .toList();

      if (permissions.isEmpty()) {
        return config.getDouble(configKey);
      }

      return permissions.stream()
          .map(permission -> permission.substring(permissionPrefix.length()).replace(',', '.'))
          .map(Double::parseDouble)
          .max(Double::compareTo)
          .orElse(config.getDouble(configKey));

    }

    return config.getDouble(configKey);
  }

  /**
   * Retrieves the multiplier value for a player.
   *
   * @param player The player for which to retrieve the multiplier.
   * @return The multiplier value.
   */
  public static Double getMultiplier(Player player) {
    return getLimit(player, Permissions.SELL_MULTIPLIER.getPermission(), "multiplier",
        ConfigPaths.MULTIPLIER_DEFAULT_MULTIPLIER.getPath());
  }

  /**
   * Retrieves the generator limit for a player.
   *
   * @param player The player for which to retrieve the generator limit.
   * @return The generator limit value.
   */
  public static Integer getGeneratorLimit(Player player) {
    return getLimit(player, Permissions.GENERATOR_LIMIT.getPermission(), "limit-settings",
        ConfigPaths.LIMIT_SETTINGS_DEFAULT_LIMIT.getPath()).intValue();
  }

  public static Integer getChunkRadius(Player player) {
    return getLimit(player, Permissions.CHUNK_RADIUS.getPermission(), "chunk-radius",
        ConfigPaths.CHUNK_RADIUS_DEFAULT_RADIUS.getPath()).intValue();
  }
}
