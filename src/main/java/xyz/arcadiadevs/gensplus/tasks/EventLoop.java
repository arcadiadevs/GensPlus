package xyz.arcadiadevs.gensplus.tasks;

import java.util.List;
import java.util.Random;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.gensplus.GensPlus;
import xyz.arcadiadevs.gensplus.models.events.ActiveEvent;
import xyz.arcadiadevs.gensplus.models.events.Event;
import xyz.arcadiadevs.gensplus.utils.TimeUtil;
import xyz.arcadiadevs.gensplus.utils.config.Config;
import xyz.arcadiadevs.gensplus.utils.config.message.Messages;

/**
 * The EventLoop class is a BukkitRunnable task responsible for managing events in a loop. It
 * randomly selects and activates events, and transitions between events based on the specified
 * durations.
 */
public class EventLoop extends BukkitRunnable {

  @Getter
  private static ActiveEvent activeEvent = null;
  private static ActiveEvent nextEvent = null;
  private final List<Event> events;
  private final long timeBetweenEvents;
  private final long eventDuration;

  /**
   * Constructs a new EventLoop with the given plugin and list of events.
   *
   * @param events The list of events to cycle through.
   */
  public EventLoop(List<Event> events) {
    this.events = events;
    this.timeBetweenEvents = TimeUtil.parseTimeMillis(Config.EVENTS_TIME_BETWEEN_EVENTS
        .getString());
    this.eventDuration = TimeUtil.parseTimeMillis(Config.EVENTS_EVENT_DURATION.getString());

    activeEvent = new ActiveEvent(null, System.currentTimeMillis(),
        System.currentTimeMillis() + timeBetweenEvents);

    setRandomNextEvent();
  }

  public void setRandomNextEvent() {
    Random random = new Random();
    int randomNumber = random.nextInt(events.size());
    nextEvent = new ActiveEvent(
        events.get(randomNumber),
        System.currentTimeMillis() + timeBetweenEvents,
        System.currentTimeMillis() + timeBetweenEvents + eventDuration
    );
  }

  /**
   * Executes the event loop task. It randomly selects an event, activates it, and transitions to
   * the next event based on the specified durations.
   */
  @Override
  public void run() {
    if (nextEvent != null && nextEvent.startTime() > System.currentTimeMillis()) {
      activeEvent = nextEvent;
      Messages.EVENT_STARTED.format(
              "event", activeEvent.event().getName(),
              "time", activeEvent.endTime())
          .send(GensPlus.getInstance().getConfig()
              .getBoolean(Config.EVENTS_BROADCAST_ENABLED.getPath()));
      nextEvent = null;
      return;
    }

    if (activeEvent.endTime() > System.currentTimeMillis()) {
      Messages.EVENT_ENDED.format(
              "event", activeEvent.event().getName(),
              "time", timeBetweenEvents)
          .send(GensPlus.getInstance().getConfig()
              .getBoolean(Config.EVENTS_BROADCAST_ENABLED.getPath()));

      activeEvent = new ActiveEvent(null, System.currentTimeMillis(),
          System.currentTimeMillis() + timeBetweenEvents);

      setRandomNextEvent();
    }
  }

}