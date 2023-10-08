package xyz.arcadiadevs.gensplus.utils;

import lombok.Getter;
import org.bukkit.Bukkit;

/**
 * An enum representing the server version.
 */
@Getter
public enum ServerVersion {
  UNKNOWN, V1_7, V1_8, V1_9, V1_10, V1_11, V1_12, V1_13, V1_14, V1_15, V1_16, V1_17, V1_18, V1_19,
  V1_20, V1_21, V1_22;

  /**
   * -- GETTER --
   *  Returns the server version as an enum value.
   *
   * @return The server version.
   */
  private static final ServerVersion serverVersion;
  private static final String serverReleaseVersion;
  private static final String serverPackageVersion;
  private static final String serverPackagePath;

  static {
    serverPackagePath = Bukkit.getServer().getClass().getPackage().getName();
    serverPackageVersion = serverPackagePath.substring(serverPackagePath.lastIndexOf('.') + 1);
    serverReleaseVersion = (serverPackageVersion.indexOf('R') != -1)
        ? serverPackageVersion.substring(serverPackageVersion.indexOf('R') + 1) : "";
    serverVersion = getVersion();
  }

  /**
   * Returns the server version as an enum value.
   *
   * @return The server version.
   */
  private static ServerVersion getVersion() {
    for (ServerVersion version : values()) {
      if (serverPackageVersion.toUpperCase().startsWith(version.name())) {
        return version;
      }
    }
    return UNKNOWN;
  }

  /**
   * Checks if this server version is less than the specified version.
   *
   * @param other The version to compare against.
   * @return True if this server version is less than the specified version, otherwise false.
   */
  public boolean isLessThan(ServerVersion other) {
    return (ordinal() < other.ordinal());
  }

  /**
   * Checks if this server version is at or below the specified version.
   *
   * @param other The version to compare against.
   * @return True if this server version is at or below the specified version, otherwise false.
   */
  public boolean isAtOrBelow(ServerVersion other) {
    return (ordinal() <= other.ordinal());
  }

  /**
   * Checks if this server version is greater than the specified version.
   *
   * @param other The version to compare against.
   * @return True if this server version is greater than the specified version, otherwise false.
   */
  public boolean isGreaterThan(ServerVersion other) {
    return (ordinal() > other.ordinal());
  }

  /**
   * Checks if this server version is at least the specified version.
   *
   * @param other The version to compare against.
   * @return True if this server version is at least the specified version, otherwise false.
   */
  public boolean isAtLeast(ServerVersion other) {
    return (ordinal() >= other.ordinal());
  }

  /**
   * Returns the server version as a string.
   *
   * @return The server version string.
   */
  public static String getServerVersionString() {
    return serverPackageVersion;
  }

  /**
   * Returns the release number of the server version.
   *
   * @return The release number of the server version.
   */
  public static String getVersionReleaseNumber() {
    return serverReleaseVersion;
  }

  /**
   * Checks if the current server version matches the specified version.
   *
   * @param version The version to check against.
   * @return True if the current server version matches the specified version, otherwise false.
   */
  public static boolean isServerVersion(ServerVersion version) {
    return (serverVersion == version);
  }

  /**
   * Checks if the current server version matches any of the specified versions.
   *
   * @param versions The versions to check against.
   * @return True if the current server version matches any of the specified versions, otherwise false.
   */
  public static boolean isServerVersion(ServerVersion... versions) {
    for (ServerVersion v : versions) {
      if (v == serverVersion) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the current server version is above the specified version.
   *
   * @param version The version to compare against.
   * @return True if the current server version is above the specified version, otherwise false.
   */
  public static boolean isServerVersionAbove(ServerVersion version) {
    return (serverVersion.ordinal() > version.ordinal());
  }

  /**
   * Checks if the current server version is at least the specified version.
   *
   * @param version The version to compare against.
   * @return True if the current server version is at least the specified version, otherwise false.
   */
  public static boolean isServerVersionAtLeast(ServerVersion version) {
    return (serverVersion.ordinal() >= version.ordinal());
  }

  /**
   * Checks if the current server version is at or below the specified version.
   *
   * @param version The version to compare against.
   * @return True if the current server version is at or below the specified version, otherwise false.
   */
  public static boolean isServerVersionAtOrBelow(ServerVersion version) {
    return (serverVersion.ordinal() <= version.ordinal());
  }

  /**
   * Checks if the current server version is below the specified version.
   *
   * @param version The version to compare against.
   * @return True if the current server version is below the specified version, otherwise false.
   */
  public static boolean isServerVersionBelow(ServerVersion version) {
    return (serverVersion.ordinal() < version.ordinal());
  }
}
