package xyz.arcadiadevs.infiniteforge.events;

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
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.objects.HologramsData;
import xyz.arcadiadevs.infiniteforge.objects.HologramsData.IfHologram;
import xyz.arcadiadevs.infiniteforge.objects.LocationsData;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;

/**
 * The BlockPlace class is responsible for handling block place events related to generator blocks
 * in InfiniteForge. It listens for BlockPlaceEvents and performs actions when a generator block is
 * placed.
 */
public class BlockPlace implements Listener {

  private final LocationsData locationsData;
  private final IHologramPool pool;
  private final InfiniteForge instance;
  private final HologramsData hologramsData;

  /**
   * Constructs a BlockPlace object with the specified LocationsData.
   *
   * @param locationsData The LocationsData object containing information about block locations.
   */
  public BlockPlace(LocationsData locationsData, IHologramPool pool, HologramsData hologramsData,
      InfiniteForge instance
  ) {
    this.locationsData = locationsData;
    this.pool = pool;
    this.instance = instance;
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

    Location centerLocation = locationsData.getCenter(tempLocation);

    Line line = new Line(instance);
    TextLine textLine = new TextLine(line, tempLocation.getGeneratorObject().name(),
        instance.getPlaceholders());

    Material material = XMaterial.matchXMaterial(
            tempLocation.getGeneratorObject().blockType().getType().toString())
        .orElseThrow(() -> new RuntimeException("Invalid item stack"))
        .parseItem()
        .getType();

    Line line2 = new Line(instance);
    ItemLine itemLine = new ItemLine(line2, new ItemStack(material));
    ItemALine itemAline = new ItemALine(itemLine, new StandardAnimatedLine(line2));

    Hologram hologram = new Hologram(instance, centerLocation, new TextItemStandardLoader());
    hologram.load(textLine, itemAline);

    itemAline.setAnimation(Animation.AnimationType.CIRCLE, hologram);

    pool.takeCareOf(hologram);

    IfHologram ifHologram = new IfHologram(
        tempLocation.getGeneratorObject().name(),
        centerLocation.getBlockX(),
        centerLocation.getBlockY(),
        centerLocation.getBlockZ(),
        centerLocation.getWorld().getName(),
        tempLocation.getGeneratorObject().blockType().getType().toString(),
        hologram
    );

    hologramsData.addHologramData(ifHologram);

    LocationsData.GeneratorLocation location = new LocationsData.GeneratorLocation(
        event.getPlayer().getUniqueId().toString(),
        tier,
        event.getBlock().getX(),
        event.getBlock().getY(),
        event.getBlock().getZ(),
        event.getBlock().getWorld().getName(),
        ifHologram.getUuid()
    );

    locationsData.remove(tempLocation);
    locationsData.addLocation(location);

    // Send a notification to the player
    ChatUtil.sendMessage(event.getPlayer(), "&aYou have placed a &eTier " + tier + " &agenerator.");
  }

}
