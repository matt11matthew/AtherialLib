package me.matthewedevelopment.atheriallib.handler;

import me.matthewedevelopment.atheriallib.AtherialLib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 1/23/2024 at 10:34 AM for the project CloudMines
 */
public class HandlerManager<T extends AtherialLib> {
    private List<Handler> handlers;

    private T main;

    public HandlerManager(T main) {
        this.main = main;
        this.handlers = new ArrayList<>();

    }

    public void registerHandler(Handler handler) {
        handlers.add(handler);
    }

    public void disableHandlers() {
        this.handlers.stream().sorted((o1, o2) -> o2.getUnloadPriority().getPriority()-o1.getUnloadPriority().getPriority()).forEach(Handler::unload);
    }
    public void enableHandlers() {
        this.handlers.stream().sorted((o1, o2) -> o2.getLoadPriority().getPriority()-o1.getLoadPriority().getPriority()).forEach(Handler::load);
        this.handlers.stream().sorted((o1, o2) -> o2.getLoadPriority().getPriority()-o1.getLoadPriority().getPriority()).filter(Handler::isEnabled).forEach(Handler::postLoad);


    }
}
