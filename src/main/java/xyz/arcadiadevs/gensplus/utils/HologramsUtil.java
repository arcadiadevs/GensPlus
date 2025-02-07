package xyz.arcadiadevs.gensplus.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.holoeasy.hologram.Hologram;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.utils.config.Config;

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
    if (!Config.HOLOGRAMS_ENABLED.getBoolean()) {
      return null;
    }

    try {
      Hologram[] result = new Hologram[1];
      Location holoLocation = location.clone().subtract(0, 1, 0);
      GensPlus.getInstance().getHologramPool().registerHolograms(() -> {
        result[0] = hologram(holoLocation, () -> {
          for (String line : text) {
            textline(line);
          }
          if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
            item(new ItemStack(material));
          }
        });
      });
      return result[0];
    } catch (Exception e) {
      if (Config.DEVELOPER_OPTIONS.getBoolean()) {
        e.printStackTrace();
      }
      return null;
    }
  }

  public static Hologram getHologram(String id) {
    if (id == null) {
      return null;
    }

    return GensPlus.getInstance().getHologramPool().get(UUID.fromString(id));
  }

  public static void removeHologram(@NotNull Hologram hologram) {
    try {
      GensPlus.getInstance().getHologramPool().remove(hologram.getId());
    } catch (Exception e) {
      if (Config.DEVELOPER_OPTIONS.getBoolean()) {
        e.printStackTrace();
      }
    }
  }

}
