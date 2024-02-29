package xyz.arcadiadevs.gensplus.guis;

import com.awaitquality.api.spigot.chat.ChatUtil;
import org.bukkit.entity.Player;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.guilib.Gui;

public class SellGui {

  public static void open(Player player) {
    if (!Config.GUIS_SELL_GUI_ENABLED.getBoolean()) {
      return;
    }

    final GensPlus instance = GensPlus.getInstance();
    final int rows = Config.GUIS_SELL_GUI_ROWS.getInt();

    final Gui menu = new Gui(
        ChatUtil.translate(Config.GUIS_SELL_GUI_TITLE.getString()),
        rows,
        instance
    );

    player.openInventory(menu.getInventory());
  }
}