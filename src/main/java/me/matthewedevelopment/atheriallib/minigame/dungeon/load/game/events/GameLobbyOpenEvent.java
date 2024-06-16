package me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.events;

import me.matthewe.extraction.dungeon.load.game.GameLoadedDungeon;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameLobbyOpenEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameLoadedDungeon dungeon;

    public GameLobbyOpenEvent(GameLoadedDungeon dungeon) {
        this.dungeon = dungeon;
    }


    public HandlerList getHandlers() {
        return handlers;
    }


    public GameLoadedDungeon getDungeon() {
        return dungeon;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

