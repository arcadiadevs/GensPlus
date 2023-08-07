package xyz.arcadiadevs.gensplus.utils.config.objects;

import java.util.ArrayList;
import java.util.List;

public record GensPerLevel(int from, int to, int gain) {

  public boolean isIn(int level) {
    return level >= from && level <= to;
  }

  public static List<GensPerLevel> factory(ArrayList<String> gensPerLevel) {
    ArrayList<GensPerLevel> gensPerLevelList = new ArrayList<>();

    for (String gens : gensPerLevel) {
      String[] split = gens.split(":");

      gensPerLevelList.add(new GensPerLevel(
          Integer.parseInt(split[0]),
          Integer.parseInt(split[1]),
          Integer.parseInt(split[2]))
      );
    }

    return gensPerLevelList;
  }

  @Override
  public String toString() {
    return "GensPerLevel{"
        + "from=" + from
        + ", to=" + to
        + ", gain=" + gain
        + '}';
  }
}
