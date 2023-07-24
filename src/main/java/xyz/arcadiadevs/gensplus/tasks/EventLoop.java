package xyz.arcadiadevs.gensplus.tasks;

import java.util.List;
import java.util.Random;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.gensplus.models.events.ActiveEvent;
import xyz.arcadiadevs.gensplus.models.events.Event;
import xyz.arcadiadevs.gensplus.utils.message.Messages;
import xyz.arcadiadevs.gensplus.utils.ChatUtil;
import xyz.arcadiadevs.gensplus.utils.TimeUtil;

/**
 * The EventLoop class is a BukkitRunnable task responsible for managing events in a loop. It
 * randomly selects and activates events, and transitions between events based on the specified
 * durations.
 */
public class EventLoop extends BukkitRunnable {

  @Getter
  private static ActiveEvent activeEvent = null;
  private final Plugin plugin;
  private final List<Event> events;

  /**
   * Constructs a new EventLoop with the given plugin and list of events.
   *
   * @param plugin The plugin instance.
   * @param events The list of events to cycle through.
   */
  public EventLoop(Plugin plugin, List<Event> events) {
    this.plugin = plugin;
    this.events = events;
    activeEvent = new ActiveEvent(null, System.currentTimeMillis(),
        System.currentTimeMillis() + TimeUtil.parseTimeMillis(plugin
            .getConfig()
            .getString("events.time-between-events")));
  }

  /**
   * Executes the event loop task. It randomly selects an event, activates it, and transitions to
   * the next event based on the specified durations.
   */
  @Override
  public void run() {
    Random random = new Random();
    int randomNumber = random.nextInt(events.size());

    activeEvent = new ActiveEvent(events.get(randomNumber), System.currentTimeMillis(),
        System.currentTimeMillis()
            + TimeUtil.parseTimeMillis(plugin.getConfig().getString("events.event-duration")));

    String eventEndTime = plugin.getConfig().getString("events.event-duration");


    Messages.EVENT_STARTED.format(
            "event", activeEvent.event().getName(),
            "time", eventEndTime)
        .send(true);

    new BukkitRunnable() {
      public void run() {

        String eventStartTime = plugin.getConfig().getString("events.time-between-events");

        Messages.EVENT_ENDED.format(
                "event", activeEvent.event().getName(),
                "time", eventStartTime)
            .send(true);

        activeEvent = new ActiveEvent(null, System.currentTimeMillis(),
            System.currentTimeMillis() + TimeUtil.parseTimeMillis(plugin
                .getConfig()
                .getString("events.time-between-events")));

        new EventLoop(plugin, events)
            .runTaskLaterAsynchronously(
                plugin,
                TimeUtil.parseTime(plugin
                    .getConfig()
                    .getString("events.time-between-events")));

        cancel();
      }

    }.runTaskLaterAsynchronously(
        plugin,
        TimeUtil.parseTime(plugin.getConfig().getString("events.event-duration"))
    );
  }

}