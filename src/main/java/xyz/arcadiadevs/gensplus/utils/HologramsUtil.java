package xyz.arcadiadevs.gensplus.utils;

import com.github.unldenis.hologram.Hologram;
import com.github.unldenis.hologram.animation.Animation;
import com.github.unldenis.hologram.line.ILine;
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
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.gensplus.GensPlus;

/**
 * The HologramsUtil class is responsible for handling hologram-related tasks in GensPlus.
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
    if (!GensPlus.getInstance().getConfig().getBoolean("holograms.enabled")) {
      return null;
    }

    Hologram hologram = new Hologram(GensPlus.getInstance(), location,
        new TextItemStandardLoader());

    List<ILine> lines = new ArrayList<>();
    for (String lineText : text) {
      Line line1 = new Line(GensPlus.getInstance());
      TextLine textLine = new TextLine(line1, lineText,
          GensPlus.getInstance().getPlaceholders());
      lines.add(textLine);
    }

    Line line = new Line(GensPlus.getInstance());
    ItemLine itemLine = new ItemLine(line, new ItemStack(material));
    ItemALine itemAline = new ItemALine(itemLine, new StandardAnimatedLine(line));

    itemAline.setAnimation(Animation.AnimationType.CIRCLE, hologram);

    lines.add(itemAline);

    hologram.load(lines.toArray(new ILine[0]));

    GensPlus.getInstance().getHologramPool().takeCareOf(hologram);

    return hologram;
  }

  public static void removeHologram(@NotNull Hologram hologram) {
    GensPlus.getInstance().getHologramPool().remove(hologram);
  }

}
