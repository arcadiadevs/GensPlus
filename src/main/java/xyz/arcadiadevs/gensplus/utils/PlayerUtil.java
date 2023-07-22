package xyz.arcadiadevs.gensplus.utils;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.statics.Permissions;

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
        "limit-settings.default-limit").intValue();
  }

  public static Integer getChunkRadius(Player player) {
    return getLimit(player, Permissions.CHUNK_RADIUS, "chunk-radius",
        "chunk-radius.default-radius").intValue();
  }
}
