package xyz.arcadiadevs.infiniteforge.models.events;

import lombok.Getter;
import lombok.Setter;

/**
 * The Event class represents an event in InfiniteForge. It provides a base class for specific event
 * types.
 */
public abstract class Event {

  /**
   * The multiplier value associated with the event.
   */
  @Getter
  protected final long multiplier;

  /**
   * The name of the event.
   */
  @Getter
  @Setter
  protected String name;

  /**
   * Constructs a new Event with the specified multiplier and name.
   *
   * @param multiplier The multiplier value associated with the event.
   * @param name       The name of the event.
   */
  public Event(long multiplier, String name) {
    this.multiplier = multiplier;
    this.name = name;
  }
}
