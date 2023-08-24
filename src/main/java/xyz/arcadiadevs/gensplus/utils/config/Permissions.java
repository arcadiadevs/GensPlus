package xyz.arcadiadevs.gensplus.utils.config;

/**
 * The Permissions class contains all the permissions used in GensPlus.
 */
public enum Permissions {
  ADMIN("gensplus.admin"),
  GENERATORS_GUI("gensplus.generator.open"),
  GENERATOR_GIVE("gensplus.admin.give"),
  GENERATOR_GIVE_ALL("gensplus.admin.give.all"),
  GENERATOR_RELOAD("gensplus.admin.reload"),
  START_EVENT("gensplus.admin.startevent"),
  GENERATOR_DROPS_SELL_ALL("gensplus.drop.sell.all"),
  GENERATOR_DROPS_SELL_HAND("gensplus.drop.sell.hand"),
  SELL_MULTIPLIER("gensplus.sell.multiplier."),
  GENERATOR_LIMIT("gensplus.limit."),
  CHUNK_RADIUS("gensplus.chunkradius."),
  SET_LIMIT("gensplus.admin.setlimit"),
  ADD_LIMIT("gensplus.admin.addlimit"),
  GIVE_WAND("gensplus.admin.givewand");

  private final String permission;

  Permissions(String permission) {
    this.permission = permission;
  }

  public String getPermission(String... args) {
    String perm = permission;

    // Find how many {} are in the permission
    int count = countBrackets(permission);

    if (count != args.length) {
      throw new IllegalArgumentException("Invalid number of arguments");
    }

    for (String arg : args) {
      perm = permission.replace("{}", arg);
    }

    return perm;
  }

  private int countBrackets(String string) {
    int count = 0;
    for (int i = 0; i < string.length(); i++) {
      if (string.charAt(i) == '{') {
        count++;
      } else if (string.charAt(i) == '}') {
        count--;
      }
    }
    return count;
  }
}
