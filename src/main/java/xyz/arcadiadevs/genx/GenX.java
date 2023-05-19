package xyz.arcadiadevs.genx;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.samjakob.spigui.SpiGUI;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.arcadiadevs.genx.commands.Commands;
import xyz.arcadiadevs.genx.events.BlockBreak;
import xyz.arcadiadevs.genx.events.BlockPlace;
import xyz.arcadiadevs.genx.objects.BlockData;
import xyz.arcadiadevs.genx.objects.Generator;
import xyz.arcadiadevs.genx.objects.GeneratorsData;
import xyz.arcadiadevs.genx.tasks.DataSaveTask;
import xyz.arcadiadevs.genx.tasks.SpawnerTask;

public final class GenX extends JavaPlugin {

    @Getter
    public static GenX instance;

    @Getter
    private Gson gson;

    private List<BlockData> blockData;

    @Getter
    private GeneratorsData generatorsData;

    @Getter
    private SpiGUI spiGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        saveResource("block_data.json", false);

        instance = this;

        gson = new GsonBuilder()
            .registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
            .setPrettyPrinting()
            .create();

        spiGui = new SpiGUI(this);

        generatorsData = loadGeneratorsData();

        blockData = loadBlockDataFromJson();

        getServer().getPluginManager().registerEvents(new BlockPlace(blockData, generatorsData), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(blockData, generatorsData), this);

        // Run block data save task every 5 minutes
        new DataSaveTask(this, blockData)
            .runTaskTimerAsynchronously(this, 0, 20);

        // Run spawner task every second
        new SpawnerTask(blockData, generatorsData)
            .runTaskTimerAsynchronously(this, 0, 20);

        getCommand("genx").setExecutor(new Commands(generatorsData));
        getCommand("getitem").setExecutor(new Commands(generatorsData));
        getCommand("generators").setExecutor(new Commands(generatorsData));
    }

    @Override
    public void onDisable() {

    }

    private GeneratorsData loadGeneratorsData() {
        List<Generator> generators = new ArrayList<>();
        List<Map<?, ?>> generatorsConfig = getConfig().getMapList("generators");

        for (Map<?, ?> generator : generatorsConfig) {
            String name = (String) generator.get("name");
            int tier = (int) generator.get("tier");
            int speed = (int) generator.get("speed");
            String spawnItem = (String) generator.get("spawnItem");
            String blockType = (String) generator.get("blockType");

            ItemStack spawnItemStack = XMaterial.matchXMaterial(spawnItem).orElseThrow().parseItem();
            ItemStack blockTypeStack = XMaterial.matchXMaterial(blockType).orElseThrow().parseItem();

            if (spawnItemStack == null || blockTypeStack == null) {
                throw new RuntimeException("Invalid item name");
            }

            ItemMeta blockTypeMeta = blockTypeStack.getItemMeta();

            if (blockTypeMeta == null) {
                throw new RuntimeException("Invalid item meta");
            }

            List<String> blockTypeLore = new ArrayList<>();

            blockTypeLore.add("Generator tier " + tier);

            blockTypeMeta.setLore(blockTypeLore);

            blockTypeStack.setItemMeta(blockTypeMeta);

            generators.add(new Generator(name, tier, speed, spawnItemStack, blockTypeStack));
        }

        return new GeneratorsData(generators);
    }

    private List<BlockData> loadBlockDataFromJson() {
        try (FileReader reader = new FileReader(getDataFolder() + "/block_data.json")) {
            return gson.fromJson(reader, new TypeToken<List<BlockData>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
