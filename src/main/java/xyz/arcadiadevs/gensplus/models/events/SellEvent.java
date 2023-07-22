package xyz.arcadiadevs.gensplus.models.events;

/**
 * The SellEvent class represents a sell event in GensPlus. It is a specific type of event
 * where players can sell items for a multiplier.
 */
public class SellEvent extends Event {

  /**
   * Constructs a new SellEvent with the specified multiplier and name.
   *
   * @param multiplier The multiplier value associated with the sell event.
   * @param name       The name of the sell event.
   */
  public SellEvent(long multiplier, String name) {
    super(multiplier, name);
  }
}

