package me.matthewedevelopment.atheriallib.chat;

import org.bukkit.entity.Player;

import java.util.Optional;

public interface Chat {
    void onChat(Player player, String message);
    void onCancel(Player player);
    void onTimeout(Optional<Player> player);
}
