package me.matthewedevelopment.atheriallib.menu;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface OnHotBarAction {
    void on(Player player, HotBarMenu hotBarMenu, int slot, HotBarClickType clickType);
}
