package xyz.arcadiadevs.gensplus.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.holoeasy.config.HologramKey;
import org.holoeasy.hologram.Hologram;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.gensplus.GensPlus;

import java.util.List;
import java.util.UUID;

import static org.holoeasy.builder.HologramBuilder.*;

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

    return hologram(new HologramKey(GensPlus.getInstance().getHologramPool(), UUID.randomUUID().toString()), location, () -> {
      for (String line : text) {
        textline(line);
      }
      if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
        item(new ItemStack(material));
      }
    });
  }

  public static void removeHologram(@NotNull Hologram hologram) {
    GensPlus.getInstance().getHologramPool().remove(hologram.getKey());
  }

}
