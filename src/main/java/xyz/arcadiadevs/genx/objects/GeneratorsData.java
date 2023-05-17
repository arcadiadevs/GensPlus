package xyz.arcadiadevs.genx.objects;

import java.util.List;
import lombok.Getter;

public record GeneratorsData(@Getter List<Generator> generators) {

  public Generator getGenerator(int tier) {
    return generators.stream()
        .filter(generator -> generator.tier() == tier)
        .findFirst()
        .orElse(null);
  }

}
