package me.matthewedevelopment.atheriallib.menu.gui.events;

import me.matthewedevelopment.atheriallib.menu.gui.speed.FastAtherialMenu;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AtherialFastMenuCloseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private FastAtherialMenu atherialMenu;

    public AtherialFastMenuCloseEvent(FastAtherialMenu atherialMenu) {
        this.atherialMenu = atherialMenu;
    }


    public FastAtherialMenu getMenu() {
        return atherialMenu;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
