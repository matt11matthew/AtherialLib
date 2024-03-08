package me.matthewedevelopment.atheriallib.dependency.headdatabase;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.dependency.Dependency;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class HeadDatabaseDependency  extends Dependency implements Listener {
    private HeadDatabaseAPI api;
    private boolean ready;
    public static HeadDatabaseDependency get() {
        return AtherialLib.getInstance().getDependencyManager().getDependency(HeadDatabaseDependency.class);
    }
    public HeadDatabaseDependency(AtherialLib plugin) {
        super("HeadDatabaseDependency", plugin);
    }


    public HeadDatabaseAPI getApi() {
        return api;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public void onEnable() {

    }
    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e) {
        api = new HeadDatabaseAPI();
        ready=true;
        plugin.getLogger().info("HeadDatabaseAPI Loaded");


    }
    @Override
    public void onPreEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @Override
    public void onDisable() {

    }

    public ItemStack createHead(String headDatabaseHead) {
        return api.getItemHead(headDatabaseHead);
    }
}
