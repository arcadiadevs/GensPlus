package xyz.arcadiadevs.gensplus.utils.permission;

/**
 * The Permissions class contains all the permissions used in GensPlus.
 */
public enum Permissions {
  ADMIN("gensplus.admin"),
  GENERATORS_GUI("gensplus.generator.open"),
  GENERATOR_GIVE("gensplus.admin.give"),
  GENERATOR_GIVE_ALL("gensplus.admin.give.all"),
  GENERATOR_RELOAD("gensplus.admin.reload"),
  GENERATOR_DROPS_SELL_ALL("gensplus.drop.sell.all"),
  GENERATOR_DROPS_SELL_HAND("gensplus.drop.sell.hand"),
  SELL_MULTIPLIER("gensplus.sell.multiplier."),
  GENERATOR_LIMIT("gensplus.limit."),
  CHUNK_RADIUS("gensplus.chunkradius."),
  SET_LIMIT("gensplus.admin.setlimit");

  private final String permission;

  Permissions(String permission) {
    this.permission = permission;
  }

  public String getPermission() {
    return permission;
  }
}
