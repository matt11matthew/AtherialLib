package me.matthewedevelopment.atheriallib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class SchedulerAdapter {
    private static final Plugin plugin = AtherialLib.getInstance();
    private static final boolean isFolia  = detectFolia();
    public static void runGlobalRepeatingTask(long delayTicks, long intervalTicks, Runnable task) {
        if (isFolia) {
            try {
                Object globalScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(Bukkit.class);

                Method runAtFixedRate = globalScheduler.getClass().getMethod(
                        "runAtFixedRate",
                        Plugin.class,
                        Runnable.class,
                        long.class,
                        long.class
                );

                long delayMillis = ticksToMillis(delayTicks);
                long intervalMillis = ticksToMillis(intervalTicks);

                runAtFixedRate.invoke(
                        globalScheduler,
                        plugin,
                        task,
                        delayMillis,
                        intervalMillis
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(
                    plugin,
                    task,
                    delayTicks,
                    intervalTicks
            );
        }
    }



    public static void runRepeatingTask(Player player, long delayTicks, long intervalTicks, Runnable task) {
        if (isFolia) {
            try {
                Location location = player.getLocation();
                Object regionScheduler = Bukkit.class.getMethod("getRegionScheduler").invoke(Bukkit.class);

                Method runAtFixedRate = regionScheduler.getClass().getMethod(
                        "runAtFixedRate",
                        Plugin.class,
                        Location.class,
                        Runnable.class,
                        long.class,
                        long.class
                );

                long delayMillis = ticksToMillis(delayTicks);
                long intervalMillis = ticksToMillis(intervalTicks);

                runAtFixedRate.invoke(
                        regionScheduler,
                        plugin,
                        location,
                        (Runnable) () -> task.run(),
                        delayMillis,
                        intervalMillis
                );
            } catch (Exception e) {
                e.printStackTrace();
                // fallback to legacy scheduler if needed
            }
        } else {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(
                    plugin,
                    task,
                    delayTicks,
                    intervalTicks
            );
        }
    }

    private static boolean detectFolia() {
        try {
            Bukkit.class.getMethod("getRegionScheduler");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private static long ticksToMillis(long ticks) {
        return ticks * 50L;
    }
}
