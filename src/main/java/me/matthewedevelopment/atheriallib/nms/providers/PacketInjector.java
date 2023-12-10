package me.matthewedevelopment.atheriallib.nms.providers;

import io.netty.channel.ChannelDuplexHandler;
import org.bukkit.entity.Player;

/**
 * Created by Matthew Eisenberg on 7/10/2018 at 1:59 PM for the project atherialapi
 */
public abstract class PacketInjector extends ChannelDuplexHandler {
    private String name;
    private Player player;

    protected PacketInjector(String name, Player player) {
        this.name = name;
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }
}
