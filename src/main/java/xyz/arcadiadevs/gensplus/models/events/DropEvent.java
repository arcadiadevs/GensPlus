package xyz.arcadiadevs.gensplus.models.events;

/**
 * The DropEvent class represents a drop event in GensPlus.
 * It extends the base Event class and provides specific functionality for drop events.
 */
public class DropEvent extends Event {

  /**
   * Constructs a new DropEvent with the specified multiplier and name.
   *
   * @param multiplier The multiplier value associated with the drop event.
   * @param name       The name of the drop event.
   */
  public DropEvent(long multiplier, String name) {
    super(multiplier, name);
  }
}

