package me.matthewedevelopment.atheriallib.playerdata;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Matthew E on 12/12/2023 at 11:32 PM for the project AtherialLib
 */
public class AtherialProfileQuitEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;


    private  final AtherialProfile<?> profile;

    public AtherialProfileQuitEvent(Player player, AtherialProfile<?> profile) {
        this.player = player;
        this.profile = profile;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public AtherialProfile<?> getProfile() {
        return profile;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
