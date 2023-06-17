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

    final boolean adminPermission = player.hasPermission(Permissions.ADMIN);

    if (command.getName().equalsIgnoreCase("infiniteforge")) {
      if (strings.length == 0) {
        ChatUtil.sendMessage(player, Messages.DEFAULT_MESSAGE.replace("%version%",
            InfiniteForge.getInstance().getDescription().getVersion()));
        return true;
      }

      if (strings[0].equalsIgnoreCase("help")) {
        if (!(adminPermission || player.hasPermission(Permissions.GENERATOR_HELP))) {
          ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
          return true;
        }

        ChatUtil.sendMessage(player,
            "&9InfiniteForge Commands:");
        ChatUtil.sendMessage(player,
            "&7- /infiniteforge: Display plugin version");
        ChatUtil.sendMessage(player,
            "&7- /infiniteforge give <player> <tier> [amount]: Give a generator to a player");
        ChatUtil.sendMessage(player,
            "&7- /infiniteforge giveall <tier> [amount]: Give a generator to all players");
        ChatUtil.sendMessage(player,
            "&7- /selldrops hand/all: Sell all drops in your hand or inventory");
        ChatUtil.sendMessage(player,
            "&7- /generators: view all generators");
        return true;
      }

      if (strings[0].equalsIgnoreCase("reload")) {
        if (!(adminPermission || player.hasPermission(Permissions.GENERATOR_RELOAD))) {
          ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
          return true;
        }

        InfiniteForge.getInstance().reloadConfig();
        ChatUtil.sendMessage(player, Messages.CONFIG_RELOADED);
        return true;
      }

      if (strings[0].equalsIgnoreCase("give")) {
        if (!(adminPermission || player.hasPermission(Permissions.GENERATOR_GIVE))) {
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

        int amount = 1;
        if (strings.length >= 4) {
          try {
            amount = Integer.parseInt(strings[3]);
          } catch (NumberFormatException e) {
            ChatUtil.sendMessage(player, Messages.INVALID_AMOUNT);
            return true;
          }
        }

        GeneratorsData.Generator generator = generatorsData.getGenerator(tier);
        if (generator == null) {
          ChatUtil.sendMessage(player, Messages.INVALID_GENERATOR_TIER);
          return true;
        }

        for (int i = 0; i < amount; i++) {
          generator.giveItem(targetPlayer);
        }

        ChatUtil.sendMessage(player, Messages.GENERATOR_GIVEN
            .replace("%targetPlayer%", targetPlayer.getName())
            .replace("%tier%", String.valueOf(tier))
            .replace("%amount%", String.valueOf(amount)));

        ChatUtil.sendMessage(targetPlayer, Messages.GENERATOR_RECEIVED
            .replace("%tier%", String.valueOf(tier))
            .replace("%amount%", String.valueOf(amount)));

        return true;
      }

      if (strings[0].equalsIgnoreCase("giveall")) {
        if (!(adminPermission || player.hasPermission(Permissions.GENERATOR_GIVE_ALL))) {
          ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
          return true;
        }

        if (strings.length < 2) {
          ChatUtil.sendMessage(player, Messages.NOT_ENOUGH_ARGUMENTS);
          return true;
        }

        int tier;
        try {
          tier = Integer.parseInt(strings[1]);
        } catch (NumberFormatException e) {
          ChatUtil.sendMessage(player, Messages.INVALID_GENERATOR_TIER);
          return true;
        }

        int amount = 1;
        if (strings.length >= 3) {
          try {
            amount = Integer.parseInt(strings[2]);
          } catch (NumberFormatException e) {
            ChatUtil.sendMessage(player, Messages.INVALID_AMOUNT);
            return true;
          }
        }

        GeneratorsData.Generator generator = generatorsData.getGenerator(tier);
        if (generator == null) {
          ChatUtil.sendMessage(player, Messages.INVALID_GENERATOR_TIER);
          return true;
        }

        int givenCount = 0;
        for (Player targetPlayer : Bukkit.getOnlinePlayers()) {
          for (int i = 0; i < amount; i++) {
            generator.giveItem(targetPlayer);
          }

          givenCount++;
        }

        ChatUtil.sendMessage(player, Messages.GENERATOR_GIVEN_ALL
            .replace("%tier%", String.valueOf(tier))
            .replace("%amount%", String.valueOf(amount))
            .replace("%count%", String.valueOf(givenCount)));

        return true;
      }
    }

    if (command.getName().equalsIgnoreCase("generators")) {
      if (!(adminPermission || player.hasPermission(Permissions.GENERATORS_GUI))) {
        ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
        return true;
      }

      GeneratorsGui.open(player);
      return true;
    }

    if (command.getName().equalsIgnoreCase("selldrops")) {
      if (strings.length == 0) {
        ChatUtil.sendMessage(player, Messages.NOT_ENOUGH_ARGUMENTS);
        return true;
      }

      final boolean sellPermission = adminPermission
          || player.hasPermission(Permissions.GENERATOR_DROPS_SELL);

      if (strings[0].equalsIgnoreCase("all")) {
        if (!(sellPermission || player.hasPermission(Permissions.GENERATOR_DROPS_SELL_ALL))) {
          ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
          return true;
        }

        SellUtil.sellAll(player);
        return true;
      }

      if (strings[0].equalsIgnoreCase("hand")) {
        if (!(sellPermission || player.hasPermission(Permissions.GENERATOR_DROPS_SELL_HAND))) {
          ChatUtil.sendMessage(player, Messages.NO_PERMISSION);
          return true;
        }

        SellUtil.sellHand(player);
        return true;
      }
    }

    return true;
  }
}
