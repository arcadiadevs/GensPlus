package xyz.arcadiadevs.gensplus.utils.skyblock;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.request.AddonRequestBuilder;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.objects.GensPerLevel;

/**
 * A utility class for handling various interactions with different Skyblock plugins.
 */
public class SkyblockUtil {

  /**
   * Calculate the generator limit based on the player's island level and the configuration.
   *
   * @param player The player for whom to calculate the generator limit.
   * @return The calculated generator limit.
   */
  public static long calculateLimit(Player player) {
    long level = getIslandLevel(player.getLocation(), player);
    long limit = 0;

    List<GensPerLevel> gensPerLevel = GensPerLevel.factory(
        Config.LIMIT_PER_ISLAND_GENS_PER_LEVEL.getStringList()
    );

    for (int i = 1; i <= level; i++) {
      final int finalLevel = i;

      GensPerLevel gpl = gensPerLevel.stream()
          .filter(g -> g.isIn(finalLevel))
          .findFirst()
          .orElse(null);

      limit += gpl.gain();
    }

    return limit;
  }

  /**
   * Get the ID of the island at a specific location.
   *
   * @param location The location to check for an island.
   * @return The ID of the island, or null if no island is found.
   */
  public static String getIslandId(Location location) {
    String islandId = null;

    try {
      if (Bukkit.getPluginManager().isPluginEnabled("BentoBox")) {
        islandId = getIdBentobox(location);
      } else if (Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
        islandId = getIdSuperiorSkyblock(location);
      } else if (Bukkit.getPluginManager().isPluginEnabled("IridiumSkyblock")) {
        islandId = getIdIridiumSkyblock(location);
      }
    } catch (NullPointerException ignored) {
      return islandId;
    }

    return islandId;
  }

  /**
   * Get the ID of the island using SuperiorSkyblock2.
   *
   * @param location The location to check for an island.
   * @return The ID of the island, or null if no island is found.
   */
  @Nullable
  public static String getIdSuperiorSkyblock(Location location) {
    Island island = SuperiorSkyblockAPI.getIslandAt(location);

    if (island == null) {
      return null;
    }

    return island.getUniqueId().toString();
  }

  /**
   * Get the ID of the island using BentoBox.
   *
   * @param location The location to check for an island.
   * @return The ID of the island, or null if no island is found.
   */
  @Nullable
  public static String getIdBentobox(Location location) {
    return BentoBox.getInstance()
        .getIslands()
        .getIslandAt(location)
        .orElse(null)
        .getUniqueId();
  }

  /**
   * Get the ID of the island using IridiumSkyblock.
   *
   * @param location The location to check for an island.
   * @return The ID of the island, or null if no island is found.
   */
  @Nullable
  public static String getIdIridiumSkyblock(Location location) {
    return String.valueOf(
        IridiumSkyblockAPI.getInstance()
            .getIslandViaLocation(location)
            .orElse(null)
            .getId()
    );
  }

  /**
   * Get the island level based on the location and player using the appropriate plugin.
   *
   * @param location The location to check for an island.
   * @param player   The player associated with the island.
   * @return The island level, or null if no island or level is found.
   */
  @Nullable
  public static Long getIslandLevel(Location location, Player player) {
    System.out.println(Bukkit.getPluginManager().isPluginEnabled("IridiumSkyblock"));
    if (Bukkit.getPluginManager().isPluginEnabled("BentoBox")) {
      return getLevelBentobox(location, player);
    } else if (Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
      return getLevelSuperiorSkyblock(location);
    } else if (Bukkit.getPluginManager().isPluginEnabled("IridiumSkyblock")) {
      return getLevelIridiumSkyblock(location);
    }

    return null;
  }

  /**
   * Retrieve the island level from the BentoBox plugin.
   *
   * @param location The location to check for an island.
   * @param player The player associated with the island.
   * @return The island level, or null if not available.
   */
  @Nullable
  public static Long getLevelBentobox(Location location, Player player) {
    try {
      return (long) new AddonRequestBuilder()
          .addon("Level")
          .label("island-level")
          .addMetaData("world-name", location.getWorld().getName())
          .addMetaData("player", player.getUniqueId())
          .request();
    } catch (NullPointerException ex) {
      return null;
    }
  }

  /**
   * Retrieve the island level from the SuperiorSkyblock2 plugin.
   *
   * @param location The location to check for an island.
   * @return The island level, or null if not available.
   */
  @Nullable
  public static Long getLevelSuperiorSkyblock(Location location) {
    String islandId = getIslandId(location);

    if (islandId == null) {
      return null;
    }

    return Long.valueOf(
        SuperiorSkyblockAPI.getGrid()
            .getIslandByUUID(UUID.fromString(islandId))
            .getIslandLevel()
            .toString()
    );
  }

  /**
   * Retrieve the island level from the IridiumSkyblock plugin.
   *
   * @param location The location to check for an island.
   * @return The island level, or null if not available.
   */
  @Nullable
  public static Long getLevelIridiumSkyblock(Location location) {
    String islandId = getIslandId(location);

    if (islandId == null) {
      return null;
    }

    return (long) IridiumSkyblockAPI.getInstance()
        .getIslandById(Integer.parseInt(islandId))
        .orElseThrow()
        .getLevel();
  }

}
