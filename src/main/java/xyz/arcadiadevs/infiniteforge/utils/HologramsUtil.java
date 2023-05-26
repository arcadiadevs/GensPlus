package xyz.arcadiadevs.infiniteforge.utils;

import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.animation.Animation;
import com.github.unldenis.hologram.line.ItemLine;
import com.github.unldenis.hologram.line.Line;
import com.github.unldenis.hologram.line.TextLine;
import com.github.unldenis.hologram.line.animated.ItemALine;
import com.github.unldenis.hologram.line.animated.StandardAnimatedLine;
import com.github.unldenis.hologram.line.hologram.TextItemStandardLoader;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;

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

}
