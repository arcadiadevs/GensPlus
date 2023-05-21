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
import net.milkbowl.vault.economy.Economy;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.arcadiadevs.genx.commands.Commands;
import xyz.arcadiadevs.genx.events.BlockBreak;
import xyz.arcadiadevs.genx.events.BlockPlace;
import xyz.arcadiadevs.genx.events.ClickEvent;
import xyz.arcadiadevs.genx.objects.GeneratorsData;
import xyz.arcadiadevs.genx.objects.LocationsData;
import xyz.arcadiadevs.genx.tasks.DataSaveTask;
import xyz.arcadiadevs.genx.tasks.SpawnerTask;
import xyz.arcadiadevs.genx.utils.ChatUtil;

public final class GenX extends JavaPlugin {

    @Getter
    public static GenX instance;

    @Getter
    private Gson gson;

    @Getter
    private LocationsData locationsData;

    @Getter
    private GeneratorsData generatorsData;

    @Getter
    private SpiGUI spiGui;

    @Getter
    private Economy econ = null;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        saveResource("block_data.json", false);

        instance = this;

        setupEconomy();

        gson = new GsonBuilder()
            .registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
            .setPrettyPrinting()
            .create();

        spiGui = new SpiGUI(this);

        generatorsData = loadGeneratorsData();

        locationsData = new LocationsData(loadBlockDataFromJson());

        getServer().getPluginManager().registerEvents(new BlockPlace(locationsData), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(locationsData, generatorsData), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(locationsData, generatorsData), this);

        // Run block data save task every 5 minutes
        new DataSaveTask(this)
            .runTaskTimerAsynchronously(this, 0, 20);

        // Run spawner task every second
        new SpawnerTask(locationsData.getGenerators(), generatorsData)
            .runTaskTimerAsynchronously(this, 0, 20);

        getCommand("genx").setExecutor(new Commands(this, generatorsData));
        getCommand("getitem").setExecutor(new Commands(this, generatorsData));
        getCommand("generators").setExecutor(new Commands(this, generatorsData));
    }

    @Override
    public void onDisable() {
        new DataSaveTask(this).runTask(this);
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            throw new RuntimeException("Vault not found");
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            throw new RuntimeException("No economy plugin found. Please install one, for example EssentialsX.");
        }

        econ = rsp.getProvider();
    }

    private GeneratorsData loadGeneratorsData() {
        List<GeneratorsData.Generator> generators = new ArrayList<>();
        List<Map<?, ?>> generatorsConfig = getConfig().getMapList("generators");

        for (Map<?, ?> generator : generatorsConfig) {
            String name = (String) generator.get("name");
            int tier = (int) generator.get("tier");
            int speed = (int) generator.get("speed");
            double price = (double) generator.get("price");
            String spawnItem = (String) generator.get("spawnItem");
            String blockType = (String) generator.get("blockType");
            List<String> lore = ((List<String>) generator.get("lore")).isEmpty() ? getConfig().getStringList("default-lore") : (List<String>) generator.get("lore");

            lore = lore.stream()
                .map(s -> s.replace("%tier%", String.valueOf(tier)))
                .map(s -> s.replace("%speed%", String.valueOf(speed)))
                .map(s -> s.replace("%price%", String.valueOf(price)))
                .map(s -> s.replace("%spawnItem%", spawnItem))
                .map(s -> s.replace("%blockType%", blockType))
                .map(ChatUtil::translate).toList();

            if (generators.stream().anyMatch(g -> g.tier() == tier)) {
                throw new RuntimeException("Duplicate tier found: " + tier);
            }

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

            blockTypeLore.add(ChatUtil.translate("&8Generator tier " + tier));
            blockTypeLore.addAll(lore);

            blockTypeMeta.setDisplayName(ChatUtil.translate(name));
            blockTypeMeta.setLore(blockTypeLore);

            blockTypeStack.setItemMeta(blockTypeMeta);

            generators.add(new GeneratorsData.Generator(name, tier, price, speed, spawnItemStack, blockTypeStack, lore));
        }

        return new GeneratorsData(generators);
    }

    private List<LocationsData.GeneratorLocation> loadBlockDataFromJson() {
        try (FileReader reader = new FileReader(getDataFolder() + "/block_data.json")) {
            return gson.fromJson(reader, new TypeToken<List<LocationsData.GeneratorLocation>>(){}.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
