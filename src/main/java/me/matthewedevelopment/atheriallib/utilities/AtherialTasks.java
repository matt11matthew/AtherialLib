package me.matthewedevelopment.atheriallib.utilities;

import org.bukkit.plugin.Plugin;

/**
 * Created by Matthew E on 12/6/2023 at 12:46 AM for the project AtherialLib
 */
public class AtherialTasks {
    private static Plugin plugin;

    public static void setPlugin(Plugin plugin) {
        AtherialTasks.plugin = plugin;
    }

    public static void runIn(Runnable task, long time) {
        if (plugin == null) return;
        plugin.getServer().getScheduler().runTaskLater(plugin, task, time);
    }

    public static void runAsync(Runnable task) {
        if (plugin == null) return;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }



}
