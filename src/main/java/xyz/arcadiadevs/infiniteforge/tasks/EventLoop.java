package xyz.arcadiadevs.infiniteforge.tasks;

import java.util.List;
import java.util.Random;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.infiniteforge.models.events.ActiveEvent;
import xyz.arcadiadevs.infiniteforge.models.events.Event;
import xyz.arcadiadevs.infiniteforge.statics.Messages;
import xyz.arcadiadevs.infiniteforge.utils.ChatUtil;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

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

    ChatUtil.sendBroadcast(
        Messages.EVENT_STARTED
            .replace("%event%", activeEvent.event().getName())
            .replace("%time%", eventEndTime), false);

    new BukkitRunnable() {
      public void run() {

        String eventStartTime = plugin.getConfig().getString("events.time-between-events");

        ChatUtil.sendBroadcast(
            Messages.EVENT_ENDED
                .replace("%event%", activeEvent.event().getName())
                .replace("%time%", eventStartTime), false);

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