package me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.events;

import me.matthewe.extraction.dungeon.load.game.GameLoadedDungeon;
import me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.GameLoadedGameMap;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent  extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final GameLoadedGameMap dungeon;

    public GameStartEvent(GameLoadedDungeon dungeon) {
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

