package me.matthewedevelopment.atheriallib.handler;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew E on 1/23/2024 at 10:34 AM for the project CloudMines
 */
public class HandlerManager<T extends JavaPlugin> {
    protected Map<String, Handler> handlers;

    private T main;

    public <H extends Handler> H get(Class<H> h) {
        if (handlers.containsKey(h.getSimpleName())){
            return (H) handlers.get(h.getSimpleName());
        }
        return null;
    }

    public HandlerManager(T main) {
        this.main = main;
        this.handlers = new HashMap<>();

    }

    public <H extends Handler> void registerHandler(H handler) {
        handlers.put(handler.getClass().getSimpleName(), handler);
    }

    public void disableHandlers() {
        this.handlers.values().stream().sorted((o1, o2) -> o2.getUnloadPriority().getPriority()-o1.getUnloadPriority().getPriority()).forEach(Handler::unload);
    }
    public void enableHandlers() {
        this.handlers.values().stream().sorted((o1, o2) -> o2.getLoadPriority().getPriority()-o1.getLoadPriority().getPriority()).forEach(Handler::load);
        this.handlers.values().stream().sorted((o1, o2) -> o2.getLoadPriority().getPriority()-o1.getLoadPriority().getPriority()).filter(Handler::isEnabled).forEach(Handler::postLoad);


    }
}
