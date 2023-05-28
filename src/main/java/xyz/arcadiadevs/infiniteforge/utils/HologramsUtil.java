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

public class HologramsUtil {

  public static Hologram createHologram(Location location, String text, Material material) {
    Line line = new Line(InfiniteForge.getInstance());
    TextLine textLine = new TextLine(line, text, InfiniteForge.instance.getPlaceholders());

    Line line2 = new Line(InfiniteForge.getInstance());
    ItemLine itemLine = new ItemLine(line2, new ItemStack(material));
    ItemALine itemAline = new ItemALine(itemLine, new StandardAnimatedLine(line2));

    Hologram hologram =
        new Hologram(InfiniteForge.getInstance(), location, new TextItemStandardLoader());
    hologram.load(textLine, itemAline);

    itemAline.setAnimation(Animation.AnimationType.CIRCLE, hologram);

    return hologram;
  }

  public static void fixConnections(LocationsData.GeneratorLocation location) {
    System.out.println("======================================================================");
    System.out.println("PROCESSING: " + location.getLocation());
    System.out.println("BLOCK: " + location.getBlock().getType().name());
    System.out.println("GENERATOR: " + location.getGenerator());

    InfiniteForge instance = InfiniteForge.getInstance();
    LocationsData locationsData = instance.getLocationsData();
    HologramsData hologramsData = instance.getHologramsData();
    IHologramPool hologramPool = instance.getHologramPool();

    Set<Block> connectedBlocks = new HashSet<>();
    locationsData.traverseBlocks(location.getBlock(), location.getGenerator(), connectedBlocks);
    List<LocationsData.GeneratorLocation> connectedLocations = connectedBlocks.stream()
        .map(locationsData::getLocationData)
        .toList();

    System.out.println("CONNECTED BLOCKS: " + connectedBlocks);

    Location centerLocation =
        locationsData.getCenter(location.getLocation().getWorld(), connectedBlocks);

    Material material = XMaterial.matchXMaterial(
            location.getGeneratorObject().blockType().getType().toString())
        .orElseThrow(() -> new RuntimeException("Invalid item stack"))
        .parseItem()
        .getType();

    Hologram hologram = HologramsUtil.createHologram(
        centerLocation,
        location.getGeneratorObject().name(),
        material
    );

    hologramPool.takeCareOf(hologram);

    HologramsData.IfHologram ifHologram = new HologramsData.IfHologram(
        location.getGeneratorObject().name(),
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

  public static void unlinkHolograms(LocationsData.GeneratorLocation location) {
    System.out.println("======================================================================");
    System.out.println("UNLINKING: " + location.getLocation());
    System.out.println("BLOCK: " + location.getBlock().getType().name());
    System.out.println("GENERATOR: " + location.getGenerator());

    InfiniteForge instance = InfiniteForge.getInstance();
    LocationsData locationsData = instance.getLocationsData();
    HologramsData hologramsData = instance.getHologramsData();
    IHologramPool hologramPool = instance.getHologramPool();

    Set<Block> connectedBlocks = new HashSet<>();
    locationsData.traverseBlocks(location.getBlock(), location.getGenerator(), connectedBlocks);
    List<LocationsData.GeneratorLocation> connectedLocations = connectedBlocks.stream()
        .map(locationsData::getLocationData)
        .toList();

    System.out.println("CONNECTED BLOCKS: " + connectedBlocks);

    connectedLocations.forEach(loc -> {
      HologramsData.IfHologram ifHologram = hologramsData.getHologramData(loc.getHologramUuid());

      if (ifHologram == null) {
        return;
      }

      hologramPool.remove(ifHologram.getHologram());
      loc.setHologramUuid(null);
      hologramsData.removeHologramData(ifHologram);
    });
  }

}
