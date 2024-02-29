package xyz.arcadiadevs.gensplus.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import xyz.arcadiadevs.gensplus.utils.SellUtil;
import xyz.arcadiadevs.gensplus.utils.config.Config;

public class OnInventoryClose implements Listener {

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    final Inventory inventory = event.getInventory();
    final Player player = (Player) event.getPlayer();

    if (event.getView().getTitle().equals(Config.GUIS_SELL_GUI_TITLE.getString())) {
      SellUtil.sellAll(player, inventory, true);
    }
  }
}
