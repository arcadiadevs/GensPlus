package xyz.arcadiadevs.infiniteforge.tasks;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.objects.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.objects.LocationsData;
import xyz.arcadiadevs.infiniteforge.objects.events.ActiveEvent;
import xyz.arcadiadevs.infiniteforge.objects.events.SpeedEvent;

/**
 * The SpawnerTask class is a BukkitRunnable task responsible for spawning items from generators at
 * regular intervals. It tracks the next spawn time for each generator and spawns items based on
 * their speed.
 */
public class SpawnerTask extends BukkitRunnable {

  private final List<LocationsData.GeneratorLocation> blockData;
  private final GeneratorsData generatorsData;
  private HashMap<GeneratorsData.Generator, Long> genNextSpawn;

  /**
   * Constructs a new SpawnerTask with the given block data and generator data.
   *
   * @param blockData      The list of generator locations.
   * @param generatorsData The generator data.
   */
  public SpawnerTask(List<LocationsData.GeneratorLocation> blockData,
      GeneratorsData generatorsData) {
    this.blockData = blockData;
    this.generatorsData = generatorsData;
    initialize();
  }

  /**
   * Initializes the spawner task by setting up the next spawn time for each generator.
   */
  private void initialize() {
    genNextSpawn = new HashMap<>();
    for (GeneratorsData.Generator generator : generatorsData.generators()) {
      genNextSpawn.put(generator, System.currentTimeMillis() + generator.speed());
    }
  }

  /**
   * Executes the spawner task. It checks if it is time to spawn items for each generator and spawns
   * items accordingly.
   */
  @Override
  public void run() {
    for (GeneratorsData.Generator generator : generatorsData.generators()) {
      if (genNextSpawn.get(generator) > System.currentTimeMillis()) {
        return;
      }

      List<LocationsData.GeneratorLocation> blocksToSpawn = blockData.stream()
          .filter(block -> block.getGenerator() == generator.tier())
          .toList();

      Bukkit.getScheduler().runTask(InfiniteForge.getInstance(), () -> {
        for (LocationsData.GeneratorLocation block : blocksToSpawn) {
          block.spawn();
        }
      });

      ActiveEvent activeEvent = EventLoop.getActiveEvent();

      long multiplier = (long) (activeEvent.event() instanceof SpeedEvent
          ? EventLoop.getActiveEvent().event().getMultiplier()
          : 1.0);
      genNextSpawn.put(generator,
          System.currentTimeMillis() + generator.speed() / multiplier * 1000L);
    }
  }

}