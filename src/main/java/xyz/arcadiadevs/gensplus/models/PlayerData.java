package xyz.arcadiadevs.gensplus.models;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The PlayerData class represents the data associated with a player in GensPlus.
 */
public record PlayerData(List<Data> data) {

  /**
   * Gets the PlayerData object representing the player with the specified UUID.
   *
   * @param uuid The UUID of the player.
   * @return The PlayerData object representing the player.
   */
  public Data getData(UUID uuid) {
    return data.stream().filter(d -> d.uuid.equals(uuid)).findFirst().orElse(null);
  }

  /**
   * Creates a new PlayerData object with the specified properties.
   *
   * @param uuid  The UUID of the player.
   * @param limit The limit of the player.
   * @return The PlayerData object representing the player.
   */
  public Data create(UUID uuid, int limit) {
    Data data = new Data(uuid, limit);
    this.data.add(data);
    return data;
  }

  /**
   * The Data class represents the data associated with a player in GensPlus.
   */
  @AllArgsConstructor
  @Getter
  public static class Data {

    private UUID uuid;

    @Setter
    private int limit;

    public static void addToLimit(Data data, int amount) {
      data.limit += amount;
    }
  }

}
