package xyz.arcadiadevs.infiniteforge.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.guis.GeneratorsGui;
import xyz.arcadiadevs.infiniteforge.objects.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
import xyz.arcadiadevs.infiniteforge.utils.SellUtil;

/**
 * The Commands class implements the CommandExecutor interface to handle custom commands in
 * InfiniteForge. It provides functionality for various commands related to generators and the
 * plugin itself.
 */
public class Commands implements CommandExecutor {

  private final InfiniteForge instance;
  private final GeneratorsData generatorsData;

  /**
   * Constructs a Commands object with the specified InfiniteForge instance and GeneratorsData.
   *
   * @param instance       The InfiniteForge instance.
   * @param generatorsData The GeneratorsData object containing information about generators.
   */
  public Commands(InfiniteForge instance, GeneratorsData generatorsData) {
    this.instance = instance;
    this.generatorsData = generatorsData;
  }

  /**
   * Executes a command issued by a CommandSender.
   *
   * @param commandSender The CommandSender who issued the command.
   * @param command       The Command object representing the executed command.
   * @param s             The label of the command.
   * @param strings       The arguments provided with the command.
   * @return true if the command was handled successfully, false otherwise.
   */
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

      GeneratorsData.Generator generator = generatorsData.getGenerator(tier);
      generator.giveItem(player);

      player.sendMessage("You got a generator of tier " + tier);
    }

    if (command.getName().equalsIgnoreCase("generators")) {
      GeneratorsGui.open(player);
    }

    if (command.getName().equalsIgnoreCase("selldrops")) {
      SellUtil.sell(player);
    }

    if (command.getName().equalsIgnoreCase("infiniteforge")) {
      if (strings.length == 0) {
        ChatUtil.sendMessage(player,
            "&9InfiniteForge> This server is running InfiniteForge version &a"
                + InfiniteForge.getInstance().getDescription().getVersion());
        return true;
      }

      if (strings[0].equalsIgnoreCase("reload")) {
        instance.reloadConfig();

        player.sendMessage("Configuration reloaded.");
      }
    }

    return true;
  }
}
