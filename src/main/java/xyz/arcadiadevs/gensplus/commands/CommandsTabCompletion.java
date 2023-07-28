package xyz.arcadiadevs.gensplus.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.GeneratorsData;
import xyz.arcadiadevs.gensplus.utils.config.ConfigPaths;
import xyz.arcadiadevs.gensplus.utils.permission.Permissions;

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

        return List.of("help", "give", "giveall", "wand");
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
        if (!(adminPermission || commandSender.hasPermission(Permissions.ADMIN.getPermission()))) {
          return null;
        }

        if (strings.length == 2) {
          return List.of("sell", "upgrade");
        }

        if (strings.length == 3) {
          List<String> playerNames = new ArrayList<>();
          for (Player player : commandSender.getServer().getOnlinePlayers()) {
            playerNames.add(player.getName());
          }

          return playerNames;
        }

        if (strings.length == 4) {
          return List.of("<uses>");
        }

        if (strings.length == 5) {
          return List.of("<multiplier>", "unlimited");
        }

        if (strings.length == 6 && strings[1].equalsIgnoreCase("upgrade")) {
          return List.of("<radius>");
        }

      }

      return null;
    }

    if (command.getName().equalsIgnoreCase("selldrops")) {
      if (!GensPlus.getInstance().getConfig()
          .getBoolean(ConfigPaths.SELL_COMMAND_ENABLED.getPath())) {
        return null;
      }

      if (!commandSender.hasPermission(Permissions.GENERATOR_DROPS_SELL_ALL.getPermission())
          || !commandSender.hasPermission(Permissions.GENERATOR_DROPS_SELL_HAND.getPermission())) {
        return null;
      }

      if (strings.length == 1) {
        return List.of("hand", "all");
      }

      return null;
    }

    return null;
  }
}
