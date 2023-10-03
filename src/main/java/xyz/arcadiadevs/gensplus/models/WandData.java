package xyz.arcadiadevs.gensplus.models;

import com.awaitquality.api.spigot.chat.formatter.Formattable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.utils.config.Config;

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
    Wand wandData = new Wand(UUID.randomUUID(), type, uses, multiplier, 0, 0);
    wands.add(wandData);
    return wandData;
  }

  public Wand remove(UUID uuid) {
    Wand wand = getWand(uuid);
    wands.remove(wand);
    return wand;
  }

  @AllArgsConstructor
  @Getter
  @Setter
  public static class Wand implements Formattable {
    private UUID uuid;
    private WandType type;
    private int uses;
    private double multiplier;
    private long totalEarned;
    private long totalItemsSold;

    /**
     * The WandType enum represents the type of wand in GensPlus.
     */
    public enum WandType {
      SELL_WAND,
    }

    @Override
    public HashMap<String, String> getPlaceHolders() {
      HashMap<String, String> output = new HashMap<>();
      output.put("%uses%", uses == -1 ? Config.SELL_WAND_UNLIMITED_USES_PREFIX.getString() : String.valueOf(uses));
      output.put("%multiplier%", String.valueOf(multiplier));
      output.put("%total_earned%", GensPlus.getInstance().getEcon().format(totalEarned));
      output.put("%total_items_sold%", String.valueOf(totalItemsSold));
      return output;
    }
  }
}
