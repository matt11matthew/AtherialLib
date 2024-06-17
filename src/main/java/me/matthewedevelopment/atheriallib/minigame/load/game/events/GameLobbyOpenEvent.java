package me.matthewedevelopment.atheriallib.minigame.load.game.events;

import me.matthewedevelopment.atheriallib.minigame.load.game.GameLoadedGameMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameLobbyOpenEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameLoadedGameMap dungeon;

    public GameLobbyOpenEvent(GameLoadedGameMap dungeon) {
        this.dungeon = dungeon;
    }


    public HandlerList getHandlers() {
        return handlers;
    }


    public GameLoadedGameMap getGame() {
        return dungeon;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

