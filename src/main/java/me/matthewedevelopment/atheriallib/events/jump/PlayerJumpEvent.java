package me.matthewedevelopment.atheriallib.events.jump;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJumpEvent   extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;



    public PlayerJumpEvent(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

}
