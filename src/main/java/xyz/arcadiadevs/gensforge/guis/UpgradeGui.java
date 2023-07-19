package xyz.arcadiadevs.gensforge.guis;

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
import xyz.arcadiadevs.gensforge.GensForge;
import xyz.arcadiadevs.gensforge.guis.guilib.Gui;
import xyz.arcadiadevs.gensforge.guis.guilib.GuiItem;
import xyz.arcadiadevs.gensforge.guis.guilib.GuiItemType;
import xyz.arcadiadevs.gensforge.models.GeneratorsData;
import xyz.arcadiadevs.gensforge.models.LocationsData;
import xyz.arcadiadevs.gensforge.statics.Messages;
import xyz.arcadiadevs.gensforge.utils.ChatUtil;
import xyz.arcadiadevs.gensforge.utils.GuiUtil;

/**
 * The UpgradeGui class provides functionality for opening the upgrade GUI for generators in
 * GensForge. It allows players to upgrade their generators to the next tier.
 */
public class UpgradeGui {

  private static final GensForge instance = GensForge.getInstance();

  /**
   * Opens the upgrade GUI for the specified player and generator.
   *
   * @param player    The Player object for whom the GUI is being opened.
   * @param generator The GeneratorLocation representing the generator to be upgraded.
   */
  public static void open(Player player, LocationsData.GeneratorLocation generator,
                          Block clickedBlock) {
    if (generator.getPlacedBy() != player
        && !player.hasPermission("gensforge.admin")
        && !player.isOp()) {
      ChatUtil.sendMessage(player, Messages.NOT_YOUR_GENERATOR_UPGRADE);
      return;
    }

    final FileConfiguration config = instance.getConfig();
    final Economy economy = instance.getEcon();

    final var rows = config.getInt("guis.upgrade-gui.rows");
    final var menu = new Gui(
        ChatUtil.translate(config.getString("guis.upgrade-gui.title")),
        rows,
        instance
    );

    final GeneratorsData.Generator current = generator.getGeneratorObject();
    GeneratorsData.Generator nextGenerator =
        instance.getGeneratorsData().getGenerator(current.tier() + 1);

    if (nextGenerator == null) {
      ChatUtil.sendMessage(player, Messages.REACHED_MAX_TIER);
      return;
    }

    final ItemStack itemStackUpgradeOne = new ItemStack(nextGenerator.blockType());
    final ItemMeta itemMetaUpgradeOne = itemStackUpgradeOne.getItemMeta();

    itemMetaUpgradeOne.setDisplayName(ChatUtil.translate(config.getString(
        "guis.upgrade-gui.upgradeOne.first-line")));

    double price = instance.getGeneratorsData().getUpgradePrice(current, nextGenerator.tier());

    List<String> lore = config.getStringList("guis.upgrade-gui.upgradeOne.lore");
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
        "guis.upgrade-gui.upgradeAll.first-line")));

    List<String> loreAll = config.getStringList("guis.upgrade-gui.upgradeAll.lore");
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

    menu.setItem(11, new GuiItem(GuiItemType.ITEM, itemStackUpgradeOne, () -> {
      upgradeGenerator(player, generator, clickedBlock);
      player.closeInventory();
    }));

    menu.setItem(15, new GuiItem(GuiItemType.ITEM, itemStackUpgradeAll, () -> {
      upgradeAllGenerators(player, generator);
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
      ChatUtil.sendMessage(player, Messages.REACHED_MAX_TIER);
      return;
    }

    double upgradePrice =
        instance.getGeneratorsData().getUpgradePrice(current, nextGenerator.tier())
            * currentLoc.getBlockLocations().size();

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

    LocationsData.GeneratorLocation next = currentLoc.getNextTier();

    currentLoc.getBlockLocations()
        .forEach(blockLoc -> blockLoc.setType(nextGenerator.blockType().getType()));

    locationsData.removeLocation(currentLoc);

    locationsData.addLocation(next);

    spawnFirework(currentLoc.getCenter());

    ChatUtil.sendMessage(player,
        Messages.SUCCESSFULLY_UPGRADED
            .replace("%tier%", String.valueOf(nextGenerator.tier())));
  }

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
    FileConfiguration config = GensForge.getInstance().getConfig();
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

