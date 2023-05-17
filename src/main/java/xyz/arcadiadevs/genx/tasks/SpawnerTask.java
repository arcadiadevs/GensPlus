package xyz.arcadiadevs.genx.tasks;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.genx.GenX;
import xyz.arcadiadevs.genx.objects.BlockData;
import xyz.arcadiadevs.genx.objects.Generator;
import xyz.arcadiadevs.genx.objects.GeneratorsData;

public class SpawnerTask extends BukkitRunnable {

  private final List<BlockData> blockData;

  private final GeneratorsData generatorsData;

  private HashMap<Generator, Long> genNextSpawn;

  public SpawnerTask(List<BlockData> blockData, GeneratorsData generatorsData) {
    this.blockData = blockData;
    this.generatorsData = generatorsData;

    initialize();
  }

  private void initialize() {
    genNextSpawn = new HashMap<>();

    for (Generator generator : generatorsData.generators()) {
      genNextSpawn.put(generator, System.currentTimeMillis() + generator.speed());
    }
  }

  @Override
  public void run() {
    for (Generator generator : generatorsData.generators()) {
      if (genNextSpawn.get(generator) > System.currentTimeMillis()) {
        return;
      }

      List<BlockData> blocksToSpawn = blockData.stream()
          .filter(block -> block.generator() == generator.tier())
          .toList();

      Bukkit.getScheduler().runTask(GenX.getInstance(), () -> {
        for (BlockData block : blocksToSpawn) {
          block.spawn();
        }
      });

      genNextSpawn.put(generator, System.currentTimeMillis() + generator.speed() * 1000L);
    }
  }

}
