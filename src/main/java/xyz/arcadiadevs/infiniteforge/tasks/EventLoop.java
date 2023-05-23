package xyz.arcadiadevs.infiniteforge.tasks;

import java.util.List;
import java.util.Random;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.infiniteforge.objects.events.ActiveEvent;
import xyz.arcadiadevs.infiniteforge.objects.events.Event;
import xyz.arcadiadevs.infiniteforge.utils.TimeUtil;

public class EventLoop extends BukkitRunnable {

  @Getter
  private static ActiveEvent activeEvent = null;
  private final Plugin plugin;
  private final List<Event> events;

  public EventLoop(Plugin plugin, List<Event> events) {
    this.plugin = plugin;
    this.events = events;
  }

  @Override
  public void run() {
    Random random = new Random();
    int randomNumber = random.nextInt(events.size());

    activeEvent = new ActiveEvent(events.get(randomNumber), System.currentTimeMillis(),
        System.currentTimeMillis()
            + TimeUtil.parseTime(plugin.getConfig().getString("events.event-duration")));

    new BukkitRunnable() {
      public void run() {

        activeEvent = new ActiveEvent(null, System.currentTimeMillis(),
            System.currentTimeMillis() + TimeUtil.parseTime(plugin
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
