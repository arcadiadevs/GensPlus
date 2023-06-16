package xyz.arcadiadevs.infiniteforge.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.item.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.statics.Messages;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;

/**
 * The GeneratorsGui class provides functionality for opening the generators GUI in InfiniteForge.
 * It displays a GUI menu containing buttons representing different generators.
 */
public class GeneratorsGui implements Listener {

  private final Gui gui;
  private final Economy economy = InfiniteForge.getInstance().getEcon();

  /**
   * Constructs a new GeneratorsGui instance.
   *
   * @param plugin  The Plugin instance.
   */
  public GeneratorsGui(Plugin plugin) {
    this.gui = new Gui("Generators", 6);
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }

  /**
   * Opens the generators GUI for the specified player.
   *
   * @param player The Player object for whom the GUI is being opened.
   */
  public void open(Player player) {
    gui.getInventory().clear();
    InfiniteForge instance = InfiniteForge.getInstance();

    GeneratorsData generatorsData = instance.getGeneratorsData();
    List<Map<?, ?>> generatorsConfig = instance.getConfig().getMapList("generators");

    for (GeneratorsData.Generator generator : generatorsData.getGenerators()) {
      final var material = XMaterial.matchXMaterial(generator.blockType()).parseItem();

      Map<?, ?> matchingGeneratorConfig = generatorsConfig.stream()
          .filter(generatorConfig -> generatorConfig.get("name").equals(generator.name()))
          .findFirst()
          .orElse(null);

      if (matchingGeneratorConfig == null) {
        continue;
      }

      List<String> lore = ((List<String>) matchingGeneratorConfig.get("lore")).isEmpty()
          ? InfiniteForge.getInstance().getConfig().getStringList("default-lore")
          : (List<String>) matchingGeneratorConfig.get("lore");

      lore = lore.stream()
          .map(s -> s.replace("%tier%", String.valueOf(generator.tier())))
          .map(s -> s.replace("%speed%", String.valueOf(generator.speed())))
          .map(s -> s.replace("%price%", String.valueOf(generator.price())))
          .map(s -> s.replace("%sellPrice%", String.valueOf(generator.sellPrice())))
          .map(s -> s.replace("%spawnItem%", generator.spawnItem().getType().name()))
          .map(s -> s.replace("%blockType%", generator.blockType().getType().name()))
          .map(ChatUtil::translate)
          .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

      final var itemBuilder = new ItemBuilder(material)
          .name(ChatUtil.translate(generator.name()))
          .lore(lore)
          .build();

      for (int i = 0; i < 40; i++) {
        gui.addItem(new Gui.GuiItem(Gui.GuiItemType.ITEM, itemBuilder, () -> {
          if (generator.price() > economy.getBalance(player)) {
            ChatUtil.sendMessage(player, Messages.NOT_ENOUGH_MONEY);
            XSound.ENTITY_VILLAGER_NO.play(player);
            return;
          }

          generator.giveItem(player);

          economy.withdrawPlayer(player, generator.price());

          ChatUtil.sendMessage(player, Messages.SUCCESSFULLY_BOUGHT
              .replace("%generator%", generator.name())
              .replace("%tier%", String.valueOf(generator.tier()))
              .replace("%price%", String.valueOf(generator.price()))
          );

          XSound.ENTITY_PLAYER_LEVELUP.play(player);
        }));
      }
    }

    gui.init(InfiniteForge.getInstance());
    player.openInventory(gui.getInventory());
  }

  /**
   * Handles the inventory click event for the generators GUI.
   *
   * @param event The InventoryClickEvent instance.
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!event.getInventory().equals(gui.getInventory())) {
      return;
    }

    event.setCancelled(true);
    gui.onButtonClick(event);
  }
}
