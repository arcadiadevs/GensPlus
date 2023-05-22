package xyz.arcadiadevs.infiniteforge.tasks;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.objects.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.objects.LocationsData;
import xyz.arcadiadevs.infiniteforge.objects.events.SpeedEvent;

public class SpawnerTask extends BukkitRunnable {

  private final List<LocationsData.GeneratorLocation> blockData;

  private final GeneratorsData generatorsData;

  private HashMap<GeneratorsData.Generator, Long> genNextSpawn;

  public SpawnerTask(List<LocationsData.GeneratorLocation> blockData, GeneratorsData generatorsData) {
    this.blockData = blockData;
    this.generatorsData = generatorsData;

    initialize();
  }

  private void initialize() {
    genNextSpawn = new HashMap<>();

    for (GeneratorsData.Generator generator : generatorsData.generators()) {
      genNextSpawn.put(generator, System.currentTimeMillis() + generator.speed());
    }
  }

  @Override
  public void run() {
    for (GeneratorsData.Generator generator : generatorsData.generators()) {
      if (genNextSpawn.get(generator) > System.currentTimeMillis()) {
        return;
      }

      List<LocationsData.GeneratorLocation> blocksToSpawn = blockData.stream()
          .filter(block -> block.generator() == generator.tier())
          .toList();

      Bukkit.getScheduler().runTask(InfiniteForge.getInstance(), () -> {
        for (LocationsData.GeneratorLocation block : blocksToSpawn) {
          block.spawn();
        }
      });

      long multiplier = (long) (EventLoop.getActiveEvent() instanceof SpeedEvent
          ? EventLoop.getActiveEvent().getMultiplier()
          : 1.0);
      genNextSpawn.put(generator, System.currentTimeMillis() + generator.speed() / multiplier * 1000L);
    }
  }

}
