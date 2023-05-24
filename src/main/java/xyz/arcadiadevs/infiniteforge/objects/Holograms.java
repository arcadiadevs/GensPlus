package xyz.arcadiadevs.infiniteforge.objects;

import com.github.unldenis.hologram.Hologram;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Holograms {

  private final UUID uuid;
  private final String name;
  private final int x;
  private final int y;
  private final int z;
  private final String world;
  private final String itemStack;
  private transient Hologram hologram;

}
