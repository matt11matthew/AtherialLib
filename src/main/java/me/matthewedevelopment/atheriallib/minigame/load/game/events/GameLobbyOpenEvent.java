package me.matthewedevelopment.atheriallib.minigame.load.game.events;

import me.matthewedevelopment.atheriallib.minigame.load.game.GameLoadedGameMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameLobbyOpenEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameLoadedGameMap gameMap;

    public GameLobbyOpenEvent(GameLoadedGameMap gameMap) {
        this.gameMap = gameMap;
    }


    public HandlerList getHandlers() {
        return handlers;
    }


    public GameLoadedGameMap getGame() {
        return gameMap;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

