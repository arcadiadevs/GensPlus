package xyz.arcadiadevs.genx.guis;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import xyz.arcadiadevs.genx.GenX;
import xyz.arcadiadevs.genx.objects.Generator;
import xyz.arcadiadevs.genx.objects.GeneratorsData;
import xyz.arcadiadevs.genx.utils.ChatUtil;

public class GeneratorsGui {

  public GeneratorsGui() {
    super();
  }

  public void open(Player player) {
    final var instance = GenX.getInstance();
    final var config = instance.getConfig();

    if (!config.getBoolean("generators-gui.enabled")) {
      return;
    }

    final var rows = config.getInt("generators-gui.rows");
    final var menu = instance
        .getSpiGui()
        .create(ChatUtil.translate(config.getString("generators-gui.title")),
            rows
        );

    menu.setAutomaticPaginationEnabled(true);
    menu.setBlockDefaultInteractions(true);

    GeneratorsData generatorsData = instance.getGeneratorsData();
    for (Generator generator : generatorsData.getGenerators()) {
      final var material = XMaterial.matchXMaterial(generator.blockType()).parseItem();

      List<String> lore = new ArrayList<>(List.of(
          "&8Tier: " + generator.tier(),
          "",
          "&7Spawns x1 %s every &e%ss!".formatted(generator.spawnItem().getType().name(), generator.speed())
      ));

      final var itemBuilder = new ItemBuilder(material)
          .name(ChatUtil.translate(generator.name()))
          .lore(ChatUtil.translate(lore))
          .build();

      menu.addButton(new SGButton(itemBuilder).withListener(event -> {
        event.setCancelled(true);
        event.getWhoClicked().sendMessage(ChatUtil.translate("message"));
      }));

    }

    player.openInventory(menu.getInventory());

  }

}
