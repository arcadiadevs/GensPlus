package xyz.arcadiadevs.gensplus.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.arcadiadevs.gensplus.utils.formatter.Formattable;

/**
 * The WandData class represents the data associated with a wand in GensPlus.
 */

public record WandData(List<Wand> wands) {

  public Wand getWand(UUID uuid) {
    return wands.stream().filter(wand -> wand.getUuid().equals(uuid)).findFirst().orElse(null);
  }

  /**
   * Creates a new wand with the specified properties.
   *
   * @param type       The type of wand.
   * @param uses       The number of uses the wand has.
   * @param multiplier The multiplier of the wand.
   * @return The Wand object representing the wand.
   */
  public Wand create(Wand.WandType type, int uses, double multiplier) {
    Wand wandData = new Wand(UUID.randomUUID(), type, uses, multiplier);
    wands.add(wandData);
    return wandData;
  }

  @AllArgsConstructor
  @Getter
  @Setter
  public static class Wand implements Formattable {
    private UUID uuid;
    private WandType type;
    private int uses;
    private double multiplier;

    /**
     * The WandType enum represents the type of wand in GensPlus.
     */
    public enum WandType {
      SELL_WAND,
    }

    @Override
    public HashMap<String, String> getPlaceHolders() {
      return new HashMap<>() {{
        put("%uses%", String.valueOf(uses));
        put("%multiplier%", String.valueOf(multiplier));
      }};
    }
  }
}
