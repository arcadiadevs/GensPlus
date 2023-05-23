package xyz.arcadiadevs.infiniteforge.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventModel {

  private long duration = 0;
  private long timeBetweenEvents = 0;

  public void reset() {
    this.duration = 0;
    this.timeBetweenEvents = 0;
  }

}
