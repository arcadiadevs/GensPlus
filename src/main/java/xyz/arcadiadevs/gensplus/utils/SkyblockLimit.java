package xyz.arcadiadevs.gensplus.utils;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;

public class SkyblockLimit {

  public static String getLimitSuperiorSkyblock(Player player) {
    String islandId = SuperiorSkyblockAPI.getPlayer(player.getUniqueId())
        .getIsland()
        .getUniqueId()
        .toString();

    return islandId;
  }

  public static String getLimitBentoBox(Player player) {
    String islandId = BentoBox.getInstance()
        .getIslands()
        .getIslands()
        .stream().filter(island -> island.getMemberSet()
            .stream()
            .anyMatch(member -> member.equals(player.getUniqueId()))
        )
        .findFirst()
        .orElseThrow()
        .getUniqueId();

    return islandId;
  }

  public static String getLimitIridiumSkyblock(Player player) {
    int islandIdInt = IridiumSkyblockAPI.getInstance()
        .getUser(player)
        .getIsland()
        .orElseThrow()
        .getId();

    String islandId = String.valueOf(islandIdInt);

    return islandId;
  }

}
