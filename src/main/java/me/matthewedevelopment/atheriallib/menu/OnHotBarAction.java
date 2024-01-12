package me.matthewedevelopment.atheriallib.menu;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Optional;

@FunctionalInterface
public interface OnHotBarAction {
    void on(Player player, HotBarMenu hotBarMenu, int slot, HotBarClickType clickType, Optional<Block> block);
}
