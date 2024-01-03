package xyz.arcadiadevs.gensplus.guis;

import com.awaitquality.api.spigot.chat.ChatUtil;
import com.cryptomorin.xseries.XSound;
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
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.utils.GuiUtil;
import xyz.arcadiadevs.gensplus.utils.ServerVersion;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.Permissions;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;
import xyz.arcadiadevs.guilib.Gui;
import xyz.arcadiadevs.guilib.GuiItem;
import xyz.arcadiadevs.guilib.GuiItemType;

import java.util.ArrayList;
import java.util.List;

/**
 * The UpgradeGui class provides functionality for opening the upgrade GUI for generators in
 * GensPlus. It allows players to upgrade their generators to the next tier.
 */
public class UpgradeGui {
  private static final GensPlus instance = GensPlus.getInstance();
  private static final Economy economy = instance.getEcon();
  private static final FileConfiguration config = instance.getConfig();

  /**
   * Opens the upgrade GUI for the specified player and generator.
   *
   * @param player    The Player object for whom the GUI is being opened.
   * @param generator The GeneratorLocation representing the generator to be upgraded.
   */
  public static void open(Player player, LocationsData.GeneratorLocation generator,
                          Block clickedBlock) {
    if (generator.getPlacedBy() != player
        && !player.hasPermission(Permissions.ADMIN.getPermission())
        && !player.isOp()) {
      Messages.NOT_YOUR_GENERATOR_UPGRADE.format().send(player);
      return;
    }

    final var rows = config.getInt(Config.GUIS_UPGRADE_GUI_ROWS.getPath());
    final var menu = new Gui(
        ChatUtil.translate(config.getString(Config.GUIS_UPGRADE_GUI_TITLE.getPath())),
        rows,
        instance
    );

    final GeneratorsData.Generator current = generator.getGeneratorObject();
    GeneratorsData.Generator nextGenerator =
        instance.getGeneratorsData().getGenerator(current.tier() + 1);

    if (nextGenerator == null) {
      Messages.REACHED_MAX_TIER.format().send(player);
      return;
    }

    final ItemStack itemStackUpgradeOne = new ItemStack(nextGenerator.blockType());
    final ItemMeta itemMetaUpgradeOne = itemStackUpgradeOne.getItemMeta();

    itemMetaUpgradeOne.setDisplayName(ChatUtil.translate(config.getString(
        Config.GUIS_UPGRADE_GUI_UPGRADE_ONE_FIRST_LINE.getPath()))
    );

    double price = instance.getGeneratorsData().getUpgradePrice(current, nextGenerator.tier());

    List<String> lore =
        config.getStringList(Config.GUIS_UPGRADE_GUI_UPGRADE_ONE_LORE.getPath());

    lore = lore.stream()
        .map(s -> s.replace("%tier%", String.valueOf(current.tier())))
        .map(s -> s.replace("%speed%", String.valueOf(current.speed())))
        .map(s -> s.replace("%price%", economy.format(current.price())))
        .map(s -> s.replace("%sellPrice%", economy.format(current.sellPrice())))
        .map(s -> s.replace("%spawnItem%", current.spawnItem().getType().name()))
        .map(s -> s.replace("%blockType%", current.blockType().getType().name()))
        .map(s -> s.replace("%nextTier%", String.valueOf(nextGenerator.tier())))
        .map(s -> s.replace("%nextSpeed%", String.valueOf(nextGenerator.speed())))
        .map(s -> s.replace("%nextPrice%", economy.format(nextGenerator.price())))
        .map(s -> s.replace("%nextSellPrice%", economy.format(nextGenerator.sellPrice())))
        .map(s -> s.replace("%nextSpawnItem%", nextGenerator.spawnItem().getType().name()))
        .map(s -> s.replace("%nextBlockType%", nextGenerator.blockType().getType().name()))
        .map(s -> s.replace("%upgradePrice%", economy.format(price)))
        .map(s -> s.replace("%money%", economy.format(economy.getBalance(player))))
        .map(ChatUtil::translate)
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    itemMetaUpgradeOne.setLore(lore);
    itemStackUpgradeOne.setItemMeta(itemMetaUpgradeOne);

    final ItemStack itemStackUpgradeAll = new ItemStack(nextGenerator.blockType());
    final ItemMeta itemMetaUpgradeAll = itemStackUpgradeAll.getItemMeta();

    itemMetaUpgradeAll.setDisplayName(ChatUtil.translate(config.getString(
        Config.GUIS_UPGRADE_GUI_UPGRADE_ALL_FIRST_LINE.getPath()))
    );

    List<String> loreAll =
        config.getStringList(Config.GUIS_UPGRADE_GUI_UPGRADE_ALL_LORE.getPath());

    double priceForAll = price * generator.getBlockLocations().size();
    loreAll = loreAll.stream()
        .map(s -> s.replace("%tier%", String.valueOf(current.tier())))
        .map(s -> s.replace("%speed%", String.valueOf(current.speed())))
        .map(s -> s.replace("%price%", economy.format(current.price())))
        .map(s -> s.replace("%sellPrice%", economy.format(current.sellPrice())))
        .map(s -> s.replace("%spawnItem%", current.spawnItem().getType().name()))
        .map(s -> s.replace("%blockType%", current.blockType().getType().name()))
        .map(s -> s.replace("%nextTier%", String.valueOf(nextGenerator.tier())))
        .map(s -> s.replace("%nextSpeed%", String.valueOf(nextGenerator.speed())))
        .map(s -> s.replace("%nextPrice%", economy.format(nextGenerator.price())))
        .map(s -> s.replace("%nextSellPrice%", economy.format(nextGenerator.sellPrice())))
        .map(s -> s.replace("%nextSpawnItem%", nextGenerator.spawnItem().getType().name()))
        .map(s -> s.replace("%nextBlockType%", nextGenerator.blockType().getType().name()))
        .map(s -> s.replace("%upgradePrice%", economy.format(priceForAll)))
        .map(s -> s.replace("%money%", economy.format(economy.getBalance(player))))
        .map(ChatUtil::translate)
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    itemMetaUpgradeAll.setLore(loreAll);
    itemStackUpgradeAll.setItemMeta(itemMetaUpgradeAll);

    final List<String> itemFill = economy.has(player, price)
        ? List.of("ORANGE_STAINED_GLASS_PANE", "LIME_STAINED_GLASS_PANE")
        : List.of("ORANGE_STAINED_GLASS_PANE", "RED_STAINED_GLASS_PANE");

    GuiUtil.fillWithRandom(menu, itemFill);

    menu.setItem(13, new GuiItem(GuiItemType.ITEM, itemStackUpgradeOne, () -> {
      upgradeGenerator(player, generator, clickedBlock);
      player.closeInventory();
    }));

    player.openInventory(menu.getInventory());
  }

  /**
   * Upgrades the specified generator to the next tier for the given player.
   *
   * @param player     The Player object who is upgrading the generator.
   * @param currentLoc The GeneratorLocation representing the generator to be upgraded.
   */
  private static void upgradeAllGenerators(Player player,
                                           LocationsData.GeneratorLocation currentLoc) {

    final LocationsData locationsData = instance.getLocationsData();
    GeneratorsData.Generator current = currentLoc.getGeneratorObject();
    GeneratorsData.Generator nextGenerator =
        instance.getGeneratorsData().getGenerator(current.tier() + 1);

    if (nextGenerator == null) {
      Messages.REACHED_MAX_TIER.format().send(player);
      return;
    }

    double upgradePrice =
        instance.getGeneratorsData().getUpgradePrice(current, nextGenerator.tier())
            * currentLoc.getBlockLocations().size();

    if (upgradePrice > instance.getEcon().getBalance(player)) {
      Messages.NOT_ENOUGH_MONEY.format(
              "currentBalance", instance.getEcon().getBalance(player),
              "price", instance.getEcon().format(upgradePrice))
          .send(player);

      XSound.ENTITY_VILLAGER_NO.play(player);
      return;
    }

    EconomyResponse response = instance.getEcon().withdrawPlayer(player, upgradePrice);

    if (!response.transactionSuccess()) {
      player.sendMessage(ChatUtil.translate(
              "Sorry, we were unable to process your transaction. Reason: "
              + response.errorMessage));
      return;
    }

    LocationsData.GeneratorLocation next = currentLoc.getNextTier();

    currentLoc.getBlockLocations()
        .forEach(blockLoc -> blockLoc.setType(nextGenerator.blockType().getType()));

    locationsData.removeLocation(currentLoc);

    locationsData.addLocation(next);

    spawnFirework(currentLoc.getCenter());

    Messages.SUCCESSFULLY_UPGRADED.format("tier", nextGenerator.tier()).send(player);
  }

  /**
   * Upgrades the specified generator to the next tier for the given player.
   *
   * @param player       The Player object who is upgrading the generator.
   * @param currentLoc   The GeneratorLocation representing the generator to be upgraded.
   * @param clickedBlock The Block object that was clicked to open the upgrade GUI.
   */
  public static void upgradeGenerator(Player player, LocationsData.GeneratorLocation currentLoc,
                                      Block clickedBlock) {
    final LocationsData locationsData = instance.getLocationsData();
    final GeneratorsData.Generator current = currentLoc.getGeneratorObject();
    final GeneratorsData.Generator nextGenerator =
        instance.getGeneratorsData().getGenerator(current.tier() + 1);

    if (nextGenerator == null) {
      Messages.REACHED_MAX_TIER.format().send(player);
      return;
    }

    if (current.tier() >= 4 && current.tier() <= 11 && !player.hasPermission("gensplus.generator.tier." + nextGenerator.tier())) {
      Messages.CANNOT_UPGRADE_TIER_4_11.format(
              "tier", nextGenerator.tier(),
              "maxTier", 12)
          .send(player);
      XSound.ENTITY_VILLAGER_NO.play(player);
      return;
    }

    if (current.tier() >= 12 && current.tier() <= 19 && !player.hasPermission("gensplus.generator.tier." + nextGenerator.tier())) {
      Messages.CANNOT_UPGRADE_TIER_4_11.format(
              "tier", nextGenerator.tier(),
              "maxTier", 20)
          .send(player);
      XSound.ENTITY_VILLAGER_NO.play(player);
      return;
    }

    if (current.tier() >= 20 && current.tier() <= 27 && !player.hasPermission("gensplus.generator.tier." + nextGenerator.tier())) {
      Messages.CANNOT_UPGRADE_TIER_4_11.format(
              "tier", nextGenerator.tier(),
              "maxTier", 28)
          .send(player);
      XSound.ENTITY_VILLAGER_NO.play(player);
      return;
    }

    double upgradePrice =
        instance.getGeneratorsData().getUpgradePrice(current, nextGenerator.tier());

    if (upgradePrice > instance.getEcon().getBalance(player)) {
      Messages.NOT_ENOUGH_MONEY.format(
              "currentBalance", instance.getEcon().getBalance(player),
              "price", instance.getEcon().format(upgradePrice))
          .send(player);

      XSound.ENTITY_VILLAGER_NO.play(player);
      return;
    }

    EconomyResponse response = instance.getEcon().withdrawPlayer(player, upgradePrice);

    if (!response.transactionSuccess()) {
      player.sendMessage(ChatUtil.translate(
              "Sorry, we were unable to process your transaction. Reason: "
                      + response.errorMessage));
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

    Messages.SUCCESSFULLY_UPGRADED.format("tier", nextGenerator.tier()).send(player);
  }

  private static void spawnFirework(Location location) {
    FileConfiguration config = GensPlus.getInstance().getConfig();
    XSound.matchXSound(config.getString(Config.PARTICLES_SOUND.getPath()))
        .orElse(XSound.ENTITY_FIREWORK_ROCKET_BLAST)
        .play(location);

    if (!config.getBoolean(Config.PARTICLES_ENABLED.getPath())) {
      return;
    }

    String particle = config.getString(Config.PARTICLES_TYPE.getPath());

    // TODO: Fix 1.8 support
    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
      location.getWorld()
          .spawnParticle(
              Particle.valueOf(particle),
              location.add(0, -1, 0),
              70
          );
    }
  }
}

