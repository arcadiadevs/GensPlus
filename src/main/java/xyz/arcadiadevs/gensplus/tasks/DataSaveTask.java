package xyz.arcadiadevs.gensplus.tasks;

import java.io.FileWriter;
import java.io.IOException;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.gensplus.GensPlus;

/**
 * The DataSaveTask class is a BukkitRunnable task responsible for saving block data to JSON. It
 * periodically saves the generator data to a JSON file.
 */
public class DataSaveTask extends BukkitRunnable {

  private final GensPlus instance;

  /**
   * Constructs a new DataSaveTask with the given GensPlus instance.
   *
   * @param instance The GensPlus instance.
   */
  public DataSaveTask(GensPlus instance) {
    this.instance = instance;
  }

  /**
   * Executes the data saving task. It calls the method to save block data to JSON.
   */
  @Override
  public void run() {
    saveBlockDataToJson();
  }

  /**
   * Saves the block data to a JSON file. It converts the generator data to JSON format and writes
   * it to a file.
   */
  public void saveBlockDataToJson() {
    try (FileWriter writer = new FileWriter(instance.getDataFolder() + "/block_data.json")) {
      instance.getGson().toJson(instance.getLocationsData().locations(), writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
