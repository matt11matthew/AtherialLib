package me.matthewedevelopment.atheriallib.minigame.events;

import me.matthewedevelopment.atheriallib.minigame.load.LoadedGameMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEnterEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private final LoadedGameMap map;

    public GameEnterEvent(Player player, LoadedGameMap map) {
        this.player = player;
        this.map = map;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }


    public LoadedGameMap getMap() {
        return map;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

