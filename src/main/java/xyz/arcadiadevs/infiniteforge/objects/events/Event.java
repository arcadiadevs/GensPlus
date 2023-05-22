package xyz.arcadiadevs.infiniteforge.objects.events;

import lombok.Getter;

public abstract class Event {

  @Getter
  protected final long multiplier;

  public Event(long multiplier) {
    this.multiplier = multiplier;
  }

}
