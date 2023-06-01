package xyz.arcadiadevs.infiniteforge.guis;

import com.github.unldenis.hologram.IHologramPool;
import com.samjakob.spigui.buttons.SGButton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.models.HologramsData;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
import xyz.arcadiadevs.infiniteforge.utils.GuiUtil;
import xyz.arcadiadevs.infiniteforge.utils.HologramsUtil;

/**
 * The UpgradeGui class provides functionality for opening the upgrade GUI for generators in
 * InfiniteForge. It allows players to upgrade their generators to the next tier.
 */
public class UpgradeGui {

  private static final InfiniteForge instance = InfiniteForge.getInstance();

  /**
   * Opens the upgrade GUI for the specified player and generator.
   *
   * @param player    The Player object for whom the GUI is being opened.
   * @param generator The GeneratorLocation representing the generator to be upgraded.
   */
  public static void open(Player player, LocationsData.GeneratorLocation generator) {
    final FileConfiguration config = instance.getConfig();
    final Economy economy = instance.getEcon();

    final var rows = config.getInt("guis.upgrade-gui.rows");
    final var menu = instance.getSpiGui().create(
        ChatUtil.translate(config.getString("guis.upgrade-gui.title")),
        rows
    );

    final GeneratorsData.Generator current = generator.getGeneratorObject();
    final LocationsData.GeneratorLocation next = generator.getNextTier();

    GeneratorsData.Generator nextGenerator = next.getGeneratorObject();

    if (nextGenerator == null) {
      ChatUtil.sendMessage(player, "&7You have reached the maximum tier for this generator.");
      return;
    }

    menu.setAutomaticPaginationEnabled(false);
    menu.setBlockDefaultInteractions(true);

    final ItemStack itemStack = new ItemStack(nextGenerator.blockType());
    final ItemMeta itemMeta = itemStack.getItemMeta();

    itemMeta.setDisplayName(ChatUtil.translate(config.getString("guis.upgrade-gui.first-line")));

    List<String> lore = config.getStringList("guis.upgrade-gui.lore");
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
        .map(s -> s.replace("%upgradePrice%", economy.format(
            instance.getGeneratorsData().getUpgradePrice(current, nextGenerator.tier()))))
        .map(s -> s.replace("%money%", economy.format(economy.getBalance(player))))
        .map(ChatUtil::translate)
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);

    GuiUtil.fillHalfInventory(menu, rows);

    menu.setButton(0, 13, new SGButton(itemStack).withListener(event -> {
      upgradeGenerator(player, generator);
      player.closeInventory();
    }));

    player.openInventory(menu.getInventory());
  }

  /**
   * Upgrades the specified generator to the next tier for the given player.
   *
   * @param player    The Player object who is upgrading the generator.
   * @param generator The GeneratorLocation representing the generator to be upgraded.
   */
  private static void upgradeGenerator(Player player, LocationsData.GeneratorLocation generator) {
    final LocationsData locationsData = instance.getLocationsData();
    final HologramsData hologramsData = instance.getHologramsData();
    final IHologramPool hologramPool = instance.getHologramPool();
    GeneratorsData.Generator current = generator.getGeneratorObject();
    LocationsData.GeneratorLocation next = generator.getNextTier();
    GeneratorsData.Generator nextGenerator = next.getGeneratorObject();

    if (nextGenerator == null) {
      player.sendMessage(ChatUtil.translate("You have reached the maximum level"));
      return;
    }

    double upgradePrice =
        instance.getGeneratorsData().getUpgradePrice(current, next.getGenerator());

    if (upgradePrice > instance.getEcon().getBalance(player)) {
      player.sendMessage(ChatUtil.translate("You don't have enough money"));
      return;
    }

    EconomyResponse response = instance.getEcon().withdrawPlayer(player, upgradePrice);

    if (!response.transactionSuccess()) {
      player.sendMessage(ChatUtil.translate("An error occurred"));
      return;
    }

    Set<Block> connectedBlocks = new HashSet<>();
    locationsData.traverseBlocks(generator.getBlock(), generator.getGenerator(), connectedBlocks);

    System.out.println(connectedBlocks.size());

    ArrayList<LocationsData.GeneratorLocation> connectedLocations = connectedBlocks.stream()
        .map(locationsData::getLocationData)
        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

    connectedLocations.forEach(location -> {
      HologramsData.IfHologram ifHologram1 =
          hologramsData.getHologramData(location.getHologramUuid());

      location.setHologramUuid(null);

      if (ifHologram1 == null) {
        return;
      }

      hologramPool.remove(ifHologram1.getHologram());
      hologramsData.removeHologramData(ifHologram1);

      HologramsUtil.unlinkHolograms(location);
    });

    ChatUtil.sendMessage(player,
        "&aYou have upgraded your generator to level " + next.getGenerator());

    generator.getBlock().setType(nextGenerator.blockType().getType());

    connectedLocations.remove(generator);
    instance.getLocationsData().remove(generator);

    next.setHologramUuid(null);
    connectedLocations.add(next);

    instance.getLocationsData().addLocation(next);

    for (LocationsData.GeneratorLocation location : connectedLocations) {
      if (location.getHologramUuid() != null) {
        continue;
      }

      HologramsUtil.fixConnections(location);
    }
  }
}

