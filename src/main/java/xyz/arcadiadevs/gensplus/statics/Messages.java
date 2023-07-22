package xyz.arcadiadevs.gensplus.statics;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.arcadiadevs.gensplus.GensPlus;

/**
 * The Messages class contains all the messages used in GensPlus.
 */
public class Messages {

  public static String NO_PERMISSION;
  public static String CONFIG_RELOADED;
  public static String PLAYER_NOT_FOUND;
  public static String INVALID_GENERATOR_TIER;
  public static String GENERATOR_GIVEN;
  public static String GENERATOR_RECEIVED;
  public static String LIMIT_REACHED;
  public static String EVENT_STARTED;
  public static String EVENT_ENDED;
  public static String SUCCESSFULLY_UPGRADED;
  public static String SUCCESSFULLY_SOLD;
  public static String NOT_ENOUGH_MONEY;
  public static String NOTHING_TO_SELL;
  public static String SUCCESSFULLY_DESTROYED;
  public static String SUCCESSFULLY_PLACED;
  public static String SUCCESSFULLY_BOUGHT;
  public static String REACHED_MAX_TIER;
  public static String NOT_ENOUGH_ARGUMENTS;
  public static String INVALID_AMOUNT;
  public static String GENERATOR_GIVEN_ALL;
  public static String DEFAULT_MESSAGE;
  public static String CANNOT_PLACE_IN_WORLD;
  public static String NOT_YOUR_GENERATOR_DESTROY;
  public static String NOT_YOUR_GENERATOR_UPGRADE;
  public static String ONLY_PLAYER_CAN_EXECUTE_COMMAND;

  /**
   * Initializes the Messages class by loading the messages.yml file.
   */
  public static void init() {
    final File file = new File(GensPlus.getInstance().getDataFolder(), "messages.yml");
    final FileConfiguration config = YamlConfiguration.loadConfiguration(file);


    NO_PERMISSION = config.getString(
        "no-permission",
        "&cError> &7You don't have permission to do that!"
    );

    CONFIG_RELOADED = config.getString(
        "config-reloaded",
        "&9GensPlus> &7Configuration reloaded."
    );

    PLAYER_NOT_FOUND = config.getString(
        "player-not-found",
        "&cError> &7Player not found!"
    );

    INVALID_GENERATOR_TIER = config.getString(
        "invalid-generator-tier",
        "&cError> &7Invalid generator tier!"
    );

    GENERATOR_GIVEN = config.getString(
        "generator-given",
        "&9GensPlus> &7You gave &a%amount% &7generator(s)"
            + " of tier &a%tier% &7to &a%targetPlayer%"
    );

    GENERATOR_RECEIVED = config.getString(
        "generator-received",
        "&9GensPlus> &7You received &a%amount% &7generator(s) of tier &a%tier%"
    );

    LIMIT_REACHED = config.getString(
        "limit-reached",
        "&cError> &7You have reached the limit of &c%limit% &7generators!"
    );

    EVENT_STARTED = config.getString(
        "event-started",
        "&9GensPlus> &7%event% has started and will end in &e&n%time%!"
    );

    EVENT_ENDED = config.getString(
        "event-ended",
        "&9GensPlus> &7%event% has ended and new event will be started in &e&n%time%!"
    );

    SUCCESSFULLY_UPGRADED = config.getString(
        "successfully-upgraded",
        "&9GensPlus> &7Successfully upgraded your generator to tier &a%tier%!"
    );

    SUCCESSFULLY_SOLD = config.getString(
        "successfully-sold",
        "&9GensPlus> &7Successfully sold drops for &a%price%"
    );

    NOT_ENOUGH_MONEY = config.getString(
        "not-enough-money",
        "&cError> &7You don't have enough money to do that!"
    );

    NOTHING_TO_SELL = config.getString(
        "nothing-to-sell",
        "&cError> &7You don't have any drops to sell!"
    );

    SUCCESSFULLY_DESTROYED = config.getString(
        "successfully-destroyed",
        "&9GensPlus> &7Successfully destroyed generator!"
    );

    SUCCESSFULLY_PLACED = config.getString(
        "successfully-placed",
        "&9GensPlus> &7Successfully placed generator tier %tier%!"
    );

    SUCCESSFULLY_BOUGHT = config.getString(
        "successfully-bought",
        "&9GensPlus> &7Successfully bought generator tier %tier% for %price%!"
    );

    REACHED_MAX_TIER = config.getString(
        "reached-max-tier",
        "&cError> &7You have reached the maximum tier of generator!"
    );

    NOT_ENOUGH_ARGUMENTS = config.getString(
        "not-enough-arguments",
        "&cError> &7Not enough arguments!"
    );

    INVALID_AMOUNT = config.getString(
        "invalid-amount",
        "&cError> &7Invalid amount!"
    );

    GENERATOR_GIVEN_ALL = config.getString(
        "generator-given-all",
        "&9GensPlus> &7You gave &a%amount% &7generator"
            + " of tier &a%tier% &7to all players! (&a%count%&7)"
    );

    DEFAULT_MESSAGE = config.getString(
        "default-message",
        "&9GensPlus> &7This server is running GensPlus &av%version%"
    );

    CANNOT_PLACE_IN_WORLD = config.getString(
        "cannot-place-in-world",
        "&cError> &7You cannot place a generator in this world!"
    );

    NOT_YOUR_GENERATOR_DESTROY = config.getString(
        "not-your-generator-destroy",
        "&cError> &7You cannot destroy generator that is not yours!"
    );

    NOT_YOUR_GENERATOR_UPGRADE = config.getString(
        "not-your-generator-upgrade",
        "&cError> &7You cannot upgrade generator that is not yours!"
    );

    ONLY_PLAYER_CAN_EXECUTE_COMMAND = config.getString(
        "only-player-can-execute-command",
        "&cError> &7Only player can execute this command!"
    );

    try {
      config.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
