package xyz.arcadiadevs.infiniteforge.objects.events;

import lombok.Getter;

@Getter
public class ActiveEvent {

  private final Event event;

  private final long startTime;

  private final long endTime;

  public ActiveEvent(Event event, long startTime, long endTime) {
    this.event = event;
    this.startTime = startTime;
    this.endTime = endTime;
  }

}
