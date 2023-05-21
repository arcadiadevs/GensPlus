package xyz.arcadiadevs.genx.objects;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.arcadiadevs.genx.GenX;

public record LocationsData(@Getter List<GeneratorLocation> generators) {

  public void addLocation(GeneratorLocation generator) {
    generators.add(generator);
  }

  public void remove(GeneratorLocation generator) {
    generators.remove(generator);
  }

  @Nullable
  public GeneratorLocation getLocationData(Block block) {
    return generators.stream()
        .filter(b -> b.x() == block.getX()
            && b.y() == block.getY()
            && b.z() == block.getZ()
            && b.world().equals(block.getWorld().getName()))
        .findFirst()
        .orElse(null);
  }

  @Getter
  public record GeneratorLocation(String playerId, int generator, int x, int y, int z,
                                  String world) {

    public void spawn() {
      Location location = new Location(Bukkit.getWorld(world), x, y, z);
      Player player = Bukkit.getPlayer(UUID.fromString(playerId));

      if (player == null) {
        return;
      }

      Item item = location.getWorld().dropItemNaturally(
          location.clone().add(0.5, 1, 0.5),
          getGeneratorObject().spawnItem()
      );



      // TODO: remove item in 5 minutes
    }

    public GeneratorsData.Generator getGeneratorObject() {
      return GenX.getInstance().getGeneratorsData().getGenerator(generator);
    }

    public GeneratorLocation getNextTier() {
      // TODO: Check if next tier exists else return null
      return new GeneratorLocation(playerId, generator + 1, x, y, z, world);
    }

  }


}