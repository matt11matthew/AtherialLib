package me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.listeners;

import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.load.LoadedDungeon;
import me.matthewe.extraction.dungeon.load.game.GameLoadedDungeon;
import me.matthewe.extraction.player.DungeonProfile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobKillListener  implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            for (LoadedDungeon value : DungeonRegistry.get().getLoadedDungeonMap().values()) {
                if (value instanceof GameLoadedDungeon) {
                    GameLoadedDungeon gameLoadedDungeon = (GameLoadedDungeon) value;
                    if (gameLoadedDungeon.isPlayerInDungeon(event.getEntity().getUniqueId())){
                        DungeonProfile byPlayer = DungeonProfile.getByPlayer(((Player) event.getEntity()).getPlayer());
                        if (byPlayer!=null)byPlayer.setDeaths(byPlayer.getDeaths()+1);
                        gameLoadedDungeon.removePlayer(((Player) event.getEntity()).getPlayer(),true);
                        return;
                    }
                }
            }
        }
        if (event.getEntity() instanceof LivingEntity) {
            for (LoadedDungeon value : DungeonRegistry.get().getLoadedDungeonMap().values()) {
                if (value instanceof GameLoadedDungeon) {
                    GameLoadedDungeon gameLoadedDungeon = (GameLoadedDungeon) value;
                    if (gameLoadedDungeon.getEnemies().isSpawnedMobs(event.getEntity())){
                        if (event.getEntity().getKiller()!=null&&event.getEntity().getKiller().isOnline()){
                            DungeonProfile dungeonProfile = DungeonProfile.getByPlayer(event.getEntity().getKiller());
                            if (dungeonProfile!=null){
                                dungeonProfile.setMobKills(dungeonProfile.getMobKills()+1);
                            }

                        }
                        gameLoadedDungeon.getEnemies().onDeath(event);
                    }
                }
            }
        }
    }
}
