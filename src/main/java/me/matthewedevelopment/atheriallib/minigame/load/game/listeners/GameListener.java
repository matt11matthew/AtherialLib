package me.matthewedevelopment.atheriallib.minigame.load.game.listeners;

import me.matthewedevelopment.atheriallib.minigame.GameMapRegistry;
import me.matthewedevelopment.atheriallib.minigame.load.LoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.load.game.GameLoadedGameMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {



    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (LoadedGameMap value : GameMapRegistry.get().getUuidLoadedGameMapMap().values()) {
            if (value instanceof GameLoadedGameMap) {
                GameLoadedGameMap gameLoadedDungeon = (GameLoadedGameMap) value;
                if (gameLoadedDungeon.isStarted()){

                    for (Object p: value.getPlayers()) {
                        Player player = (Player) p;
                        if (player.getUniqueId().equals(event.getPlayer().getUniqueId())){

                            player.damage(100000);
                        }
                    }
                }
            }
        }
    }
}
