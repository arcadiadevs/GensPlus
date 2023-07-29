package xyz.arcadiadevs.gensplus.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.utils.Config;
import xyz.arcadiadevs.guilib.Gui;
import xyz.arcadiadevs.guilib.GuiItem;
import xyz.arcadiadevs.guilib.GuiItemType;
import xyz.arcadiadevs.guilib.ItemBuilder;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.utils.message.Messages;
import xyz.arcadiadevs.gensplus.utils.ChatUtil;
import xyz.arcadiadevs.gensplus.utils.GuiUtil;

/**
 * The GeneratorsGui class provides functionality for opening the generators GUI in GensPlus.
 * It displays a GUI menu containing buttons representing different generators.
 */
public class GeneratorsGui {

  /**
   * Opens the generators GUI for the specified player.
   *
   * @param player The Player object for whom the GUI is being opened.
   */
  public static void open(Player player) {
    final var instance = GensPlus.getInstance();
    final var config = instance.getConfig();
    final Economy economy = instance.getEcon();

    if (!config.getBoolean(Config.GUIS_GENERATORS_GUI_ENABLED.getPath())) {
      return;
    }

    final var rows = config.getInt(Config.GUIS_GENERATORS_GUI_ROWS.getPath());

    final var menu = new Gui(
        ChatUtil.translate(config.getString(Config.GUIS_GENERATORS_GUI_TITLE.getPath())),
        rows,
        instance
    );

    GeneratorsData generatorsData = instance.getGeneratorsData();
    List<Map<?, ?>> generatorsConfig = config.getMapList(Config.GENERATORS.getPath());

    for (GeneratorsData.Generator generator : generatorsData.getGenerators()) {
      final ItemStack material = new ItemStack(generator.blockType());

      Map<?, ?> matchingGeneratorConfig = generatorsConfig.stream()
          .filter(generatorConfig -> generatorConfig.get("name").equals(generator.name()))
          .findFirst()
          .orElse(null);

      if (matchingGeneratorConfig == null) {
        continue;
      }

      List<String> lore = ((List<String>) matchingGeneratorConfig.get("lore")).isEmpty()
          ? config.getStringList("default-lore")
          : (List<String>) matchingGeneratorConfig.get("lore");

      lore = lore.stream()
          .map(s -> s.replace("%tier%", String.valueOf(generator.tier())))
          .map(s -> s.replace("%speed%", String.valueOf(generator.speed())))
          .map(s -> s.replace("%price%", economy.format(generator.price())))
          .map(s -> s.replace("%sellPrice%", economy.format(generator.sellPrice())))
          .map(s -> s.replace("%spawnItem%", generator.spawnItem().getType().name()))
          .map(s -> s.replace("%blockType%", generator.blockType().getType().name()))
          .map(ChatUtil::translate)
          .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

      final var itemBuilder = new ItemBuilder(material)
          .name(ChatUtil.translate(generator.name()))
          .lore(lore)
          .build();

      if (config.getBoolean(Config.GUIS_GENERATORS_GUI_BORDER_ENABLED.getPath())) {
        GuiUtil.addBorder(menu,
            config.getString(Config.GUIS_GENERATORS_GUI_BORDER_MATERIAL.getPath())
        );
      }

      ItemStack nextPage = new ItemBuilder(XMaterial.ARROW.parseItem())
          .name(ChatUtil.translate("&aNext Page"))
          .build();

      ItemStack previousPage = new ItemBuilder(XMaterial.ARROW.parseItem())
          .name(ChatUtil.translate("&aPrevious Page"))
          .build();

      ItemStack closeButton = new ItemBuilder(XMaterial.BARRIER.parseItem())
          .name(ChatUtil.translate("&cClose"))
          .build();

      menu.setItem(((rows - 1) * 9) + 3, new GuiItem(GuiItemType.PREVIOUS, previousPage, null));
      menu.setItem(((rows - 1) * 9) + 4, new GuiItem(GuiItemType.CLOSE, closeButton, null));
      menu.setItem(((rows - 1) * 9) + 5, new GuiItem(GuiItemType.NEXT, nextPage, null));

      menu.addItem(new GuiItem(GuiItemType.ITEM, itemBuilder, () -> {
        if (generator.price() > economy.getBalance(player)) {
          Messages.NOT_ENOUGH_MONEY.format(
              "currentBalance", economy.getBalance(player),
              "price", economy.currencyNameSingular() + generator.price())
              .send(player);

          XSound.ENTITY_VILLAGER_NO.play(player);
          return;
        }

        generator.giveItem(player);

        economy.withdrawPlayer(player, generator.price());

        Messages.SUCCESSFULLY_BOUGHT.format(
                "generator", generator.name(),
                "tier", generator.tier(),
                "price", generator.price())
            .send(player);

        XSound.ENTITY_PLAYER_LEVELUP.play(player);
      }));

    }

    player.openInventory(menu.getInventory());
  }
}
