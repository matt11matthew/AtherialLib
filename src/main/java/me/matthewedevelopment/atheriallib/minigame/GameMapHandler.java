package me.matthewedevelopment.atheriallib.minigame;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.handler.Handler;
import me.matthewedevelopment.atheriallib.handler.HandlerPriority;
import me.matthewedevelopment.atheriallib.minigame.load.LoadedGameMap;
import me.matthewedevelopment.atheriallib.utilities.file.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 12/31/2023 at 12:32 AM for the project Extraction
 */
public  class GameMapHandler extends Handler<AtherialLib, GameMapConfig> implements Listener {
    private GameMapRegistry gameMapRegistry;
    private String gameName;
    private Class liveClass;
    private Class editClass;
    private Class gameDataClass;

    public void setGameDataClass(Class gameDataClass) {
        this.gameDataClass = gameDataClass;
    }

    public Class getGameDataClass() {
        return gameDataClass;
    }

    public void setEditClass(Class editClass) {
        this.editClass = editClass;
    }

    public Class getLiveClass() {
        return liveClass;
    }

    public Class getEditClass() {
        return editClass;
    }

    public void setLiveClass(Class liveClass) {
        this.liveClass = liveClass;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public GameMapHandler(AtherialLib atherialLib) {
        super(atherialLib, new GameMapConfig(), HandlerPriority.NORMAL,HandlerPriority.NORMAL);


    }

    public GameMapConfig getConfig() {
        return c;
    }

    /*
        editMode: false
    dungeons:
      1:
        timeBetween: 60
        autoStart: true
         */

    public static GameMapHandler get() {
        return AtherialLib.getInstance().getGameMapHandler();
    }


    public  void teleportToSpawn(Player player) {
        //TODO
        player.teleport(        Bukkit.getWorld("world").getSpawnLocation());

    }
    @Override
    public void onLoad() {

        cleanupWorlds();

        registerListener(this);

        gameMapRegistry = new GameMapRegistry();
        gameMapRegistry.register();


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.core,() -> {
            for (LoadedGameMap value : gameMapRegistry.getUuidLoadedGameMapMap().values()) {
                value.selfUpdate();
                value.update();
            }
        },5L,5L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.core,() -> {
            for (LoadedGameMap<? > value : gameMapRegistry.getUuidLoadedGameMapMap().values()) {
                value.fastUpdate();
            }
        },1L,1L);




    }

    @Override
    public void reload() {

    }


    @Override
    public void onUnload() {
        //TODO cleanup
        gameMapRegistry.unloadAll();

        gameMapRegistry.uploadAllSync();
//        floorRegistry.uploadAllSync();

//        cleanupWorlds();

    }

    private boolean cleanupWorld(World world) {
        for (Player player : world.getPlayers()) {
            teleportToSpawn(player);
        }
        Bukkit.unloadWorld(world, false);
        Bukkit.getWorlds().remove(world);

        File file = new File(world.getName());
        if (file.exists()) {
            try {
                FileUtils.deleteDirectoryRecursively(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

//    public abstract Location getMainSpawn();

    private void cleanupWorlds() {
        List<World> list = new ArrayList<>();

        List<String> deletingList = new ArrayList<>();

        Bukkit.getWorlds().stream().filter(world -> world.getName().startsWith("MAPS_")).forEach(world -> {
            list.add(world);
        });
        for (World world : list) {
            if (cleanupWorld(world)) {
                deletingList.add(world.getName());

            }

        }


        File path = new File(new File(new File(AtherialLib.getInstance().getDataFolder().getParent()).getAbsolutePath()).getParent());
        List<File> toDelete = new ArrayList<>();

        for (File file : path.listFiles()) {
            if (file.isDirectory() && file.getName().startsWith("MAPS_") && !deletingList.contains(file.getName())) {
                System.out.println(file.getName());
                toDelete.add(file);

            }
        }
        for (File file : toDelete) {
            try {
                FileUtils.deleteDirectoryRecursively(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }



    public GameMapRegistry getDungeonRegistry() {
        return gameMapRegistry;
    }

    public void start() {

    }

    public boolean isSetup() {
        return gameDataClass!=null&&liveClass!=null&&editClass!=null;
    }

    public void stop() {

    }
}
