package me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.enemies;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.dungeon.extraction.ExtractionPoint;
import me.matthewe.extraction.dungeon.extraction.ExtractionPointType;
import me.matthewe.extraction.dungeon.floor.Floor;
import me.matthewe.extraction.dungeon.load.game.GameLoadedDungeon;
import me.matthewe.extraction.dungeon.load.game.GameSpawner;
import me.matthewe.extraction.dungeon.load.game.floor.GameFloor;
import me.matthewe.extraction.spawner.Spawner;
import me.matthewe.extraction.spawner.SpawnerRegistry;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameEnemies {
    private GameLoadedDungeon game;
    private Map<UUID, UUID> spawnerByMobId  = new HashMap<>();
    private Map<UUID, GameSpawner> spawners = new HashMap<>();
    private int totalSpawned;

    public GameEnemies(GameLoadedDungeon game) {
        this.game = game;
        this.totalSpawned = 0;
    }

    public int getTotalSpawned() {
        return totalSpawned;
    }
    public void start() {
        List<Spawner> collect = SpawnerRegistry.get().getMap().values().stream().filter(spawner -> spawner.getDungeonId().equals(game.getDungeonID())).collect(Collectors.toList());
        for (Spawner spawner : collect) {
            spawners.put(spawner.getUuid(),new GameSpawner(spawner.getUuid(),spawner, 0));

            spawner.getLocation().toLocation(game.getWorldName()).getBlock().setType(Material.AIR);
        }

    }
    public void update() {

//        game.sendDebugMessage("&eON ENEMIES UPDATE " + spawners.keySet().size());
        for (GameSpawner spawner : spawners.values()) {
            if (!game.getFloorManager().isFloorOpen(spawner.getSpawner().getFloorNumber())) {
//                game.sendDebugMessage("&cFloor Closed");
                continue;
            }
            if (spawner.isReadyToSpawn()) {
                GameFloor floor = game.getFloorManager().getFloor(spawner.getSpawner().getFloorNumber());
                spawn(spawner.getSpawner(),floor);
            }
        }
    }

    public void onDeath(EntityDeathEvent event) {

        UUID uuid = spawnerByMobId.get(event.getEntity().getUniqueId());
        GameSpawner gameSpawner = spawners.get(uuid);

        totalSpawned--;
        gameSpawner.setTotalAlive(gameSpawner.getTotalAlive()-1);

        Player killer = event.getEntity().getKiller();
        if (killer!=null) {
            ExtractionPoint extractionPointToSpawnAfterMobDeath = game.getExtractionPts().findExtractionPointToSpawnAfterMobDeath(event.getEntity().getLocation(), killer);
            if (extractionPointToSpawnAfterMobDeath != null) {
                if (extractionPointToSpawnAfterMobDeath.getType()==ExtractionPointType.LEAVE){
                    game.getExtractionPts().spawnDueToEnemyDeath(extractionPointToSpawnAfterMobDeath);
                    return;

                }
                if (game.isFloorOpen(extractionPointToSpawnAfterMobDeath.getFloorNumber())) {
                    game.getExtractionPts().spawnDueToEnemyDeath(extractionPointToSpawnAfterMobDeath);
                }

            }
        }
        gameSpawner.removeMob(event.getEntity().getUniqueId());


    }
    public boolean isSpawnedMobs(LivingEntity entity) {
        return spawnerByMobId.containsKey(entity.getUniqueId());
    }

    public void onFloorOpen(GameFloor gameFloor) {
        Floor floor = gameFloor.getFloor();
        List<Spawner> collect = SpawnerRegistry.get().getMap().values().stream().filter(spawner -> spawner.getDungeonId().equals(game.getDungeonID())&&spawner.getFloorId().equals(floor.getUuid())).collect(Collectors.toList());
        collect.forEach(spawner -> spawn(spawner,gameFloor));
    }

    public void spawn(Spawner spawner, GameFloor floor) {
        GameSpawner gameSpawner = spawners.get(spawner.getUuid());
//        game.sendDebugMessage(spawner.getDelay()+"");
//        game.sendDebugMessage(spawner.getDelay()+"");
//        game.sendDebugMessage(spawner.getDelay()+"");
//        game.sendDebugMessage(spawner.getDelay()+"");
//        game.sendDebugMessage(spawner.getDelay()+"");
//        game.sendDebugMessage(spawner.getDelay()+"");
        gameSpawner.setDelay(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(spawner.getDelay()));
        if (spawner.getSpawnMin()==spawner.getSpawnMax()){

            gameSpawner.setTotalAlive(gameSpawner.getTotalAlive()+1);
            totalSpawned++;

            ActiveMob activeMob = MythicBukkit.inst().getMobManager().spawnMob(spawner.getMobName(), spawner.getLocation().toLocation(game.getWorldName()));
            activeMob.getEntity().setMetadata("dungeonID", new FixedMetadataValue(Extraction.getInstance(),game.getDungeonID().toString()));
            gameSpawner.addMob(activeMob);

            spawnerByMobId.put(activeMob.getEntity().getUniqueId(),spawner.getUuid());
            game.sendDebugMessage("&e[DEBUG] &bSpawning in 1x " +spawner.getMobName() +" because floor &f#"+floor.getNumber()+" &bis now open");

        } else {
            Random random = new Random();

            // Generate a random integer between min (inclusive) and max (inclusive)
            int randomNum = random.nextInt((spawner.getSpawnMax() - spawner.getSpawnMin()) + 1) +spawner.getSpawnMin();
            game.sendDebugMessage("&e[DEBUG] &bSpawning in "+randomNum+"x " +spawner.getMobName() +" because floor &f#"+floor.getNumber()+" &bis now open");
            totalSpawned+=randomNum;
            gameSpawner.setTotalAlive(gameSpawner.getTotalAlive()+randomNum);
            for (int i = 0; i < randomNum; i++) {

                ActiveMob activeMob = MythicBukkit.inst().getMobManager().spawnMob(spawner.getMobName(), spawner.getLocation().toLocation(game.getWorldName()));
                spawnerByMobId.put(activeMob.getEntity().getUniqueId(),spawner.getUuid());
                activeMob.getEntity().setMetadata("dungeonID", new FixedMetadataValue(Extraction.getInstance(),game.getDungeonID().toString()));
                gameSpawner.addMob(activeMob);
            }
        }
    }
}
