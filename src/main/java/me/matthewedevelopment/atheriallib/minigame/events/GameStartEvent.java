package me.matthewedevelopment.atheriallib.minigame.events;

import me.matthewedevelopment.atheriallib.minigame.load.game.GameLoadedGameMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent  extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameLoadedGameMap map;

    public GameStartEvent(GameLoadedGameMap map) {
        this.map = map;
    }


    public HandlerList getHandlers() {
        return handlers;
    }


    public GameLoadedGameMap getMap() {
        return map;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

