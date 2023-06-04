package xyz.arcadiadevs.infiniteforge.utils;

import com.cryptomorin.xseries.XMaterial;
import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.IHologramPool;
import com.github.unldenis.hologram.animation.Animation;
import com.github.unldenis.hologram.line.ItemLine;
import com.github.unldenis.hologram.line.Line;
import com.github.unldenis.hologram.line.TextLine;
import com.github.unldenis.hologram.line.animated.ItemALine;
import com.github.unldenis.hologram.line.animated.StandardAnimatedLine;
import com.github.unldenis.hologram.line.hologram.TextItemStandardLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.models.HologramsData;
import xyz.arcadiadevs.infiniteforge.models.LocationsData;

/**
 * The HologramsUtil class is responsible for handling hologram-related tasks in InfiniteForge.
 */
public class HologramsUtil {

  /**
   * Creates a hologram at the specified location with the specified text.
   *
   * @param location The location of the hologram.
   * @param text     The text of the hologram.
   * @param material The material of the hologram.
   * @return The hologram created.
   */
  public static Hologram createHologram(Location location, List<String> text, Material material) {
    Hologram hologram = new Hologram(InfiniteForge.getInstance(), location,
        new TextItemStandardLoader());

    List<TextLine> textLines = new ArrayList<>();
    for (String lineText : text) {
      Line line1 = new Line(InfiniteForge.getInstance());
      TextLine textLine = new TextLine(line1, lineText,
          InfiniteForge.getInstance().getPlaceholders());
      textLines.add(textLine);
    }

    Line line = new Line(InfiniteForge.getInstance());
    ItemLine itemLine = new ItemLine(line, new ItemStack(material));
    ItemALine itemAline = new ItemALine(itemLine, new StandardAnimatedLine(line));

    itemAline.setAnimation(Animation.AnimationType.CIRCLE, hologram);

    hologram.load(textLines.get(0), textLines.get(1), textLines.get(2), textLines.get(3),
        itemAline);

    return hologram;
  }


  /**
   * Fixes the connections between generators.
   *
   * @param location The location of the generator block.
   */
  public static void fixConnections(LocationsData.GeneratorLocation location) {
    final InfiniteForge instance = InfiniteForge.getInstance();
    final LocationsData locationsData = instance.getLocationsData();
    final HologramsData hologramsData = instance.getHologramsData();
    final IHologramPool hologramPool = instance.getHologramPool();

    final Set<Block> connectedBlocks = new HashSet<>();
    locationsData.traverseBlocks(location.getBlock(), location.getGenerator(), connectedBlocks);
    final List<LocationsData.GeneratorLocation> connectedLocations = connectedBlocks.stream()
        .map(locationsData::getLocationData)
        .toList();

    Location centerLocation =
        locationsData.getCenter(location.getLocation().getWorld(), connectedBlocks);

    Material material = XMaterial.matchXMaterial(
            location.getGeneratorObject().blockType().getType().toString())
        .orElseThrow(() -> new RuntimeException("Invalid item stack"))
        .parseItem()
        .getType();

    List<String> lines = InfiniteForge.getInstance().getConfig()
        .getStringList("holograms.lines")
        .stream()
        .map(line -> line.replace("%name%", location.getGeneratorObject().name()))
        .map(line -> line.replace("%tier%",
            String.valueOf(location.getGeneratorObject().tier())))
        .map(line -> line.replace("%speed%",
            String.valueOf(location.getGeneratorObject().speed())))
        .map(line -> line.replace("%spawnItem%",
            location.getGeneratorObject().spawnItem().getType().toString()))
        .map(line -> line.replace("%sellPrice%",
            String.valueOf(location.getGeneratorObject().sellPrice())))
        .map(ChatUtil::translate)
        .toList();

    Hologram hologram = HologramsUtil.createHologram(
        centerLocation,
        lines,
        material
    );

    hologramPool.takeCareOf(hologram);

    HologramsData.IfHologram ifHologram = new HologramsData.IfHologram(
        location.getGeneratorObject().name(),
        lines,
        centerLocation.getX(),
        centerLocation.getY(),
        centerLocation.getZ(),
        centerLocation.getWorld().getName(),
        location.getGeneratorObject().blockType().getType().toString(),
        hologram
    );

    hologramsData.addHologramData(ifHologram);

    connectedLocations.forEach(loc -> loc.setHologramUuid(ifHologram.getUuid()));

  }

  /**
   * Unlinks all holograms connected to the given location.
   *
   * @param location the location to unlink
   */
  public static void unlinkHolograms(LocationsData.GeneratorLocation location) {
    InfiniteForge instance = InfiniteForge.getInstance();
    LocationsData locationsData = instance.getLocationsData();
    HologramsData hologramsData = instance.getHologramsData();
    IHologramPool hologramPool = instance.getHologramPool();

    Set<Block> connectedBlocks = new HashSet<>();
    locationsData.traverseBlocks(location.getBlock(), location.getGenerator(), connectedBlocks);
    List<LocationsData.GeneratorLocation> connectedLocations = connectedBlocks.stream()
        .map(locationsData::getLocationData)
        .toList();

    connectedLocations.forEach(loc -> {
      HologramsData.IfHologram ifHologram = hologramsData.getHologramData(loc.getHologramUuid());

      loc.setHologramUuid(null);

      if (ifHologram == null) {
        return;
      }

      hologramPool.remove(ifHologram.getHologram());
      hologramsData.removeHologramData(ifHologram);
    });
  }

}
