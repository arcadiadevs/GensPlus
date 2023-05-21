package xyz.arcadiadevs.genx.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.arcadiadevs.genx.guis.UpgradeGui;
import xyz.arcadiadevs.genx.objects.GeneratorsData;
import xyz.arcadiadevs.genx.objects.LocationsData;

public class ClickEvent implements Listener {

  private final LocationsData locationsData;
  private final GeneratorsData generatorsData;

  public ClickEvent(LocationsData locationsData, GeneratorsData generatorsData) {
    this.locationsData = locationsData;
    this.generatorsData = generatorsData;
  }

  @EventHandler
  public void onBlockClick(PlayerInteractEvent event) {
    Block block = event.getClickedBlock();
    Player player = event.getPlayer();

    if (block == null) {
      return;
    }

    if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !player.isSneaking()) {
      return;
    }

    LocationsData.GeneratorLocation generatorLocation = locationsData.getLocationData(block);

    GeneratorsData.Generator generator = generatorsData.getGenerator(generatorLocation.generator());

    if (generator == null) {
      return;
    }

    UpgradeGui.open(player, generatorLocation);
  }

}
