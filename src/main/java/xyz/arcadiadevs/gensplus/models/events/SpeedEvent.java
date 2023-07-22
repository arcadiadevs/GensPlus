package xyz.arcadiadevs.gensplus.models.events;

/**
 * The SpeedEvent class represents a speed event in GensPlus. It is a specific type of
 * event where players can enhance the speed of certain actions with a multiplier.
 */
public class SpeedEvent extends Event {

  /**
   * Constructs a new SpeedEvent with the specified multiplier and name.
   *
   * @param multiplier The multiplier value associated with the speed event.
   * @param name       The name of the speed event.
   */
  public SpeedEvent(long multiplier, String name) {
    super(multiplier, name);
  }
}
