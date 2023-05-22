package xyz.arcadiadevs.infiniteforge.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.arcadiadevs.infiniteforge.objects.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.objects.LocationsData;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;

public class BlockBreak implements Listener {

  private final LocationsData locationsData;

  private final GeneratorsData generatorsData;

  public BlockBreak(LocationsData blockData, GeneratorsData generatorsData) {
    this.locationsData = blockData;
    this.generatorsData = generatorsData;
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    LocationsData.GeneratorLocation block = locationsData.getLocationData(event.getBlock());

    if (block == null) {
      return;
    }

    GeneratorsData.Generator generator = generatorsData.getGenerator(block.generator());

    generator.dropItem(event.getPlayer(), event);

    locationsData.remove(block);

    ChatUtil.sendMessage(event.getPlayer(), "&aYou have broken a generator block!");
  }



}
