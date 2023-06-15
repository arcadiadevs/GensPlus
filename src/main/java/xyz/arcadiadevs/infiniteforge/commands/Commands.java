package xyz.arcadiadevs.infiniteforge.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;
import xyz.arcadiadevs.infiniteforge.guis.GeneratorsGui;
import xyz.arcadiadevs.infiniteforge.models.GeneratorsData;
import xyz.arcadiadevs.infiniteforge.statics.Messages;
import xyz.arcadiadevs.infiniteforge.statics.Permissions;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
import xyz.arcadiadevs.infiniteforge.utils.SellUtil;

/**
 * The Commands class implements the CommandExecutor interface to handle custom commands in
 * InfiniteForge. It provides functionality for various commands related to generators and the
 * plugin itself.
 */
public class Commands implements CommandExecutor {

  private final GeneratorsData generatorsData;

  /**
   * Constructs a Commands object with the specified InfiniteForge instance and GeneratorsData.
   *
   * @param generatorsData The GeneratorsData object containing information about generators.
   */
  public Commands(GeneratorsData generatorsData) {
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

    if (command.getName().equalsIgnoreCase("infiniteforge")) {
      if (strings.length == 0) {
        ChatUtil.sendMessage(player, Messages.DEFAULT_MESSAGE.replace("%version%",
            InfiniteForge.getInstance().getDescription().getVersion()));
        return true;
      }

      if (strings[0].equalsIgnoreCase("reload")) {
        if (!player.hasPermission(Permissions.GENERATOR_RELOAD)) {
          ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
          return true;
        }

        InfiniteForge.getInstance().reloadConfig();
        ChatUtil.sendMessage(player, Messages.CONFIG_RELOADED);
        return true;
      }

      if (strings[0].equalsIgnoreCase("admingive")) {
        if (!player.hasPermission(Permissions.GENERATOR_GIVE)) {
          ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
          return true;
        }

        if (strings.length < 3) {
          ChatUtil.sendMessage(player, Messages.NOT_ENOUGH_ARGUMENTS);
          return true;
        }

        Player targetPlayer = Bukkit.getPlayer(strings[1]);
        if (targetPlayer == null) {
          ChatUtil.sendMessage(player, Messages.PLAYER_NOT_FOUND);
          return true;
        }

        int tier;
        try {
          tier = Integer.parseInt(strings[2]);
        } catch (NumberFormatException e) {
          ChatUtil.sendMessage(player, Messages.INVALID_GENERATOR_TIER);
          return true;
        }

        GeneratorsData.Generator generator = generatorsData.getGenerator(tier);
        if (generator == null) {
          ChatUtil.sendMessage(player, Messages.INVALID_GENERATOR_TIER);
          return true;
        }

        generator.giveItem(targetPlayer);
        ChatUtil.sendMessage(player,
            String.format(Messages.GENERATOR_GIVEN
                    .replace("%player%", targetPlayer.getName()))
                    .replace("%tier%", String.valueOf(tier)));
        ChatUtil.sendMessage(targetPlayer, String.format(Messages.GENERATOR_RECEIVED
            .replace("%tier%", String.valueOf(tier)))
        );
        return true;
      }
    }

    if (command.getName().equalsIgnoreCase("generators")) {
      if (!player.hasPermission(Permissions.GENERATORS_GUI)) {
        ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
        return true;
      }
      new GeneratorsGui(InfiniteForge.getInstance()).open(player);
      return true;
    }

    if (command.getName().equalsIgnoreCase("selldrops")) {
      if (!player.hasPermission(Permissions.GENERATOR_SELL)) {
        ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
        return true;
      }

      SellUtil.sell(player);
      return true;
    }

    return true;
  }
}
