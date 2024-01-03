package xyz.arcadiadevs.gensplus.utils.config.message;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.arcadiadevs.gensplus.GensPlus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Messages class contains all the messages used in GensPlus.
 */
public enum Messages {

  // List of Messages with their corresponding default values
  NO_PERMISSION("no-permission", "&cError> &7You don't have permission to do that!"),
  CONFIG_RELOADED("config-reloaded", "&9GensPlus> &7Configuration reloaded."),
  PLAYER_NOT_FOUND("player-not-found", "&cError> &7Player not found!"),
  INVALID_GENERATOR_TIER("invalid-generator-tier", "&cError> &7Invalid generator tier!"),
  INVALID_FORMAT("invalid-format", "&cError> &7Invalid format!"),
  GENERATOR_GIVEN("generator-given",
      "&9GensPlus> &7You gave &a%amount% &7generator(s) of tier &a%tier% &7to &a%targetPlayer%"),
  GENERATOR_RECEIVED("generator-received",
      "&9GensPlus> &7You received &a%amount% &7generator(s) of tier &a%tier%"),
  LIMIT_REACHED("limit-reached",
      "&cError> &7You have reached the limit of &c%limit% &7generators!"),
  EVENT_STARTED("event-started", "&9GensPlus> &7%event% has started and will end in &e&n%time%!"),
  EVENT_ENDED("event-ended",
      "&9GensPlus> &7%event% has ended and a new event will be started in &e&n%time%!"),
  EVENT_FORCE_ENDED("event-force-ended",
      "&9GensPlus> &7Event has been force ended! New event will be started in &e&n%time%!"),
  EVENT_ALREADY_RUNNING("event-already-running", "&cError> &7An event is already running!"),
  EVENT_NOT_FOUND("event-not-found", "&cError> &7Event not found!"),
  SUCCESSFULLY_UPGRADED("successfully-upgraded",
      "&9GensPlus> &7Successfully upgraded your generator to tier &a%tier%!"),
  SUCCESSFULLY_SOLD("successfully-sold", "&9GensPlus> &7Successfully sold drops for &a%price%"),
  SUCCESSFULLY_SOLD_ACTION_BAR("successfully-sold-action-bar",
      "&cSuccessfully sold &a%amount% &cdrops for &a%price%&c!"),
  NOT_ENOUGH_MONEY("not-enough-money",
      "&cError> &7You don't have enough money to do that! (%currentBalance%/&a%price%&7)"),
  NOTHING_TO_SELL("nothing-to-sell", "&cError> &7You don't have any drops to sell!"),
  SUCCESSFULLY_DESTROYED("successfully-destroyed",
      "&9GensPlus> &7Successfully destroyed generator!"
  ),
  SUCCESSFULLY_PLACED("successfully-placed",
      "&9GensPlus> &7Successfully placed generator tier %tier%!"
  ),
  SUCCESSFULLY_BOUGHT("successfully-bought",
      "&9GensPlus> &7Successfully bought generator tier %tier% for %price%!"
  ),
  REACHED_MAX_TIER("reached-max-tier",
      "&cError> &7You have reached the maximum tier of the generator!"
  ),
  NOT_ENOUGH_ARGUMENTS("not-enough-arguments", "&cError> &7Not enough arguments!"),
  INVALID_AMOUNT("invalid-amount", "&cError> &7Invalid amount!"),
  GENERATOR_GIVEN_ALL("generator-given-all",
      "&9GensPlus> &7You gave &a%amount% &7generator(s) of tier &a%tier% &7to all players! (&a%count%&7)"
  ),
  DEFAULT_MESSAGE("default-message", "&9GensPlus> &7This server is running GensPlus &av%version%"
  ),
  CANNOT_PLACE_IN_WORLD("cannot-place-in-world",
      "&cError> &7You cannot place a generator in this world!"
  ),
  NOT_YOUR_GENERATOR_DESTROY("not-your-generator-destroy",
      "&cError> &7You cannot destroy a generator that is not yours!"
  ),
  NOT_YOUR_GENERATOR_UPGRADE("not-your-generator-upgrade",
      "&cError> &7You cannot upgrade a generator that is not yours!"
  ),
  ONLY_PLAYER_CAN_EXECUTE_COMMAND("only-player-can-execute-command",
      "&cError> &7Only a player can execute this command!"
  ),
  SELL_WAND_GIVEN("sell-wand-given", "&9GensPlus> &7You have been given a sell wand!"),
  SELL_WAND_RECEIVED("sell-wand-received", "&9GensPlus> &7You have received a sell wand!"),
  UPGRADE_WAND_GIVEN("upgrade-wand-given", "&9GensPlus> &7You have been given an upgrade wand!"),
  UPGRADE_WAND_RECEIVED("upgrade-wand-received",
      "&9GensPlus> &7You have received an upgrade wand!"
  ),
  WAND_BROKE("wand-broke", "&cError> &7Your wand broke!"),
  CANNOT_BUY_TIER_4_11("cannot-buy-tier-4-11",
      "&cError> &7You cannot buy a generator of tier &c%tier%&7! (Max tier: &c%maxTier%&7)"
  ),
  CANNOT_BUY_TIER_12_19("cannot-buy-tier-12-19",
      "&cError> &7You cannot buy a generator of tier &c%tier%&7! (Max tier: &c%maxTier%&7)"
  ),
  CANNOT_BUY_TIER_20_27("cannot-buy-tier-20-27",
      "&cError> &7You cannot buy a generator of tier &c%tier%&7! (Max tier: &c%maxTier%&7)"
  ),
  CANNOT_UPGRADE_TIER_4_11("cannot-upgrade-tier-4-11",
      "&cError> &7You cannot upgrade a generator to tier &c%tier%&7! (Max tier: &c%maxTier%&7)"
  ),
  CANNOT_UPGRADE_TIER_12_19("cannot-upgrade-tier-12-19",
      "&cError> &7You cannot upgrade a generator to tier &c%tier%&7! (Max tier: &c%maxTier%&7)"
  ),
  CANNOT_UPGRADE_TIER_20_27("cannot-upgrade-tier-20-27",
      "&cError> &7You cannot upgrade a generator to tier &c%tier%&7! (Max tier: &c%maxTier%&7)"
  ),
  LIMIT_UPDATED("limit-updated",
      "&9GensPlus> &7You have changed %player%'s gens limit to &a%limit%&7!");

  private final String key;

  @Getter
  private final String defaultMessage;

  @Getter
  private String message;

  /**
   * Initializes the Messages enum by loading the messages.yml file.
   */
  public static void init() {
    final File file = new File(GensPlus.getInstance().getDataFolder(), "messages.yml");
    final FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    for (Messages message : Messages.values()) {
      message.message = config.getString(message.key, message.defaultMessage);
    }

    try {
      config.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  Messages(String key, String defaultMessage) {
    this.key = key;
    this.defaultMessage = defaultMessage;
  }

  /**
   * Returns the path of the message.
   *
   * @return The path of the message.
   */
  public String getPath() {
    return name().toLowerCase().replace("_", "-");
  }

  /**
   * Returns a cached list containing the default message.
   *
   * @return A list containing the default message.
   */
  public List<String> getCached() {
    return new ArrayList<>(Collections.singletonList(message));
  }

  /**
   * Formats the message with provided placeholders.
   *
   * @param placeholders The values to replace the placeholders in the message.
   * @return A formatted PlayerMessage with the placeholders replaced.
   */
  public PlayerMessage format(Object... placeholders) {
    return new PlayerMessage(this).format(placeholders);
  }

  /**
   * Returns the message with placeholders replaced by provided values.
   *
   * @param replacements Pairs of placeholder-replacement values
   *                     (in the format of [placeholder, replacement]).
   * @return The message with placeholders replaced.
   */
  public String getMessage(String... replacements) {
    String msg = message;
    for (int i = 0; i < replacements.length - 1; i += 2) {
      msg = msg.replace(replacements[i], replacements[i + 1]);
    }
    return msg;
  }

}
