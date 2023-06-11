package xyz.arcadiadevs.infiniteforge.utils;

import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.animation.Animation;
import com.github.unldenis.hologram.line.ItemLine;
import com.github.unldenis.hologram.line.Line;
import com.github.unldenis.hologram.line.TextLine;
import com.github.unldenis.hologram.line.animated.ItemALine;
import com.github.unldenis.hologram.line.animated.StandardAnimatedLine;
import com.github.unldenis.hologram.line.hologram.TextItemStandardLoader;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;

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

    InfiniteForge.getInstance().getHologramPool().takeCareOf(hologram);

    return hologram;
  }

  public static void removeHologram(Hologram hologram) {
    InfiniteForge.getInstance().getHologramPool().remove(hologram);
  }

}
