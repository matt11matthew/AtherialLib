package me.matthewedevelopment.atheriallib.menu.gui.events;

import me.matthewedevelopment.atheriallib.menu.gui.AtherialMenu;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
@Deprecated
public class AtherialMenuCloseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private AtherialMenu atherialMenu;

    public AtherialMenuCloseEvent(AtherialMenu atherialMenu) {
        this.atherialMenu = atherialMenu;
    }


    public AtherialMenu getMenu() {
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
