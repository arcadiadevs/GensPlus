package xyz.arcadiadevs.gensplus.commands;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.guis.GeneratorsGui;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.models.PlayerData;
import xyz.arcadiadevs.gensplus.utils.ChatUtil;
import xyz.arcadiadevs.gensplus.utils.ItemUtil;
import xyz.arcadiadevs.gensplus.utils.SellUtil;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;
import xyz.arcadiadevs.gensplus.utils.config.Permissions;

/**
 * The Commands class implements the CommandExecutor interface to handle custom commands in
 * GensPlus. It provides functionality for various commands related to generators and the
 * plugin itself.
 */
@AllArgsConstructor
public class Commands implements CommandExecutor {

  private final GeneratorsData generatorsData;
  private final PlayerData playerData;

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
    final boolean adminPermission = commandSender.hasPermission(Permissions.ADMIN.getPermission());

    if (command.getName().equalsIgnoreCase("gensplus")) {
      if (strings.length == 0) {
        Messages.DEFAULT_MESSAGE.format("version",
            GensPlus.getInstance().getDescription().getVersion()).send(commandSender);
        return true;
      }

      if (strings[0].equalsIgnoreCase("help")) {
        ChatUtil.sendMessage(commandSender,
            "&9GensPlus Commands:");
        ChatUtil.sendMessage(commandSender,
            "&7- /gensplus: Display plugin version");
        ChatUtil.sendMessage(commandSender,
            "&7- /gensplus give <player> <tier> [amount]: Give a generator to a player");
        ChatUtil.sendMessage(commandSender,
            "&7- /gensplus giveall <tier> [amount]: Give a generator to all players");
        ChatUtil.sendMessage(commandSender,
            "&7- /gensplus wand sell <player> <uses> <multiplier>: Give a sell wand to a player");
        ChatUtil.sendMessage(commandSender,
            "&7- /gensplus setlimit <player> <limit>: Set a player's generator limit");
        ChatUtil.sendMessage(commandSender,
            "&7- /selldrops hand/all: Sell all drops in your hand or inventory");
        ChatUtil.sendMessage(commandSender,
            "&7- /generators: view all generators");
        return true;
      }

      if (strings[0].equalsIgnoreCase("setlimit")) {
        if (!adminPermission) {
          Messages.NO_PERMISSION.format().send(commandSender);
          return true;
        }

        if (strings.length < 3) {
          Messages.NOT_ENOUGH_ARGUMENTS.format().send(commandSender);
          return true;
        }

        if (!strings[2].matches("\\d+")) {
          Messages.INVALID_FORMAT.format().send(commandSender);
          return true;
        }

        final Player targetPlayer = Bukkit.getPlayer(strings[1]);

        if (targetPlayer == null) {
          Messages.PLAYER_NOT_FOUND.format().send(commandSender);
          return true;
        }

        PlayerData.Data data = playerData.getData(targetPlayer.getUniqueId());
        data.setLimit(Integer.parseInt(strings[2]));
        Messages.LIMIT_UPDATED.format(
            "limit", strings[2],
            "player", targetPlayer.getName())
            .send(commandSender);

        return true;
      }

      if (strings[0].equalsIgnoreCase("wand")) {
        if (!(commandSender instanceof Player player)) {
          Messages.ONLY_PLAYER_CAN_EXECUTE_COMMAND.format().send(commandSender);
          return true;
        }

        if (strings.length < 2) {
          Messages.NOT_ENOUGH_ARGUMENTS.format().send(commandSender);
          return true;
        }

        if (strings[1].equalsIgnoreCase("sell")) {
          if (!commandSender.hasPermission(Permissions.GIVE_WAND.getPermission())) {
            Messages.NO_PERMISSION.format().send(commandSender);
            return true;
          }

          if (strings.length < 5) {
            Messages.NOT_ENOUGH_ARGUMENTS.format().send(commandSender);
            return true;
          }

          if (!strings[3].matches("\\d+") || !strings[4].matches("\\d+\\.?\\d*")) {
            Messages.INVALID_FORMAT.format().send(commandSender);
            return true;
          }

          final Player targetPlayer = Bukkit.getPlayer(strings[2]);

          player.getInventory().addItem(ItemUtil.getSellWand(Integer.parseInt(strings[3]),
              Double.parseDouble(strings[4])));
          Messages.SELL_WAND_GIVEN.format().send(commandSender);
          Messages.SELL_WAND_RECEIVED.format().send(targetPlayer);
          return true;
        }

        return true;
      }

      /*if (strings[0].equalsIgnoreCase("startevent")) {
        if (strings.length < 2) {
          Messages.NOT_ENOUGH_ARGUMENTS.format().send(commandSender);
          return true;
        }

        if (EventLoop.getActiveEvent().event() != null) {
          Messages.EVENT_ALREADY_RUNNING.format().send(commandSender);
          return true;
        }

        String event = strings[1];


      }

      if (strings[0].equalsIgnoreCase("stopevent")) {

      }*/

      /*if (strings[0].equalsIgnoreCase("reload")) {
        if (!(adminPermission || commandSender.hasPermission(Permissions.GENERATOR_RELOAD))) {
          ChatUtil.sendMessage(commandSender, Messages.NO_PERMISSION);
          return true;
        }

        GensPlus.getInstance().reloadConfig();
        ChatUtil.sendMessage(commandSender, Messages.CONFIG_RELOADED);
        return true;
      }*/

      if (strings[0].equalsIgnoreCase("give")) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.GENERATOR_GIVE.getPermission()))) {
          Messages.NO_PERMISSION.format().send(commandSender);
          return true;
        }

        if (strings.length < 3) {
          Messages.NOT_ENOUGH_ARGUMENTS.format().send(commandSender);
          return true;
        }

        Player targetPlayer = Bukkit.getPlayer(strings[1]);
        if (targetPlayer == null) {
          Messages.PLAYER_NOT_FOUND.format().send(commandSender);
          return true;
        }

        int tier;
        try {
          tier = Integer.parseInt(strings[2]);
        } catch (NumberFormatException e) {
          Messages.INVALID_GENERATOR_TIER.format().send(commandSender);
          return true;
        }

        int amount = 1;
        if (strings.length >= 4) {
          try {
            amount = Integer.parseInt(strings[3]);
          } catch (NumberFormatException e) {
            Messages.INVALID_AMOUNT.format().send(commandSender);
            return true;
          }
        }

        GeneratorsData.Generator generator = generatorsData.getGenerator(tier);
        if (generator == null) {
          Messages.INVALID_GENERATOR_TIER.format().send(commandSender);
          return true;
        }

        for (int i = 0; i < amount; i++) {
          generator.giveItem(targetPlayer);
        }

        Messages.GENERATOR_GIVEN.format(
                "targetPlayer", targetPlayer.getName(),
                "tier", String.valueOf(tier),
                "amount", String.valueOf(amount))
            .send(commandSender);

        Messages.GENERATOR_RECEIVED.format(
                "tier", String.valueOf(tier),
                "amount", String.valueOf(amount))
            .send(targetPlayer);

        return true;
      }

      if (strings[0].equalsIgnoreCase("giveall")) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.GENERATOR_GIVE_ALL.getPermission()))) {
          Messages.NO_PERMISSION.format().send(commandSender);
          return true;
        }

        if (strings.length < 2) {
          Messages.NOT_ENOUGH_ARGUMENTS.format().send(commandSender);
          return true;
        }

        int tier;
        try {
          tier = Integer.parseInt(strings[1]);
        } catch (NumberFormatException e) {
          Messages.INVALID_GENERATOR_TIER.format().send(commandSender);
          return true;
        }

        int amount = 1;
        if (strings.length >= 3) {
          try {
            amount = Integer.parseInt(strings[2]);
          } catch (NumberFormatException e) {
            Messages.INVALID_AMOUNT.format().send(commandSender);
            return true;
          }
        }

        GeneratorsData.Generator generator = generatorsData.getGenerator(tier);
        if (generator == null) {
          Messages.INVALID_GENERATOR_TIER.format().send(commandSender);
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
            .send(commandSender);

        return true;
      }
    }

    if (command.getName().equalsIgnoreCase("generators")) {
      if (!(commandSender instanceof Player player)) {
        Messages.ONLY_PLAYER_CAN_EXECUTE_COMMAND.format().send(commandSender);
        return true;
      }

      if (!(adminPermission || commandSender.hasPermission(Permissions.GENERATORS_GUI.getPermission()))) {
        Messages.NO_PERMISSION.format().send(commandSender);
        return true;
      }

      GeneratorsGui.open(player);
      return true;
    }

    if (command.getName().equalsIgnoreCase("selldrops")) {
      if (!(commandSender instanceof Player player)) {
        Messages.ONLY_PLAYER_CAN_EXECUTE_COMMAND.format().send(commandSender);
        return true;
      }

      if (!GensPlus.getInstance().getConfig()
          .getBoolean(Config.SELL_COMMAND_ENABLED.getPath())) {
        return true;
      }

      if (strings.length == 0) {
        Messages.NOT_ENOUGH_ARGUMENTS.format().send(commandSender);
        return true;
      }

      if (strings[0].equalsIgnoreCase("all")) {
        if (!commandSender.hasPermission(Permissions.GENERATOR_DROPS_SELL_ALL.getPermission())) {
          Messages.NO_PERMISSION.format().send(commandSender);
          return true;
        }

        SellUtil.sellAll(player);
        return true;
      }

      if (strings[0].equalsIgnoreCase("hand")) {
        if (!commandSender.hasPermission(Permissions.GENERATOR_DROPS_SELL_HAND.getPermission())) {
          Messages.NO_PERMISSION.format().send(commandSender);
          return true;
        }

        SellUtil.sellHand(player);
        return true;
      }
    }

    return true;
  }
}
