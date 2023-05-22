package xyz.arcadiadevs.infiniteforge.tasks;

import java.io.FileWriter;
import java.io.IOException;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;

public class DataSaveTask extends BukkitRunnable {

  private final InfiniteForge instance;

  public DataSaveTask(InfiniteForge instance) {
    this.instance = instance;
  }

  @Override
  public void run() {
    saveBlockDataToJson();
  }

  private void saveBlockDataToJson() {
    try (FileWriter writer = new FileWriter(instance.getDataFolder() + "/block_data.json")) {
      instance.getGson().toJson(instance.getLocationsData().getGenerators(), writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
