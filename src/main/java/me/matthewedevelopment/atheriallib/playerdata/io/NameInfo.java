package me.matthewedevelopment.atheriallib.playerdata.io;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class NameInfo {
    private UUID uuid;
    private String username;

    public NameInfo(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public static NameInfo of(Player player) {
        return new NameInfo(player.getUniqueId(), player.getName());

    }

    public static NameInfo of(UUID uuid, String username) {
        return new NameInfo(uuid, username);
    }

    public Player toPlayer (){
        return Bukkit.getPlayer(uuid);
    }

    public boolean isOnline() {
        Player player = toPlayer();
        if (player==null)return false;
        return player.isOnline();
    }
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameInfo nameInfo = (NameInfo) o;
        return Objects.equals(uuid, nameInfo.uuid) && Objects.equals(username, nameInfo.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, username);
    }

    public String getUsername() {
        return username;
    }
}