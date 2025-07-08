package me.matthewedevelopment.atheriallib.utilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AtherialTasks {
    private static Plugin plugin;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private static final boolean isFolia = detectFolia();

    public static void setPlugin(Plugin plugin) {
        AtherialTasks.plugin = plugin;
    }

    public static void shutdown() {
        scheduler.shutdown();
    }

    public static void runAsync(Runnable task) {
        if (plugin == null) return;
        scheduler.execute(task);
    }

    public static void runSync(Runnable task) {
        if (plugin == null) return;
        if (isFolia) {
            try {
                Object globalScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(Bukkit.class);
                Method execute = globalScheduler.getClass().getMethod("execute", Plugin.class, Runnable.class);
                execute.invoke(globalScheduler, plugin, task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runIn(Runnable task, long delayTicks) {
        if (plugin == null) return;
        if (isFolia) {
            try {
                Object globalScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(Bukkit.class);
                Method runDelayed = globalScheduler.getClass().getMethod("runDelayed", Plugin.class, Runnable.class, long.class);
                long delayMillis = delayTicks * 50L;
                runDelayed.invoke(globalScheduler, plugin, task, delayMillis);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static void runRegion(Player player, Runnable task) {
        if (plugin == null || player == null) return;
        if (isFolia) {
            try {
                Location location = player.getLocation();
                Object regionScheduler = Bukkit.class.getMethod("getRegionScheduler").invoke(Bukkit.class);
                Method execute = regionScheduler.getClass().getMethod("execute", Plugin.class, Location.class, Runnable.class);
                execute.invoke(regionScheduler, plugin, location, task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
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
}
