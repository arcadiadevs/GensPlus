package xyz.arcadiadevs.infiniteforge.statics;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.arcadiadevs.infiniteforge.InfiniteForge;

/**
 * The Messages class contains all the messages used in InfiniteForge.
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
  public static String NOT_ENOUGH_DROPS;
  public static String SUCCESSFULLY_DESTROYED;
  public static String SUCCESSFULLY_PLACED;
  public static String SUCCESSFULLY_BOUGHT;
  public static String REACHED_MAX_TIER;
  public static String NOT_ENOUGH_ARGUMENTS;
  public static String INVALID_AMOUNT;
  public static String GENERATOR_GIVEN_ALL;
  public static String DEFAULT_MESSAGE;

  /**
   * Initializes the Messages class by loading the messages.yml file.
   */
  public static void init() {
    final File file = new File(InfiniteForge.getInstance().getDataFolder(), "messages.yml");
    final FileConfiguration config = YamlConfiguration.loadConfiguration(file);


    NO_PERMISSION = config.getString(
        "no-permission",
        "&cError> &7You don't have permission to do that!"
    );

    CONFIG_RELOADED = config.getString(
        "config-reloaded",
        "&9InfiniteForge> &7Configuration reloaded."
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
        "&9InfiniteForge> &7You gave a generator of tier &a%tier% &7to &a%targetPlayer%"
    );

    GENERATOR_RECEIVED = config.getString(
        "generator-received",
        "&9InfiniteForge> &7You received a generator of tier &a%tier"
    );

    LIMIT_REACHED = config.getString(
        "limit-reached",
        "&cError> &7You have reached the limit of &c%limit% &7generators!"
    );

    EVENT_STARTED = config.getString(
        "event-started",
        "&9InfiniteForge> &7%event% has started and will end in &e&n%time%!"
    );

    EVENT_ENDED = config.getString(
        "event-ended",
        "&9InfiniteForge> &7%event% has ended and new event will be started in &e&n%time%!"
    );

    SUCCESSFULLY_UPGRADED = config.getString(
        "successfully-upgraded",
        "&9InfiniteForge> &7Successfully upgraded your generator to tier &a%tier%!"
    );

    SUCCESSFULLY_SOLD = config.getString(
        "successfully-sold",
        "&9InfiniteForge> &7Successfully sold drops for &a%price%"
    );

    NOT_ENOUGH_MONEY = config.getString(
        "not-enough-money",
        "&cError> &7You don't have enough money to do that!"
    );

    NOT_ENOUGH_DROPS = config.getString(
        "not-enough-drops",
        "&cError> &7You don't have any drops to sell!"
    );

    SUCCESSFULLY_DESTROYED = config.getString(
        "successfully-destroyed",
        "&9InfiniteForge> &7Successfully destroyed generator!"
    );

    SUCCESSFULLY_PLACED = config.getString(
        "successfully-placed",
        "&9InfiniteForge> &7Successfully placed generator tier %tier%!"
    );

    SUCCESSFULLY_BOUGHT = config.getString(
        "successfully-bought",
        "&9InfiniteForge> &7Successfully bought generator tier %tier% for %price%!"
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
        "&9InfiniteForge> &7You gave &a%amount% &7generator"
            + " of tier &a%tier% &7to all players! (&a%count%&7)"
    );

    DEFAULT_MESSAGE = config.getString(
        "default-message",
        "&9InfiniteForge> &7This server is running InfiniteForge &av%version%"
    );

    try {
      config.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
