package xyz.arcadiadevs.genx.guis;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.buttons.SGButton;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.genx.GenX;
import xyz.arcadiadevs.genx.objects.GeneratorsData;
import xyz.arcadiadevs.genx.objects.LocationsData;
import xyz.arcadiadevs.genx.utils.ChatUtil;

public class UpgradeGui {

  public static void open(Player player, LocationsData.GeneratorLocation generator) {
    final GenX instance = GenX.getInstance();
    final FileConfiguration config = instance.getConfig();

    if (!config.getBoolean("generators-gui.enabled")) {
      return;
    }

    final var menu = instance
        .getSpiGui()
        .create(
            ChatUtil.translate(config.getString("generators-gui.title")),
            3
        );

    menu.setAutomaticPaginationEnabled(true);
    menu.setBlockDefaultInteractions(true);

    final ItemStack itemStack = XMaterial.EMERALD_BLOCK.parseItem();

    menu.addButton(new SGButton(itemStack).withListener(event -> {
      GeneratorsData.Generator current = generator.getGeneratorObject();
      LocationsData.GeneratorLocation next = generator.getNextTier();
      GeneratorsData.Generator nextGenerator = next.getGeneratorObject();

      if (nextGenerator == null) {
        event.getWhoClicked().sendMessage(ChatUtil.translate("You have reached the maximum level"));
        return;
      }

      double upgradePrice = instance.getGeneratorsData().getUpgradePrice(current, next.generator());

      if (upgradePrice > instance.getEcon().getBalance(player)) {
        event.getWhoClicked().sendMessage(ChatUtil.translate("You don't have enough money"));
        return;
      }

      EconomyResponse response = instance.getEcon().withdrawPlayer(player, upgradePrice);

      if (!response.transactionSuccess()) {
        event.getWhoClicked().sendMessage(ChatUtil.translate("An error occurred"));
        return;
      }

      instance.getLocationsData().remove(generator);

      instance.getLocationsData().addLocation(next);

      event.getWhoClicked().sendMessage(ChatUtil.translate("Successfully upgraded"));
    }));

    player.openInventory(menu.getInventory());
  }

}
