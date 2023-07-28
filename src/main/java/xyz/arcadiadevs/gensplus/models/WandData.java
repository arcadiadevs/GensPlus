package xyz.arcadiadevs.gensplus.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import xyz.arcadiadevs.gensplus.utils.formatter.Formattable;

/**
 * The WandData class represents the data associated with a wand in GensPlus.
 */

public record WandData(List<Wand> wands) {

  /**
   * Creates a new wand with the specified properties.
   *
   * @param type       The type of wand.
   * @param uses       The number of uses the wand has.
   * @param radius     The radius of the wand.
   * @param multiplier The multiplier of the wand.
   * @return The Wand object representing the wand.
   */
  public Wand create(Wand.WandType type, int uses, int radius, double multiplier) {
    Wand wandData = new Wand(UUID.randomUUID(), type, uses, radius, multiplier);
    wands.add(wandData);
    return wandData;
  }

  /**
   * The WandData record represents a wand in GensPlus. It contains various properties
   * associated with the wand.
   */
  public record Wand(UUID uuid, WandType type, int uses, int radius, double multiplier)
      implements Formattable {

    @Override
    public HashMap<String, String> getPlaceHolders() {
      return new HashMap<>() {{
        put("%uses%", String.valueOf(uses));
        put("%radius%", String.valueOf(radius));
        put("%multiplier%", String.valueOf(multiplier));
      }};
    }

    /**
     * The WandType enum represents the type of wand in GensPlus.
     */
    public enum WandType {
      SELL_WAND,
      UPGRADE_WAND
    }
  }
}
