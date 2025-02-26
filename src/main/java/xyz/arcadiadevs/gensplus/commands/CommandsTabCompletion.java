package xyz.arcadiadevs.gensplus.commands;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The CommandsTabCompletion class implements the TabCompleter interface to provide tab completion
 * for custom commands in GensPlus.
 */

@AllArgsConstructor
public class CommandsTabCompletion implements TabCompleter {

  private final GeneratorsData generatorsData;

  @Nullable
  @Override
  public List<String> onTabComplete(@NotNull CommandSender commandSender,
                                    @NotNull Command command,
                                    @NotNull String s,
                                    @NotNull String[] strings) {

    final boolean adminPermission = commandSender.hasPermission(Permissions.ADMIN.getPermission());

    if (command.getName().equalsIgnoreCase("gensplus")
        || command.getName().equalsIgnoreCase("gens")
        || command.getName().equalsIgnoreCase("gp")) {

      if (strings.length == 1) {
        if (!(adminPermission || commandSender.hasPermission(Permissions.ADMIN.getPermission()))) {
          return null;
        }

        return List.of("help", "list", "give", "giveall", "wand", "setlimit",
            "addlimit", "startevent", "stopevent", "reload");
      }

      if (strings[0].equalsIgnoreCase("reload")) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.GENERATOR_RELOAD.getPermission()))) {
          return null;
        }
      }

      if (strings[0].equalsIgnoreCase("setlimit")) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.SET_LIMIT.getPermission()))) {
          return null;
        }

        if (strings.length == 2) {
          List<String> playerNames = new ArrayList<>();
          for (Player player : commandSender.getServer().getOnlinePlayers()) {
            playerNames.add(player.getName());
          }

          return playerNames;
        }

        if (strings.length == 3) {
          return List.of("<limit>");
        }

      }

      if (strings[0].equalsIgnoreCase("addlimit")) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.ADD_LIMIT.getPermission()))) {
          return null;
        }

        if (strings.length == 2) {
          List<String> playerNames = new ArrayList<>();
          for (Player player : commandSender.getServer().getOnlinePlayers()) {
            playerNames.add(player.getName());
          }

          return playerNames;
        }

        if (strings.length == 3) {
          return List.of("<limit>");
        }

      }

      if (strings[0].equalsIgnoreCase("startevent")) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.START_EVENT.getPermission()))) {
          return null;
        }

        if (strings.length == 2) {
          List<String> listEvents = new ArrayList<>();

          if (Config.EVENTS_SPEED_EVENT_ENABLED.getBoolean()) {
            listEvents.add(Config.EVENTS_SPEED_EVENT_NAME.getString());
          }
          if (Config.EVENTS_SELL_EVENT_ENABLED.getBoolean()) {
            listEvents.add(Config.EVENTS_SELL_EVENT_NAME.getString());
          }
          if (Config.EVENTS_DROP_EVENT_ENABLED.getBoolean()) {
            listEvents.add(Config.EVENTS_DROP_EVENT_NAME.getString());
          }

          return listEvents;
        }
      }

      if (strings[0].equalsIgnoreCase("stopevent")) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.STOP_EVENT.getPermission()))) {
          return null;
        }
      }

      if (Arrays.stream(strings).anyMatch(string -> string.equalsIgnoreCase("give"))) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.GENERATOR_GIVE.getPermission()))) {
          return null;
        }

        if (strings.length == 2) {
          List<String> playerNames = new ArrayList<>();
          for (Player player : commandSender.getServer().getOnlinePlayers()) {
            playerNames.add(player.getName());
          }

          return playerNames;
        }

        if (strings.length == 3) {
          List<Integer> generatorTiers = generatorsData.getGenerators()
              .stream()
              .map(GeneratorsData.Generator::tier)
              .toList();

          return generatorTiers.stream().map(String::valueOf).toList();
        }

        if (strings.length == 4) {
          return List.of("[amount]");
        }
      }

      if (Arrays.stream(strings).anyMatch(string -> string.equalsIgnoreCase("giveall"))) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.GENERATOR_GIVE_ALL.getPermission()))) {
          return null;
        }

        if (strings.length == 2) {
          List<Integer> generatorTiers = generatorsData.getGenerators()
              .stream()
              .map(GeneratorsData.Generator::tier)
              .toList();

          return generatorTiers.stream().map(String::valueOf).toList();
        }

        if (strings.length == 3) {
          return List.of("[amount]");
        }
      }

      if (Arrays.stream(strings).anyMatch(string -> string.equalsIgnoreCase("wand"))) {
        if (!(adminPermission
            || commandSender.hasPermission(Permissions.GIVE_WAND.getPermission()))) {
          return null;
        }

        if (strings.length == 2) {
          return List.of("sell");
        }

        if (strings.length == 3) {
          List<String> playerNames = new ArrayList<>();
          for (Player player : commandSender.getServer().getOnlinePlayers()) {
            playerNames.add(player.getName());
          }

          return playerNames;
        }

        if (strings.length == 4) {
          return List.of("<uses>", "-1");
        }

        if (strings.length == 5) {
          return List.of("<multiplier>");
        }
      }

      return null;
    }

    if (command.getName().equalsIgnoreCase("selldrops")) {
      if (!(Config.SELL_COMMAND_ENABLED.getBoolean())) {
        return null;
      }

      if (!commandSender.hasPermission(Permissions.GENERATOR_DROPS_SELL_ALL.getPermission())
          || !commandSender.hasPermission(Permissions.GENERATOR_DROPS_SELL_HAND.getPermission())
          || !commandSender.hasPermission(Permissions.GENERATOR_DROPS_SELL_GUI.getPermission())) {
        return null;
      }

      if (strings.length == 1) {
        return List.of("hand", "all", "gui");
      }

      return null;
    }

    return null;
  }
}
