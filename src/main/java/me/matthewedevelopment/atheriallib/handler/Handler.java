package me.matthewedevelopment.atheriallib.handler;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.AtherialCommand;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSpigotCommand;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Matthew E on 12/31/2023 at 12:29 AM for the project Extraction
 */
public abstract class Handler<T extends JavaPlugin, C  extends YamlConfig> implements Listener {
    protected C c;
    protected C config;
    protected T core;

    private HandlerPriority unloadPriority;
    private HandlerPriority loadPriority;

    public abstract void onLoad();
    protected boolean enabled;

    public abstract void reload();

    public Handler(T  core, C config, HandlerPriority unloadPriority, HandlerPriority loadPriority) {
        this.core = core;
        this.unloadPriority = unloadPriority;
        this.loadPriority = loadPriority;
        this.enabled = false;
        this.c = config;
        this.config = c;

    }

//    public final <T extends AtherialRedisPacket> void registerListener(Class<T> msg, AtherialPacketListener<T> listener) {
//
//        StaffFramework.getInstance().getAtherialRedis().registerListener(msg,listener);
//    }

    public HandlerPriority getLoadPriority() {
        return loadPriority;
    }

    public void registerCommand(AtherialLibSpigotCommand spigotCommand) {
            AtherialLib.getInstance().registerCommand(spigotCommand);
    }

    public void registerCommand(AtherialCommand atherialCommand) {
        AtherialLib.getInstance().registerAtherialCommand(atherialCommand);
    }

    public C getC() {
        return c;
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener,core);
    }

    public HandlerPriority getUnloadPriority() {
        return unloadPriority;
    }

    public void load() {
        boolean error = false;
        try {
            this.core.getLogger().info("[" + getClass().getSimpleName() + "] Loading...");
            core.getServer().getPluginManager().registerEvents(this,core);
            this.onLoad();
            enabled = true;
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        } finally {
            if (!error) enabled = true;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void postLoad() {
        this.core.getLogger().info("["+getClass().getSimpleName()+"] Loaded.");
    }

    public void unload(){
        this.onUnload();
    }

    public abstract void onUnload();


    public T getCore() {
        return core;
    }
}