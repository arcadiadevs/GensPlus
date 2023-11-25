package xyz.arcadiadevs.gensplus.tasks;

import java.util.List;
import java.util.Random;
import lombok.Getter;
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
  private static List<Event> events;
  private static long timeBetweenEvents;
  private static long eventDuration;

  /**
   * Constructs a new EventLoop with the given plugin and list of events.
   *
   * @param events The list of events to cycle through.
   */
  public EventLoop(List<Event> events) {
    EventLoop.events = events;
    timeBetweenEvents = TimeUtil.parseTimeMillis(Config.EVENTS_TIME_BETWEEN_EVENTS
        .getString());
    eventDuration = TimeUtil.parseTimeMillis(Config.EVENTS_EVENT_DURATION.getString());

    activeEvent = new ActiveEvent(null, System.currentTimeMillis(),
        System.currentTimeMillis() + timeBetweenEvents);

    setRandomNextEvent();
  }

  private static void setRandomNextEvent() {
    Random random = new Random();
    if (events.isEmpty()) {
      nextEvent = null;
      return;
    }
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
    if (nextEvent != null && nextEvent.startTime() < System.currentTimeMillis()) {
      activeEvent = nextEvent;
      Messages.EVENT_STARTED.format(
              "event", activeEvent.event().getName(),
              "time", TimeUtil.millisToTime(eventDuration))
          .send(GensPlus.getInstance().getConfig()
              .getBoolean(Config.EVENTS_BROADCAST_ENABLED.getPath()));
      nextEvent = null;
      return;
    }

    if (activeEvent.endTime() < System.currentTimeMillis()) {
      Messages.EVENT_ENDED.format(
              "event", activeEvent.event().getName(),
              "time", TimeUtil.millisToTime(timeBetweenEvents))
          .send(GensPlus.getInstance().getConfig()
              .getBoolean(Config.EVENTS_BROADCAST_ENABLED.getPath()));

      activeEvent = new ActiveEvent(null, System.currentTimeMillis(),
          System.currentTimeMillis() + timeBetweenEvents);

      setRandomNextEvent();
    }
  }

  /**
   * Sets the next event to the given event.
   *
   * @param event The event to set as the next event.
   */
  public static void setNextEvent(Event event) {
    if (event == null) {
      return;
    }

    activeEvent = new ActiveEvent(
        event,
        System.currentTimeMillis(),
        System.currentTimeMillis() + eventDuration
    );

    nextEvent = activeEvent;

  }

  /**
   * Stops the current event.
   */
  public static void stopEvent() {
    activeEvent = new ActiveEvent(null, System.currentTimeMillis(),
        System.currentTimeMillis() + timeBetweenEvents);

    Messages.EVENT_FORCE_ENDED.format("time", TimeUtil.millisToTime(timeBetweenEvents))
        .send(Config.EVENTS_BROADCAST_ENABLED.getBoolean());

    nextEvent = null;

    setRandomNextEvent();
  }

}