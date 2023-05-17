package xyz.arcadiadevs.genx.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.genx.objects.Generator;
import xyz.arcadiadevs.genx.objects.GeneratorsData;

public class Commands implements CommandExecutor {

  private final GeneratorsData generatorsData;

  public Commands(GeneratorsData generatorsData) {
    this.generatorsData = generatorsData;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                           @NotNull String s, @NotNull String[] strings) {
    // Make command /getgen <tier> which gives the player the generator item

    if (strings.length < 1) {
      return false;
    }

    int tier = Integer.parseInt(strings[0]);

    Generator generator = generatorsData.getGenerator(tier);

    generator.giveItem((Player) commandSender);

    commandSender.sendMessage("You got a generator of tier " + tier);

    return true;
  }

}
