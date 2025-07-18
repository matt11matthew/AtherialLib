package me.matthewedevelopment.atheriallib.utilities;


import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class AtherialTasks {
    private static Plugin plugin;
    private static FoliaLib foliaLib;

    public static void setPlugin(Plugin plugin) {
        AtherialTasks.plugin = plugin;
        AtherialTasks.foliaLib = new FoliaLib(plugin);
    }

    public static void shutdown() {
        // No shutdown needed for FoliaLib scheduler
    }

    public static void runAsync(Runnable task) {
        if (plugin == null) return;
        foliaLib.getScheduler().runAsync(toConsumer(task));
    }

    public static void runSync(Runnable task) {
        if (plugin == null) return;
        foliaLib.getScheduler().runNextTick(toConsumer(task));
    }

    public static void runIn(Runnable task, long delayTicks) {
        if (plugin == null) return;
        foliaLib.getScheduler().runLater(toConsumer(task), delayTicks);
    }

    public static void runRegion(Player player, Runnable task) {
        if (plugin == null || player == null) return;
        foliaLib.getScheduler().runAtEntity(player, toConsumer(task));
    }

    private static Consumer<WrappedTask> toConsumer(Runnable runnable) {
        return (wrappedTask) -> runnable.run();
    }
}

