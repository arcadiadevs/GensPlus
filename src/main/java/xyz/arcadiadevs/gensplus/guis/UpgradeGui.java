package xyz.arcadiadevs.gensplus.guis;

import com.cryptomorin.xseries.XSound;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.guis.guilib.Gui;
import xyz.arcadiadevs.gensplus.guis.guilib.GuiItem;
import xyz.arcadiadevs.gensplus.guis.guilib.GuiItemType;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.statics.Messages;
import xyz.arcadiadevs.gensplus.utils.ChatUtil;
import xyz.arcadiadevs.gensplus.utils.GuiUtil;

/**
 * The UpgradeGui class provides functionality for opening the upgrade GUI for generators in
 * GensPlus. It allows players to upgrade their generators to the next tier.
 */
public class UpgradeGui {

  private static final GensPlus instance = GensPlus.getInstance();

  public static void upgradeGenerator(Player player, LocationsData.GeneratorLocation currentLoc,
                                      Block clickedBlock) {
    final LocationsData locationsData = instance.getLocationsData();
    final GeneratorsData.Generator current = currentLoc.getGeneratorObject();
    final GeneratorsData.Generator nextGenerator =
        instance.getGeneratorsData().getGenerator(current.tier() + 1);

    if (nextGenerator == null) {
      ChatUtil.sendMessage(player, Messages.REACHED_MAX_TIER);
      return;
    }

    double upgradePrice =
        instance.getGeneratorsData().getUpgradePrice(current, nextGenerator.tier());

    if (upgradePrice > instance.getEcon().getBalance(player)) {
      ChatUtil.sendMessage(player, Messages.NOT_ENOUGH_MONEY);
      XSound.ENTITY_VILLAGER_NO.play(player);
      return;
    }

    EconomyResponse response = instance.getEcon().withdrawPlayer(player, upgradePrice);

    if (!response.transactionSuccess()) {
      player.sendMessage(ChatUtil.translate("An error occurred"));
      return;
    }

    ArrayList<Block> blocks = currentLoc.getBlockLocations();
    blocks.remove(clickedBlock);

    locationsData.removeLocation(currentLoc);

    blocks.forEach(block -> {
      LocationsData.GeneratorLocation loc = locationsData.getGeneratorLocation(block);

      if (loc != null) {
        return;
      }

      locationsData.createLocation(player, currentLoc.getGenerator(), block);
    });

    locationsData.createLocation(player, currentLoc.getGenerator() + 1, clickedBlock);

    clickedBlock.setType(nextGenerator.blockType().getType());

    spawnFirework(currentLoc.getCenter());

    ChatUtil.sendMessage(player, Messages.SUCCESSFULLY_UPGRADED
        .replace("%tier%", String.valueOf(currentLoc.getGenerator() + 1)));
  }

  private static void spawnFirework(Location location) {
    FileConfiguration config = GensPlus.getInstance().getConfig();
    XSound.matchXSound(config.getString("particles.sound"))
        .orElse(XSound.ENTITY_FIREWORK_ROCKET_BLAST)
        .play(location);

    if (!config.getBoolean("particles.enabled")) {
      return;
    }

    String particle = config.getString("particles.type");
    location.getWorld()
        .spawnParticle(
            Particle.valueOf(particle),
            location.add(0, -1, 0),
            70
        );
  }
}

