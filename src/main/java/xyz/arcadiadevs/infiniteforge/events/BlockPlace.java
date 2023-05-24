package xyz.arcadiadevs.infiniteforge.events;

import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.IHologramPool;
import com.github.unldenis.hologram.animation.Animation;
import com.github.unldenis.hologram.line.ItemLine;
import com.github.unldenis.hologram.line.Line;
import com.github.unldenis.hologram.line.TextLine;
import com.github.unldenis.hologram.line.animated.ItemALine;
import com.github.unldenis.hologram.line.animated.StandardAnimatedLine;
import com.github.unldenis.hologram.line.hologram.TextItemStandardLoader;
import com.github.unldenis.hologram.placeholder.Placeholders;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
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

  /**
   * Constructs a BlockPlace object with the specified LocationsData.
   *
   * @param locationsData The LocationsData object containing information about block locations.
   */
  public BlockPlace(LocationsData locationsData, IHologramPool pool, InfiniteForge instance) {
    this.locationsData = locationsData;
    this.pool = pool;
    this.instance = instance;
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

    LocationsData.GeneratorLocation location = new LocationsData.GeneratorLocation(
        event.getPlayer().getUniqueId().toString(),
        tier,
        event.getBlock().getX(),
        event.getBlock().getY(),
        event.getBlock().getZ(),
        event.getBlock().getWorld().getName()
    );

    // Add the generator block location to the data
    locationsData.addLocation(location);

    Location centerLocation = locationsData.getCenter(location);

    // create new line structure (armorstand)
    Line line = new Line(instance);
    // compose an TextLine hologram
    TextLine textLine = new TextLine(line, "Hiasd", instance.getPlaceholders());

    // create new line structure (armorstand)
    Line line2 = new Line(instance);
    // compose this second ItemLine hologram
    ItemLine itemLine = new ItemLine(line2, new ItemStack(Material.GOLD_BLOCK));
    // compose this second ItemAnimatedLine hologram
    ItemALine itemAline = new ItemALine(itemLine, new StandardAnimatedLine(line2));

    // append to hologram that will make all the hard work for you
    // the TextItemStandardLoader loader will load lines(text or item) one below the other.
    Hologram hologram = new Hologram(instance, centerLocation, new TextItemStandardLoader());
    // remember to call this method or hologram will not be visible
    hologram.load(textLine, itemAline);

    // start animation
    itemAline.setAnimation(Animation.AnimationType.CIRCLE, hologram);

    pool.takeCareOf(hologram);

    // Send a notification to the player
    ChatUtil.sendMessage(event.getPlayer(), "&aYou have placed a &eTier " + tier + " &agenerator.");
  }

}
