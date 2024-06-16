package me.matthewedevelopment.atheriallib.minigame.dungeon.load.game;

import io.lumine.mythic.core.mobs.ActiveMob;
import me.matthewe.extraction.dungeon.load.game.enemies.GameEnemy;
import me.matthewe.extraction.spawner.Spawner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameSpawner {
    private UUID uuid;
    private Spawner spawner;
    private long delay;
    private Map<UUID, GameEnemy> spawnedMobs;
    private int totalAlive;

    public GameSpawner(UUID uuid, Spawner spawner, long delay) {
        this.uuid = uuid;
        this.spawner = spawner;
        this.delay = delay;
        this.spawnedMobs = new HashMap<>();
        this.totalAlive = 0;
    }

    public int getTotalAlive() {
        return totalAlive;
    }

    public void setTotalAlive(int totalAlive) {
        this.totalAlive = totalAlive;
    }

    public boolean isReadyToSpawn() {
//        Bukkit.getServer().broadcastMessage(totalAlive+":"+(delay-System.currentTimeMillis()));
        return totalAlive==0&&System.currentTimeMillis()>delay;

    }


    public void setDelay(long delay) {
        this.delay = delay;
    }

    public Map<UUID, GameEnemy> getSpawnedMobs() {
        return spawnedMobs;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Spawner getSpawner() {
        return spawner;
    }

    public long getDelay() {
        return delay;
    }

    public void addMob(ActiveMob activeMob) {
        spawnedMobs.put(activeMob.getEntity().getUniqueId(),new GameEnemy());
    }

    public void removeMob(UUID uniqueId) {
        spawnedMobs.remove(uniqueId);
    }
}
