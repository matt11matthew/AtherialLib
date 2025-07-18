package me.matthewedevelopment.atheriallib;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class SchedulerAdapter {

    private static final FoliaLib foliaLib = AtherialLib.getInstance().getFoliaLib();

    public static void runGlobalRepeatingTask(long delayTicks, long intervalTicks, Runnable task) {
        foliaLib.getScheduler().runTimer(task, delayTicks, intervalTicks);
    }

    public static void runRepeatingTask(Player player, long delayTicks, long intervalTicks, Runnable task) {
        foliaLib.getScheduler().runAtEntityTimer(player, task, delayTicks, intervalTicks);
    }

    public static void runSync(Consumer<WrappedTask> task) {
        foliaLib.getScheduler().runNextTick(task);
    }

    public static void runAsync(Consumer<WrappedTask> task) {
        foliaLib.getScheduler().runAsync(task);
    }

    public static void runLater(Runnable task, long delayTicks) {
        foliaLib.getScheduler().runLater(task, delayTicks);
    }
}
