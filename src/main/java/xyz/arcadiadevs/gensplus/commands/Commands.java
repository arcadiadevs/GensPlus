package xyz.arcadiadevs.gensplus.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.guis.GeneratorsGui;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.utils.message.Messages;
import xyz.arcadiadevs.gensplus.utils.permission.Permissions;
import xyz.arcadiadevs.gensplus.utils.ChatUtil;
import xyz.arcadiadevs.gensplus.utils.SellUtil;

/**
 * The Commands class implements the CommandExecutor interface to handle custom commands in
 * GensPlus. It provides functionality for various commands related to generators and the
 * plugin itself.
 */
public class Commands implements CommandExecutor {

  private final GeneratorsData generatorsData;

  /**
   * Constructs a Commands object with the specified GensPlus instance and GeneratorsData.
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
      Messages.ONLY_PLAYER_CAN_EXECUTE_COMMAND.format().send(commandSender);
      return false;
    }

    final boolean adminPermission = player.hasPermission(Permissions.ADMIN.getPermission());

    if (command.getName().equalsIgnoreCase("gensplus")) {
      if (strings.length == 0) {
        Messages.DEFAULT_MESSAGE.format("version",
            GensPlus.getInstance().getDescription().getVersion()).send(player);
        return true;
      }

      if (strings[0].equalsIgnoreCase("help")) {
        ChatUtil.sendMessage(player,
            "&9GensPlus Commands:");
        ChatUtil.sendMessage(player,
            "&7- /gensplus: Display plugin version");
        ChatUtil.sendMessage(player,
            "&7- /gensplus give <player> <tier> [amount]: Give a generator to a player");
        ChatUtil.sendMessage(player,
            "&7- /gensplus giveall <tier> [amount]: Give a generator to all players");
        ChatUtil.sendMessage(player,
            "&7- /selldrops hand/all: Sell all drops in your hand or inventory");
        ChatUtil.sendMessage(player,
            "&7- /generators: view all generators");
        return true;
      }

      /*if (strings[0].equalsIgnoreCase("reload")) {
        if (!(adminPermission || player.hasPermission(Permissions.GENERATOR_RELOAD))) {
          ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
          return true;
        }

        GensPlus.getInstance().reloadConfig();
        ChatUtil.sendMessage(player, Messages.CONFIG_RELOADED);
        return true;
      }*/

      if (strings[0].equalsIgnoreCase("give")) {
        if (!(adminPermission
            || player.hasPermission(Permissions.GENERATOR_GIVE.getPermission()))) {
          Messages.NO_PERMISSION.format().send(player);
          return true;
        }

        if (strings.length < 3) {
          Messages.NOT_ENOUGH_ARGUMENTS.format().send(player);
          return true;
        }

        Player targetPlayer = Bukkit.getPlayer(strings[1]);
        if (targetPlayer == null) {
          Messages.PLAYER_NOT_FOUND.format().send(player);
          return true;
        }

        int tier;
        try {
          tier = Integer.parseInt(strings[2]);
        } catch (NumberFormatException e) {
          Messages.INVALID_GENERATOR_TIER.format().send(player);
          return true;
        }

        int amount = 1;
        if (strings.length >= 4) {
          try {
            amount = Integer.parseInt(strings[3]);
          } catch (NumberFormatException e) {
            Messages.INVALID_AMOUNT.format().send(player);
            return true;
          }
        }

        GeneratorsData.Generator generator = generatorsData.getGenerator(tier);
        if (generator == null) {
          Messages.INVALID_GENERATOR_TIER.format().send(player);
          return true;
        }

        for (int i = 0; i < amount; i++) {
          generator.giveItem(targetPlayer);
        }

        Messages.GENERATOR_GIVEN.format(
                "targetPlayer", targetPlayer.getName(),
                "tier", String.valueOf(tier),
                "amount", String.valueOf(amount))
            .send(player);

        Messages.GENERATOR_RECEIVED.format(
                "tier", String.valueOf(tier),
                "amount", String.valueOf(amount))
            .send(targetPlayer);

        return true;
      }

      if (strings[0].equalsIgnoreCase("giveall")) {
        if (!(adminPermission
            || player.hasPermission(Permissions.GENERATOR_GIVE_ALL.getPermission()))) {
          Messages.NO_PERMISSION.format().send(player);
          return true;
        }

        if (strings.length < 2) {
          Messages.NOT_ENOUGH_ARGUMENTS.format().send(player);
          return true;
        }

        int tier;
        try {
          tier = Integer.parseInt(strings[1]);
        } catch (NumberFormatException e) {
          Messages.INVALID_GENERATOR_TIER.format().send(player);
          return true;
        }

        int amount = 1;
        if (strings.length >= 3) {
          try {
            amount = Integer.parseInt(strings[2]);
          } catch (NumberFormatException e) {
            Messages.INVALID_AMOUNT.format().send(player);
            return true;
          }
        }

        GeneratorsData.Generator generator = generatorsData.getGenerator(tier);
        if (generator == null) {
          Messages.INVALID_GENERATOR_TIER.format().send(player);
          return true;
        }

        int givenCount = 0;
        for (Player targetPlayer : Bukkit.getOnlinePlayers()) {
          for (int i = 0; i < amount; i++) {
            generator.giveItem(targetPlayer);
          }

          givenCount++;
        }

        Messages.GENERATOR_GIVEN_ALL.format(
                "tier", String.valueOf(tier),
                "amount", String.valueOf(amount),
                "count", String.valueOf(givenCount))
            .send(player);

        return true;
      }
    }

    if (command.getName().equalsIgnoreCase("generators")) {
      if (!(adminPermission || player.hasPermission(Permissions.GENERATORS_GUI.getPermission()))) {
        Messages.NO_PERMISSION.format().send(player);
        return true;
      }

      GeneratorsGui.open(player);
      return true;
    }

    if (command.getName().equalsIgnoreCase("selldrops")) {
      if (!GensPlus.getInstance().getConfig().getBoolean("sell-command.enabled")) {
        return true;
      }

      if (strings.length == 0) {
        Messages.NOT_ENOUGH_ARGUMENTS.format().send(player);
        return true;
      }

      if (strings[0].equalsIgnoreCase("all")) {
        if (!player.hasPermission(Permissions.GENERATOR_DROPS_SELL_ALL.getPermission())) {
          Messages.NO_PERMISSION.format().send(player);
          return true;
        }

        SellUtil.sellAll(player);
        return true;
      }

      if (strings[0].equalsIgnoreCase("hand")) {
        if (!player.hasPermission(Permissions.GENERATOR_DROPS_SELL_HAND.getPermission())) {
          Messages.NO_PERMISSION.format().send(player);
          return true;
        }

        SellUtil.sellHand(player);
        return true;
      }
    }

    return true;
  }
}
