package xyz.arcadiadevs.genx.tasks;

import java.util.List;
import java.util.Random;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.arcadiadevs.genx.GenX;
import xyz.arcadiadevs.genx.objects.events.Event;
import xyz.arcadiadevs.genx.utils.TimeUtil;

public class EventLoop extends BukkitRunnable {

  @Getter
  private static Event activeEvent = null;

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

    activeEvent = events.get(randomNumber);

    new BukkitRunnable() {
      public void run() {

        activeEvent = null;

        new EventLoop(plugin, events)
            .runTaskLaterAsynchronously(
                plugin,
                TimeUtil.parseTime(plugin
                    .getConfig()
                    .getString("events.time-between-events"))
            );
        cancel();
      }

    }.runTaskLaterAsynchronously(
        plugin,
        TimeUtil.parseTime(plugin.getConfig().getString("events.event-duration"))
    );
  }
}
