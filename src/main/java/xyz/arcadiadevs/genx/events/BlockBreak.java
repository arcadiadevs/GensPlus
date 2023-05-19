package xyz.arcadiadevs.genx.events;

import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.arcadiadevs.genx.objects.BlockData;
import xyz.arcadiadevs.genx.objects.Generator;
import xyz.arcadiadevs.genx.objects.GeneratorsData;
import xyz.arcadiadevs.genx.utils.ChatUtil;

public class BlockBreak implements Listener {

  private final List<BlockData> blockData;

  private final GeneratorsData generatorsData;

  public BlockBreak(List<BlockData> blockData, GeneratorsData generatorsData) {
    this.blockData = blockData;
    this.generatorsData = generatorsData;
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    BlockData block = blockData.stream()
        .filter(b -> b.x() == event.getBlock().getX()
            && b.y() == event.getBlock().getY()
            && b.z() == event.getBlock().getZ()
            && b.world().equals(event.getBlock().getWorld().getName()))
        .findFirst()
        .orElse(null);

    if (block == null) {
      return;
    }

    Generator generator = generatorsData.getGenerator(1);

    // TODO: get generator that has been destroyed

    generator.dropItem(event.getPlayer(), event);

    blockData.remove(block);
    ChatUtil.sendMessage(event.getPlayer(), "&aYou have broken a generator block!");
  }

}
