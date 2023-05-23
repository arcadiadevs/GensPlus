package xyz.arcadiadevs.infiniteforge.objects.events;

import lombok.Getter;
import lombok.Setter;

public abstract class Event {

  @Getter
  protected final long multiplier;

  @Getter @Setter
  protected String name;

  public Event(long multiplier, String name) {
    this.multiplier = multiplier;
    this.name = name;
  }

}
