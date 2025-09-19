package me.matthewedevelopment.atheriallib.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class AdventureCompat {
    private static volatile boolean initialized = false;
    private static Method sendMessageComponentMethod = null;

    private AdventureCompat() {}

    private static void init() {
        if (initialized) return;
        synchronized (AdventureCompat.class) {
            if (initialized) return;
            try {
                // Try: Player#sendMessage(Component)
                sendMessageComponentMethod =
                        Player.class.getMethod("sendMessage", Component.class);
                sendMessageComponentMethod.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
                // Not available on this server API; will use fallback
            }
            initialized = true;
        }
    }

    /**
     * Sends an Adventure Component to a Player using reflection.
     * Returns true if sent via Component (MiniMessage preserved), false if legacy fallback used.
     */
    public static boolean send(Player player, Component component) {
        init();
        if (sendMessageComponentMethod != null) {
            try {
                sendMessageComponentMethod.invoke(player, component);
                return true; // MiniMessage preserved
            } catch (Throwable ignored) {
                // If invocation fails, drop to fallback
            }
        }

        // Fallback: legacy (MiniMessage effects will be flattened)
        String legacy = LegacyComponentSerializer.legacySection().serialize(component);
        try {
            // Prefer spigot() if available for better legacy handling
            player.spigot().sendMessage(net.md_5.bungee.api.chat.TextComponent.fromLegacyText(legacy));
        } catch (Throwable t) {
            // Absolute fallback
            player.sendMessage(legacy);
        }
        return false;
    }
}
