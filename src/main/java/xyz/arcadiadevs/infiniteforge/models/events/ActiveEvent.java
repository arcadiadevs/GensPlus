package xyz.arcadiadevs.infiniteforge.models.events;

/**
 * The ActiveEvent record represents an active event in InfiniteForge.
 * It stores information about the event, its start time, and end time.
 */
public record ActiveEvent(Event event, long startTime, long endTime) {

  /**
   * Constructs a new ActiveEvent record with the specified event, start time, and end time.
   *
   * @param event     The Event object representing the active event.
   * @param startTime The start time of the event in milliseconds (UNIX timestamp).
   * @param endTime   The end time of the event in milliseconds (UNIX timestamp).
   */
  public ActiveEvent {
    // Empty body, as the record constructor already initializes the fields.
  }
}