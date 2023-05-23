package xyz.arcadiadevs.infiniteforge.tasks;

import java.util.List;
import java.util.Random;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.infiniteforge.model.EventModel;
import xyz.arcadiadevs.infiniteforge.objects.events.Event;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

public class EventLoop extends BukkitRunnable {

  @Getter
  private static Event activeEvent = null;
  private final Plugin plugin;
  private final List<Event> events;
  private final EventModel eventModel;

  public EventLoop(Plugin plugin, List<Event> events, EventModel eventModel) {
    this.plugin = plugin;
    this.events = events;
    this.eventModel = eventModel;
  }

  @Override
  public void run() {
    Random random = new Random();
    int randomNumber = random.nextInt(events.size());

    activeEvent = events.get(randomNumber);

    eventModel.reset();
    eventModel.setDuration(System.currentTimeMillis() +
        TimeUtil.parseTimeMillis(plugin
        .getConfig()
        .getString("events.event-duration")));

    new BukkitRunnable() {
      public void run() {

        activeEvent = null;

        new EventLoop(plugin, events, eventModel)
            .runTaskLaterAsynchronously(
                plugin,
                TimeUtil.parseTime(plugin
                    .getConfig()
                    .getString("events.time-between-events"))
            );

        eventModel.reset();
        eventModel.setTimeBetweenEvents(System.currentTimeMillis() +
            TimeUtil.parseTimeMillis(plugin
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
