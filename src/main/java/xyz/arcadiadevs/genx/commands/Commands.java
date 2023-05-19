package xyz.arcadiadevs.genx.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.genx.GenX;
import xyz.arcadiadevs.genx.guis.GeneratorsGui;
import xyz.arcadiadevs.genx.objects.Generator;
import xyz.arcadiadevs.genx.objects.GeneratorsData;
import xyz.arcadiadevs.genx.utils.ChatUtil;

public class Commands implements CommandExecutor {

  private final GeneratorsData generatorsData;

  public Commands(GeneratorsData generatorsData) {
    this.generatorsData = generatorsData;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
      @NotNull String s, @NotNull String[] strings) {

    if (!(commandSender instanceof Player player)) {
      return false;
    }

    if (command.getName().equalsIgnoreCase("getitem")) {

      if (strings.length < 1) {
        return true;
      }

      int tier = Integer.parseInt(strings[0]);

      Generator generator = generatorsData.getGenerator(tier);
      generator.giveItem(player);

      player.sendMessage("You got a generator of tier " + tier);
    }

    if (command.getName().equalsIgnoreCase("generators")) {
      GeneratorsGui.open(player);
    }

    if (command.getName().equalsIgnoreCase("genx")) {
      if (strings.length == 0) {
        ChatUtil.sendMessage(player, "&9GenX> This server is running GenX version &a" + GenX.getInstance().getDescription().getVersion());
        return true;
      }

      if (strings[0].equalsIgnoreCase("reload")) {
        final var instance = GenX.getInstance();
        instance.reloadConfig();

        player.sendMessage("Configuration reloaded.");
      }
    }

    return true;
  }


}
