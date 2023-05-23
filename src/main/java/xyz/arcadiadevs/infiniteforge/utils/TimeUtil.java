package xyz.arcadiadevs.infiniteforge.utils;

/**
 * The TimeUtil class provides utility methods for time-related operations.
 */
public class TimeUtil {

  /**
   * Parses a time string into the corresponding number of ticks.
   *
   * @param time The time string to parse.
   * @return The number of ticks.
   * @throws IllegalArgumentException if an invalid unit is specified in the time string.
   */
  public static long parseTime(String time) {
    long totalTicks = 0;

    String[] parts = time.split("\\s+");

    for (String part : parts) {
      char unit = part.charAt(part.length() - 1);
      int value = Integer.parseInt(part.substring(0, part.length() - 1));

      switch (unit) {
        case 's' -> totalTicks += value * 20L; // 20 ticks per second
        case 'm' -> totalTicks += value * 20L * 60L; // 20 ticks per second, 60 seconds per minute
        case 'h' -> totalTicks += value * 20L * 60L
            * 60L; // 20 ticks per second, 60 seconds per minute, 60 minutes per hour

        // You can add additional cases for other units if needed (e.g., 'd' for days)
        default ->
            throw new IllegalArgumentException("Invalid unit specified in despawn time: " + unit);
      }
    }

    return totalTicks;
  }

  /**
   * Converts the specified number of ticks into a formatted time string in the format "HH:MM:SS".
   *
   * @param ticks The number of ticks to convert.
   * @return The formatted time string.
   */
  public static String ticksToTime(long ticks) {
    long seconds = ticks / 20L;
    long minutes = seconds / 60L;
    long hours = minutes / 60L;
    return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
  }

  /**
   * Parses a time string into the corresponding number of milliseconds.
   *
   * @param time The time string to parse.
   * @return The number of milliseconds.
   * @throws IllegalArgumentException if an invalid unit is specified in the time string.
   */
  public static long parseTimeMillis(String time) {
    long totalMillis = 0;

    String[] parts = time.split("\\s+");

    for (String part : parts) {
      char unit = part.charAt(part.length() - 1);
      int value = Integer.parseInt(part.substring(0, part.length() - 1));

      switch (unit) {
        case 's' -> totalMillis += value * 1000L; // 1000 milliseconds per second
        case 'm' -> totalMillis +=
            value * 1000L * 60L; // 1000 milliseconds per second, 60 seconds per minute
        case 'h' -> totalMillis += value * 1000L * 60L
            * 60L; // 1000 milliseconds per second, 60 seconds per minute, 60 minutes per hour

        // You can add additional cases for other units if needed (e.g., 'd' for days)
        default ->
            throw new IllegalArgumentException("Invalid unit specified in despawn time: " + unit);
      }
    }

    return totalMillis;
  }

  /**
   * Converts the specified number of milliseconds into a formatted time string in the format "HH:MM:SS".
   *
   * @param millis The number of milliseconds to convert.
   * @return The formatted time string.
   */
  public static String millisToTime(long millis) {
    long seconds = millis / 1000L;
    long minutes = seconds / 60L;
    long hours = minutes / 60L;
    return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
  }

}