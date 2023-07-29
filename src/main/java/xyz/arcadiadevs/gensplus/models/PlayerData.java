package xyz.arcadiadevs.gensplus.models;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public record PlayerData(List<Data> data) {

  public Data getData(UUID uuid) {
    return data.stream().filter(d -> d.uuid.equals(uuid)).findFirst().orElse(null);
  }

  @AllArgsConstructor
  @Getter
  public static class Data {

    public UUID uuid;

    @Setter
    public int limit;
  }

}
