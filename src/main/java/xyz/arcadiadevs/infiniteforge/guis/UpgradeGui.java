package xyz.arcadiadevs.infiniteforge.guis;

import com.samjakob.spigui.buttons.SGButton;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.objects.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.objects.LocationsData;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
import xyz.arcadiadevs.infiniteforge.utils.GuiUtil;

/**
 * The UpgradeGui class provides functionality for opening the upgrade GUI for generators in
 * InfiniteForge. It allows players to upgrade their generators to the next tier.
 */
public class UpgradeGui {

  private static final InfiniteForge instance = InfiniteForge.getInstance();

  /**
   * Opens the upgrade GUI for the specified player and generator.
   *
   * @param player         The Player object for whom the GUI is being opened.
   * @param generator      The GeneratorLocation representing the generator to be upgraded.
   */
  public static void open(Player player, LocationsData.GeneratorLocation generator) {
    final FileConfiguration config = instance.getConfig();

    if (!config.getBoolean("generators-gui.enabled")) {
      return;
    }

    final var rows = 3;
    final var menu = instance.getSpiGui().create(
        ChatUtil.translate(config.getString("generators-gui.title")),
        rows
    );

    menu.setAutomaticPaginationEnabled(false);
    menu.setBlockDefaultInteractions(true);

    final ItemStack itemStack = generator.getNextTier().getGeneratorObject().blockType();

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
    GeneratorsData.Generator current = generator.getGeneratorObject();
    LocationsData.GeneratorLocation next = generator.getNextTier();
    GeneratorsData.Generator nextGenerator = next.getGeneratorObject();

    if (nextGenerator == null) {
      player.sendMessage(ChatUtil.translate("You have reached the maximum level"));
      return;
    }

    double upgradePrice = instance.getGeneratorsData().getUpgradePrice(current, next.generator());

    if (upgradePrice > instance.getEcon().getBalance(player)) {
      player.sendMessage(ChatUtil.translate("You don't have enough money"));
      return;
    }

    EconomyResponse response = instance.getEcon().withdrawPlayer(player, upgradePrice);

    if (!response.transactionSuccess()) {
      player.sendMessage(ChatUtil.translate("An error occurred"));
      return;
    }

    ChatUtil.sendMessage(player, "&aYou have upgraded your generator to level " + next.generator());

    instance.getLocationsData().remove(generator);
    instance.getLocationsData().addLocation(next);
    generator.getBlock().setType(nextGenerator.blockType().getType());
  }
}

