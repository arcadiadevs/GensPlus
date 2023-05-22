package xyz.arcadiadevs.infiniteforge.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.objects.events.DropEvent;
import xyz.arcadiadevs.infiniteforge.tasks.EventLoop;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

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

  public record GeneratorLocation(String playerId, int generator, int x, int y, int z,
                                  String world) {

    public void spawn() {
      Location location = new Location(Bukkit.getWorld(world), x, y, z);
      Player player = Bukkit.getPlayer(UUID.fromString(playerId));

      if (player == null) {
        return;
      }


      List<Item> items = new ArrayList<>();

      for (int i = 0; i < (EventLoop.getActiveEvent() instanceof DropEvent event ? event.getMultiplier() : 1); i++) {
        Item item = location.getWorld().dropItemNaturally(
            location.clone().add(0.5, 1, 0.5),
            getGeneratorObject().spawnItem()
        );
        items.add(item);
      }

      Bukkit.getScheduler().runTaskLater(InfiniteForge.getInstance(), () -> items.forEach(Item::remove),
          TimeUtil.parseTime(InfiniteForge.getInstance().getConfig().getString("item-despawn-time")));
    }

    public GeneratorsData.Generator getGeneratorObject() {
      return InfiniteForge.getInstance().getGeneratorsData().getGenerator(generator);
    }

    public GeneratorLocation getNextTier() {
      return new GeneratorLocation(playerId, generator + 1, x, y, z, world);
    }

    public Block getBlock() {
      return Bukkit.getWorld(world).getBlockAt(x, y, z);
    }

  }

}