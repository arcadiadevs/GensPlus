package xyz.arcadiadevs.gensplus.guis;

import com.awaitquality.api.spigot.chat.ChatUtil;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.LocationsData;
import xyz.arcadiadevs.gensplus.models.PlayerData;
import xyz.arcadiadevs.gensplus.utils.LimitUtil;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.guilib.Gui;
import xyz.arcadiadevs.guilib.GuiItem;
import xyz.arcadiadevs.guilib.GuiItemType;
import xyz.arcadiadevs.guilib.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class ListGui {

  public static void open(Player player) {
    final var instance = GensPlus.getInstance();

    final var menu = new Gui(
        ChatUtil.translate("List of Players"),
        6,
        instance
    );

    Material nextPageMaterial = XMaterial.matchXMaterial(
            Config.GUIS_GENERATORS_GUI_NEXT_PAGE_MATERIAL.getString())
        .orElse(XMaterial.ARROW)
        .parseMaterial();

    Material previousPageMaterial = XMaterial.matchXMaterial(
            Config.GUIS_GENERATORS_GUI_PREVIOUS_PAGE_MATERIAL.getString())
        .orElse(XMaterial.ARROW)
        .parseMaterial();

    Material closeButtonMaterial = XMaterial.matchXMaterial(
            Config.GUIS_GENERATORS_GUI_CLOSE_BUTTON_MATERIAL.getString())
        .orElse(XMaterial.BARRIER)
        .parseMaterial();


    ItemStack nextPage = new ItemBuilder(nextPageMaterial)
        .name(ChatUtil.translate("&aNext Page"))
        .build();

    ItemStack previousPage = new ItemBuilder(previousPageMaterial)
        .name(ChatUtil.translate("&aPrevious Page"))
        .build();

    ItemStack closeButton = new ItemBuilder(closeButtonMaterial)
        .name(ChatUtil.translate("&cClose"))
        .build();


    menu.setItem(((6 - 1) * 9) + 3, new GuiItem(GuiItemType.PREVIOUS, previousPage, null));
    menu.setItem(((6 - 1) * 9) + 4, new GuiItem(GuiItemType.CLOSE, closeButton, null));
    menu.setItem(((6 - 1) * 9) + 5, new GuiItem(GuiItemType.NEXT, nextPage, null));

    PlayerData playerData = instance.getPlayerData();

    for (OfflinePlayer p : instance.getServer().getOnlinePlayers()) {
      int combinedLimit = LimitUtil.calculateCombinedLimit(p, playerData);

      List<String> lore = new ArrayList<>();
      lore.add(ChatUtil.translate("&7Placed: &e" + instance.getLocationsData().getGeneratorsCountByPlayer(p.getPlayer())));
      lore.add(ChatUtil.translate("&7Limit: &e" + combinedLimit));
      lore.add(ChatUtil.translate("&7Click to view generators"));

      final var itemBuilder = new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial())
          .name(ChatUtil.translate(p.getName()))
          .lore(lore)
          .skullOwner(p.getName())
          .build();

      menu.addItem(new GuiItem(GuiItemType.ITEM, itemBuilder, () -> {
        generatorListForPlayer(player, p.getPlayer());
      }));
    }

    player.getPlayer().openInventory(menu.getInventory());
  }

  private static void generatorListForPlayer(Player player, OfflinePlayer targetPlayer) {
    final var instance = GensPlus.getInstance();

    final var menu = new Gui(
        ChatUtil.translate("List of Generators for " + targetPlayer.getName()),
        6,
        instance
    );

    Material nextPageMaterial = XMaterial.matchXMaterial(
            Config.GUIS_GENERATORS_GUI_NEXT_PAGE_MATERIAL.getString())
        .orElse(XMaterial.ARROW)
        .parseMaterial();

    Material previousPageMaterial = XMaterial.matchXMaterial(
            Config.GUIS_GENERATORS_GUI_PREVIOUS_PAGE_MATERIAL.getString())
        .orElse(XMaterial.ARROW)
        .parseMaterial();

    Material closeButtonMaterial = XMaterial.matchXMaterial(
            Config.GUIS_GENERATORS_GUI_CLOSE_BUTTON_MATERIAL.getString())
        .orElse(XMaterial.BARRIER)
        .parseMaterial();


    ItemStack nextPage = new ItemBuilder(nextPageMaterial)
        .name(ChatUtil.translate("&aNext Page"))
        .build();

    ItemStack previousPage = new ItemBuilder(previousPageMaterial)
        .name(ChatUtil.translate("&aPrevious Page"))
        .build();

    ItemStack closeButton = new ItemBuilder(closeButtonMaterial)
        .name(ChatUtil.translate("&cClose"))
        .build();


    menu.setItem(((6 - 1) * 9) + 3, new GuiItem(GuiItemType.PREVIOUS, previousPage, null));
    menu.setItem(((6 - 1) * 9) + 4, new GuiItem(GuiItemType.CLOSE, closeButton, null));
    menu.setItem(((6 - 1) * 9) + 5, new GuiItem(GuiItemType.NEXT, nextPage, null));

    instance.getLocationsData()
        .locations()
        .stream()
        .filter(location -> location.getPlacedBy().equals(targetPlayer))
        .forEach(location -> {
          ItemStack locationItem = new ItemStack(location.getGeneratorObject().blockType());
          ItemMeta meta = locationItem.getItemMeta();
          meta.setDisplayName(ChatUtil.translate(location.getCenter().getBlockX() + ", " + location.getCenter().getBlockY() + ", " + location.getCenter().getBlockZ()));
          locationItem.setItemMeta(meta);

          menu.addItem(new GuiItem(GuiItemType.ITEM, locationItem, () -> {
            openInside(player.getPlayer(), location);
          }));
        });

    player.getPlayer().openInventory(menu.getInventory());
  }

  private static void openInside(OfflinePlayer player, LocationsData.GeneratorLocation locations) {
    final var instance = GensPlus.getInstance();

    final var menu = new Gui(
        ChatUtil.translate("List of Generators for " + player.getName()),
        6,
        instance
    );

    locations.getBlockLocations().forEach(location -> {
      ItemStack locationItem = new ItemStack(locations.getGeneratorObject().blockType());
      Location finalLocation = location.getLocation();
      locationItem.setItemMeta(null);
      ItemMeta meta = locationItem.getItemMeta();
      meta.setDisplayName(ChatUtil.translate("&7Click to choose an option"));
      meta.setLore(List.of(
          ChatUtil.translate("&7Name: " + locations.getGeneratorObject().name()),
          ChatUtil.translate("&7Tier: " + locations.getGeneratorObject().tier()),
          ChatUtil.translate("&7X: " + finalLocation.getBlockX()),
          ChatUtil.translate("&7Y: " + finalLocation.getBlockY()),
          ChatUtil.translate("&7Z: " + finalLocation.getBlockZ())
      ));
      locationItem.setItemMeta(meta);

      menu.addItem(new GuiItem(GuiItemType.ITEM, locationItem, () -> {
        chooseOption(player.getPlayer(), location.getLocation());
      }));
    });

    player.getPlayer().openInventory(menu.getInventory());
  }

  private static void chooseOption(OfflinePlayer player, Location finalLocation) {
    final var instance = GensPlus.getInstance();

    final var menu = new Gui(
        ChatUtil.translate("Choose an Option"),
        3,
        instance
    );

    ItemStack teleportItem = new ItemStack(XMaterial.ENDER_PEARL.parseItem());
    ItemMeta meta = teleportItem.getItemMeta();
    meta.setDisplayName(ChatUtil.translate("Teleport to Generator"));
    teleportItem.setItemMeta(meta);
    menu.addItem(new GuiItem(GuiItemType.ITEM, teleportItem, () -> {
      player.getPlayer().teleport(finalLocation.add(0.5, 1, 0.5));
    }));

    ItemStack removeItem = new ItemStack(XMaterial.TNT.parseItem());
    meta = removeItem.getItemMeta();
    meta.setDisplayName(ChatUtil.translate("Remove Generator"));
    removeItem.setItemMeta(meta);
    menu.addItem(new GuiItem(GuiItemType.ITEM, removeItem, () -> {
      final Block eventBlock = finalLocation.getBlock();
      final LocationsData.GeneratorLocation generatorLocation =
          instance.getLocationsData().getGeneratorLocation(eventBlock);

      if (generatorLocation == null) {
        return;
      }

      final int tier = generatorLocation.getGenerator();
      ArrayList<Block> blocks = generatorLocation.getBlockLocations();

      blocks.remove(eventBlock);

      instance.getLocationsData().removeLocation(generatorLocation);

      blocks.forEach(block -> {
        LocationsData.GeneratorLocation loc = instance.getLocationsData().getGeneratorLocation(block);

        if (loc != null) {
          return;
        }

        instance.getLocationsData().createLocation(player, tier, block);
      });

      finalLocation.getBlock().setType(XMaterial.AIR.parseMaterial());

      player.getPlayer().sendMessage(ChatUtil.translate("&aGenerator removed!"));
      player.getPlayer().closeInventory();
    }));

    player.getPlayer().openInventory(menu.getInventory());
  }

}
