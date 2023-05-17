package xyz.arcadiadevs.genx.tasks;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.genx.GenX;
import xyz.arcadiadevs.genx.objects.BlockData;

public class DataSaveTask extends BukkitRunnable {

  private final GenX instance;
  private final List<BlockData> blockData;

  public DataSaveTask(GenX instance, List<BlockData> blockData) {
    this.blockData = blockData;
    this.instance = instance;
  }

  @Override
  public void run() {
    saveBlockDataToJson();
  }

  private void saveBlockDataToJson() {
    try (FileWriter writer = new FileWriter(instance.getDataFolder() + "/block_data.json")) {
      instance.getGson().toJson(blockData, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
