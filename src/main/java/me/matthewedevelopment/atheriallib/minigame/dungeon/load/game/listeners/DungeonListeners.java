package me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.listeners;

import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.floor.Floor;
import me.matthewe.extraction.dungeon.load.LoadedDungeon;
import me.matthewe.extraction.dungeon.load.game.GameLoadedDungeon;
import me.matthewe.extraction.dungeon.load.game.events.FloorOpenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class DungeonListeners  implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onFloorOpen(FloorOpenEvent event) {
        GameLoadedDungeon dungeon = event.getDungeon();
        dungeon.getEnemies().onFloorOpen(event.getFloor());


        Floor floor = event.getFloor().getFloor();



    }
    @EventHandler
    public void onBlockPLace(BlockPlaceEvent event) {
        if (event.isCancelled())return;
        Optional<LoadedDungeon> currentGameDungeon = GameLoadedDungeon.getCurrentGameDungeon(event.getPlayer());
        if (currentGameDungeon.isPresent()&&currentGameDungeon.get() instanceof GameLoadedDungeon){
            event.setCancelled(true);
            ExtractionConfig.get().NO_BLOCK_PLACE.send(event.getPlayer());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())return;
        Optional<LoadedDungeon> currentGameDungeon = GameLoadedDungeon.getCurrentGameDungeon(event.getPlayer());
        if (currentGameDungeon.isPresent()&&currentGameDungeon.get() instanceof GameLoadedDungeon){
            event.setCancelled(true);
            ExtractionConfig.get().NO_BLOCK_BREAK.send(event.getPlayer());
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (LoadedDungeon value : DungeonRegistry.get().getLoadedDungeonMap().values()) {
            if (value instanceof GameLoadedDungeon) {
                GameLoadedDungeon gameLoadedDungeon = (GameLoadedDungeon) value;
                if (gameLoadedDungeon.isStarted()){

                    for (Player player : value.getPlayers()) {
                        if (player.getUniqueId().equals(event.getPlayer().getUniqueId())){

                            player.damage(100000);
                        }
                    }
                }
            }
        }
    }
}
