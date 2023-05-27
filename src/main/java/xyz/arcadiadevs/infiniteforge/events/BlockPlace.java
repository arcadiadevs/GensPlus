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
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.infiniteforge.models.HologramsData;
import xyz.arcadiadevs.infiniteforge.models.HologramsData.IfHologram;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
import xyz.arcadiadevs.infiniteforge.utils.HologramsUtil;

/**
 * The BlockPlace class is responsible for handling block place events related to generator blocks
 * in InfiniteForge. It listens for BlockPlaceEvents and performs actions when a generator block is
 * placed.
 */
public class BlockPlace implements Listener {

  private final LocationsData locationsData;
  private final IHologramPool pool;
  private final HologramsData hologramsData;

  /**
   * Constructs a BlockPlace object with the specified LocationsData.
   *
   * @param locationsData The LocationsData object containing information about block locations.
   */
  public BlockPlace(LocationsData locationsData, IHologramPool pool, HologramsData hologramsData) {
    this.locationsData = locationsData;
    this.pool = pool;
    this.hologramsData = hologramsData;
  }

  /**
   * Handles the BlockPlaceEvent triggered when a player places a block.
   *
   * @param event The BlockPlaceEvent object representing the block place event.
   */
  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    ItemStack item = event.getItemInHand();
    ItemMeta meta = item.getItemMeta();

    if (meta == null) {
      return;
    }

    if (!meta.hasLore()) {
      return;
    }

    List<String> lore = meta.getLore();

    if (lore == null || lore.size() < 1) {
      return;
    }

    String firstLine = lore.get(0);

    if (!firstLine.contains("Generator tier")) {
      return;
    }

    int tier = Integer.parseInt(firstLine.split(" ")[2]);

    LocationsData.GeneratorLocation tempLocation = new LocationsData.GeneratorLocation(
        event.getPlayer().getUniqueId().toString(),
        tier,
        event.getBlock().getX(),
        event.getBlock().getY(),
        event.getBlock().getZ(),
        event.getBlock().getWorld().getName(),
        null
    );

    locationsData.addLocation(tempLocation);

    IfHologram ifHologram = null;

    if (pool != null) {
      Location centerLocation = locationsData.getCenter(tempLocation);

      Block placedBlock = event.getBlock();
      Set<Block> connectedBlocks = new HashSet<>();
      locationsData.traverseBlocks(placedBlock, tier, connectedBlocks, 0);

      List<LocationsData.GeneratorLocation> connectedObjects = connectedBlocks.stream()
          .map(locationsData::getLocationData)
          .toList();

      if (connectedBlocks.size() > 1) {
        connectedBlocks.remove(placedBlock);

        connectedObjects
            .forEach(location -> {
              IfHologram ifHologram1 = hologramsData.getHologramData(location.getHologramUuid());

              if (ifHologram1 == null) {
                return;
              }

              pool.remove(ifHologram1.getHologram());
              location.setHologramUuid(null);
              hologramsData.removeHologramData(ifHologram1);
            });

        Material material = XMaterial.matchXMaterial(
                tempLocation.getGeneratorObject().blockType().getType().toString())
            .orElseThrow(() -> new RuntimeException("Invalid item stack"))
            .parseItem()
            .getType();

        Hologram hologram = HologramsUtil.createHologram(
            centerLocation,
            tempLocation.getGeneratorObject().name(),
            material
        );

        pool.takeCareOf(hologram);

        ifHologram = new IfHologram(
            tempLocation.getGeneratorObject().name(),
            centerLocation.getX(),
            centerLocation.getY(),
            centerLocation.getZ(),
            centerLocation.getWorld().getName(),
            tempLocation.getGeneratorObject().blockType().getType().toString(),
            hologram
        );

        hologramsData.addHologramData(ifHologram);
      } else {
        Material material = XMaterial.matchXMaterial(
                tempLocation.getGeneratorObject().blockType().getType().toString())
            .orElseThrow(() -> new RuntimeException("Invalid item stack"))
            .parseItem()
            .getType();

        Hologram hologram = HologramsUtil.createHologram(
            centerLocation,
            tempLocation.getGeneratorObject().name(),
            material
        );

        pool.takeCareOf(hologram);

        ifHologram = new IfHologram(
            tempLocation.getGeneratorObject().name(),
            centerLocation.getX(),
            centerLocation.getY(),
            centerLocation.getZ(),
            centerLocation.getWorld().getName(),
            tempLocation.getGeneratorObject().blockType().getType().toString(),
            hologram
        );

        hologramsData.addHologramData(ifHologram);
      }

      for (LocationsData.GeneratorLocation location : connectedObjects) {
        location.setHologramUuid(ifHologram.getUuid());
      }
    }

    LocationsData.GeneratorLocation location = new LocationsData.GeneratorLocation(
        event.getPlayer().getUniqueId().toString(),
        tier,
        event.getBlock().getX(),
        event.getBlock().getY(),
        event.getBlock().getZ(),
        event.getBlock().getWorld().getName(),
        ifHologram == null ? null : ifHologram.getUuid()
    );

    locationsData.remove(tempLocation);
    locationsData.addLocation(location);

    // Send a notification to the player
    ChatUtil.sendMessage(event.getPlayer(), "&aYou have placed a &eTier " + tier + " &agenerator.");
  }

}
