package xyz.arcadiadevs.infiniteforge.guis;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.objects.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;

/**
 * The GeneratorsGui class provides functionality for opening the generators GUI in InfiniteForge.
 * It displays a GUI menu containing buttons representing different generators.
 */
public class GeneratorsGui {

  /**
   * Opens the generators GUI for the specified player.
   *
   * @param player The Player object for whom the GUI is being opened.
   */
  public static void open(Player player) {
    final var instance = InfiniteForge.getInstance();
    final var config = instance.getConfig();

    if (!config.getBoolean("generators-gui.enabled")) {
      return;
    }

    final var rows = config.getInt("generators-gui.rows");
    final var menu = instance.getSpiGui().create(
        ChatUtil.translate(config.getString("generators-gui.title")),
        rows
    );

    menu.setAutomaticPaginationEnabled(true);
    menu.setBlockDefaultInteractions(true);

    GeneratorsData generatorsData = instance.getGeneratorsData();
    List<Map<?, ?>> generatorsConfig = config.getMapList("generators");

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
          ? config.getStringList("default-lore")
          : (List<String>) matchingGeneratorConfig.get("lore");

      lore = lore.stream()
          .map(s -> s.replace("%tier%", String.valueOf(generator.tier())))
          .map(s -> s.replace("%speed%", String.valueOf(generator.speed())))
          .map(s -> s.replace("%price%", String.valueOf(generator.price())))
          .map(s -> s.replace("%spawnItem%", generator.spawnItem().getType().name()))
          .map(s -> s.replace("%blockType%", generator.blockType().getType().name()))
          .map(ChatUtil::translate)
          .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

      final var itemBuilder = new ItemBuilder(material)
          .name(ChatUtil.translate(generator.name()))
          .lore(lore)
          .build();

      menu.addButton(new SGButton(itemBuilder).withListener(event -> {
        event.setCancelled(true);
        event.getWhoClicked().sendMessage(ChatUtil.translate("message"));
      }));
    }

    player.openInventory(menu.getInventory());
  }
}
