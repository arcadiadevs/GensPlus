package xyz.arcadiadevs.genx.events;

import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.arcadiadevs.genx.objects.LocationsData;
import xyz.arcadiadevs.genx.utils.ChatUtil;

public class BlockPlace implements Listener {

  private final LocationsData locationsData;

  public BlockPlace(LocationsData locationsData) {
    this.locationsData = locationsData;
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    ItemStack item = event.getItemInHand();
    ItemMeta meta = item.getItemMeta();

    if (meta == null) {
      return;
    }

    if (!meta.hasLore()) {
      return;
    }

    List<String> lore = meta.getLore();

    if (lore == null || lore.size() < 1) {
      return;
    }

    String firstLine = lore.get(0);

    if (!firstLine.contains("Generator tier")) {
      return;
    }

    int tier = Integer.parseInt(firstLine.split(" ")[2]);

    locationsData.addLocation(
        new LocationsData.GeneratorLocation(
            event.getPlayer().getUniqueId().toString(),
            tier,
            event.getBlock().getX(),
            event.getBlock().getY(),
            event.getBlock().getZ(),
            event.getBlock().getWorld().getName()
        )
    );

    ChatUtil.sendMessage(event.getPlayer(), "&aYou have placed a &eTier " + tier + " &agenerator.");
  }

}
