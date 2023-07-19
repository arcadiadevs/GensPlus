package xyz.arcadiadevs.gensforge.commands;

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
import xyz.arcadiadevs.gensforge.GensForge;
import xyz.arcadiadevs.gensforge.models.GeneratorsData;
import xyz.arcadiadevs.gensforge.statics.Permissions;

/**
 * The CommandsTabCompletion class implements the TabCompleter interface to provide tab completion
 * for custom commands in GensForge.
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

    final boolean adminPermission = commandSender.hasPermission(Permissions.ADMIN);

    if (command.getName().equalsIgnoreCase("gensforge")) {

      if (strings.length == 1) {
        if (!(adminPermission || commandSender.hasPermission(Permissions.ADMIN))) {
          return null;
        }

        return List.of("help", "give", "giveall");
      }

      if (Arrays.stream(strings).anyMatch(string -> string.equalsIgnoreCase("give"))) {
        if (!(adminPermission || commandSender.hasPermission(Permissions.GENERATOR_GIVE))) {
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
        if (!(adminPermission || commandSender.hasPermission(Permissions.GENERATOR_GIVE_ALL))) {
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

      return null;
    }

    if (command.getName().equalsIgnoreCase("selldrops")) {
      if (!GensForge.getInstance().getConfig().getBoolean("sell-command.enabled")) {
        return null;
      }

      if (!commandSender.hasPermission(Permissions.GENERATOR_DROPS_SELL_ALL)
          || !commandSender.hasPermission(Permissions.GENERATOR_DROPS_SELL_HAND)) {
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
