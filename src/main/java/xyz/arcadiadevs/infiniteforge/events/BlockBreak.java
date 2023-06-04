package xyz.arcadiadevs.infiniteforge.events;

import com.cryptomorin.xseries.XMaterial;
import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.IHologramPool;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.models.HologramsData;
import xyz.arcadiadevs.infiniteforge.models.HologramsData.IfHologram;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;
import xyz.arcadiadevs.infiniteforge.statics.Messages;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
import xyz.arcadiadevs.infiniteforge.utils.HologramsUtil;

/**
 * The BlockBreak class is responsible for handling block break events related to generator blocks
 * in InfiniteForge. It listens for BlockBreakEvents and triggers generator drop item generation,
 * removal of the block location, and sending a notification to the player.
 */
public class BlockBreak implements Listener {

  private final LocationsData locationsData;
  private final GeneratorsData generatorsData;
  private final HologramsData hologramsData;
  private final IHologramPool pool;

  /**
   * Constructs a BlockBreak object with the specified LocationsData and GeneratorsData.
   *
   * @param locationsData  The LocationsData object containing information about block locations.
   * @param generatorsData The GeneratorsData object containing information about generators.
   */
  public BlockBreak(LocationsData locationsData, GeneratorsData generatorsData,
      HologramsData hologramsData, IHologramPool pool) {
    this.locationsData = locationsData;
    this.generatorsData = generatorsData;
    this.hologramsData = hologramsData;
    this.pool = pool;
  }

  /**
   * Handles the BlockBreakEvent triggered when a player breaks a block.
   *
   * @param event The BlockBreakEvent object representing the block break event.
   */
  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    final Block eventBlock = event.getBlock();
    final LocationsData.GeneratorLocation block = locationsData.getLocationData(eventBlock);

    if (block == null) {
      return;
    }
    GeneratorsData.Generator generator = generatorsData.getGenerator(block.getGenerator());

    // Generate and drop the generator item for the player
    generator.dropItem(event.getPlayer(), event);

    if (pool != null) {
      final IfHologram ifHologram = hologramsData.getHologramData(block.getHologramUuid());

      // Check if there are any connected blocks
      Set<Block> connectedBlocks = new HashSet<>();
      locationsData.traverseBlocks(event.getBlock(), block.getGeneratorObject().tier(),
          connectedBlocks, 0);

      Block nearGenBlock = connectedBlocks.stream()
          .filter(b -> b != eventBlock)
          .findFirst()
          .orElse(null);

      hologramsData.removeHologramData(ifHologram);
      pool.remove(ifHologram.getHologram());

      if (nearGenBlock != null) {

        connectedBlocks.stream()
            .map(locationsData::getLocationData)
            .forEach(location -> location.setHologramUuid(null));

        Location center;

        connectedBlocks.remove(eventBlock);

        for (Block connectedBlock : connectedBlocks) {
          Set<Block> blocks = new HashSet<>();
          locationsData.traverseBlocks(connectedBlock, block.getGeneratorObject().tier(), blocks,
              eventBlock, 0);

          List<LocationsData.GeneratorLocation> generatorLocations = blocks.stream()
              .map(locationsData::getLocationData)
              .toList();

          if (!generatorLocations.stream().allMatch(loc -> loc.getHologramUuid() == null)) {
            continue;
          }

          center = locationsData.getCenter(connectedBlock.getWorld(), blocks);

          Material material = XMaterial.matchXMaterial(
                  block.getGeneratorObject().blockType().getType().toString())
              .orElseThrow(() -> new RuntimeException("Invalid item stack"))
              .parseItem()
              .getType();

          List<String> lines = InfiniteForge.getInstance().getConfig()
              .getStringList("holograms.lines")
              .stream()
              .map(line -> line.replace("%name%", generator.name()))
              .map(line -> line.replace("%tier%", String.valueOf(generator.tier())))
              .map(line -> line.replace("%speed%", String.valueOf(generator.speed())))
              .map(line -> line.replace("%spawnItem%", generator.spawnItem().getType().toString()))
              .map(line -> line.replace("%sellPrice%", String.valueOf(generator.sellPrice())))
              .map(ChatUtil::translate)
              .toList();

          Hologram hologram = HologramsUtil.createHologram(
              center,
              lines,
              material
          );

          pool.takeCareOf(hologram);

          IfHologram ifHologram1 = new IfHologram(
              block.getGeneratorObject().name(),
              lines,
              center.getX(),
              center.getY(),
              center.getZ(),
              center.getWorld().getName(),
              block.getGeneratorObject().blockType().getType().toString(),
              hologram
          );

          hologramsData.addHologramData(ifHologram1);

          generatorLocations.forEach(loc -> loc.setHologramUuid(ifHologram1.getUuid()));
        }
      }
    }

    // Remove the block location from the data
    locationsData.remove(block);

    // Send a notification to the player
    ChatUtil.sendMessage(event.getPlayer(), Messages.SUCCESSFULLY_DESTROYED);
  }
}
