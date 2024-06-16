package me.matthewedevelopment.atheriallib.minigame.dungeon.load;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.io.Callback;
import me.matthewedevelopment.atheriallib.minigame.dungeon.GameMap;
import me.matthewedevelopment.atheriallib.minigame.dungeon.GameMapConfig;
import me.matthewedevelopment.atheriallib.minigame.dungeon.GameMapRegistry;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import me.matthewedevelopment.atheriallib.utilities.file.FileUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Matthew E on 12/30/2023 at 6:56 PM for the project Extraction
 */
public  abstract class LoadedGameMap<T extends LoadedGameMap<T>> {
    private LoadedGameMapState dungeonState;
    private World world;
    private String zipFileNameCache;
    private Class<T> clazz;

    public abstract void onWorldLoad(World world);
    public abstract void onSessionEnd(Player player);
    private UUID sessionId;

    protected UUID dungeon;
    private GameMapMode gameMapMode;

    public UUID getSessionId() {
        return sessionId;
    }

    public GameMapMode getGameMapMode() {
        return gameMapMode;
    }
    public void sendMessage(String text){
        for (Player player : getPlayers()) {
            player.sendMessage(ChatUtils.colorize(text));
        }
    }


    public void sendDebugMessage(String text) {
        for (Player player : getPlayers()) {
            if (!player.hasPermission(GameMapConfig.get().DEBUG_PERM))continue;
            player.sendMessage(ChatUtils.colorize(text));
        }
    }
    public List<Player> getPlayers() {
        World world1 = getWorld();
        if (world1==null||!Bukkit.getWorlds().contains(world1)) return new ArrayList<>();
        return world1.getPlayers();
    }


    public GameMap getGameMap() {
        return GameMapRegistry.get().getMap().get(dungeon);
    }

    public UUID getDungeonID(){
        return dungeon;
    }
    public abstract void update();
    public abstract void fastUpdate();
    @Override
    public String toString() {
        return dungeonState.toString()+": ("+zipFileNameCache+")";
    }
    public LoadedGameMap(UUID dungeon, UUID sessionId, GameMapMode gameMapMode, Class<T> clazz){
        this.dungeon = dungeon;

        this.clazz=clazz;
        this.sessionId =sessionId;
        this.gameMapMode = gameMapMode;
    }


    public Class<T> getClazz() {
        return clazz;
    }

    public LoadedGameMap<T> setDungeonState(LoadedGameMapState dungeonState) {
        this.dungeonState = dungeonState;
        return this;
    }



    public abstract void onLoad();


    public void loadAsync(Callback<T> loadedDungeonCallback) {
        AtherialTasks.runAsync(() -> {

            this.loadFile();
            AtherialTasks.runSync(() -> {
                this.setupWorld();
                this.setDungeonState(LoadedGameMapState.LOADED);
                loadedDungeonCallback.call((T) this);
                zipFileNameCache= getGameMap().getZipFileName();
                onWorldLoad(world);
            });



        });
        onLoad();

    }

    public String getZipFileNameCache() {
        return zipFileNameCache;
    }

    public World getWorld() {
        return world;
    }

    protected void setupWorld() {
        // Create world.
        WorldCreator worldCreator = new WorldCreator(getWorldName());
        worldCreator.generateStructures(false);
        World w = Bukkit.getServer().createWorld(worldCreator);
        w.setStorm(false);
        w.setAutoSave(false);
        w.setKeepSpawnInMemory(false);
        w.setPVP(false);
        w.setGameRuleValue("randomTickSpeed", "0");


        Bukkit.getWorlds().add(w);
        this.world = w;

    }


    public String getWorldName() {
       return "DUNGEON_"+sessionId.toString().replaceAll("-","").trim();
    }

    public void save() {

    }

    public void unload() {
        if (world!=null){

        }
    }
    public void cleanupFileForZipping() {


        List<File> toDelete =new  ArrayList<>();


        List<String>folders =new ArrayList<>();
        folders.add("data/");
        folders.add("playerdata/");
        folders.add("stats/");
        folders.add("datapacks/");
        folders.add("entities/");
        folders.add("poi/");

        for (String folder : folders) {

            File file2 = new File(getWorldName() + "/"+folder);
            if (file2.exists()) {
                try {
                    FileUtils.deleteDirectoryRecursively(file2.toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        File file = new File(getWorldName(), "ready.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        toDelete.add(new File(getWorldName(), "uid.dat"));
        toDelete.add(new File(getWorldName(), "session.lock"));

        for (File file2 : toDelete) {
            if (file2.exists()){
                file2.delete();
            }
        }

    }
    private void loadFile() {
        File worldFile =new File(AtherialLib.getInstance().getDataFolder(), "worlds/"+ getGameMap().getZipFileName());
        if (!worldFile.exists()){
            return;

        }
        try {
            new ZipFile(worldFile).extractAll(getWorldName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(getWorldName(), "uid.dat");
        if (file.exists()) file.delete();

    }


    public void zipAndMove() {
        File worldFile =new File(AtherialLib.getInstance().getDataFolder(), "worlds/"+ zipFileNameCache);
        ZipFile zipFile = new ZipFile(worldFile);
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);

        File folderToZip = new File(getWorldName());

        // Add folder contents to the zip file
        for (File file : folderToZip.listFiles()) {
            if (file.isDirectory()) {
                try {
                    zipFile.addFolder(file, parameters);
                } catch (ZipException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    zipFile.addFile(file, parameters);
                } catch (ZipException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public boolean isPlayerInDungeon(UUID uniqueId) {
        List<Player> players = getPlayers();
        if (players!=null&&!players.isEmpty()){
            for (Player player : players) {
                if (player.getUniqueId().equals(uniqueId)){
                    return true;
                }
            }
        }
        return false;
    }

    public Location getSpawnLocation() {
        GameMap gameMap = getGameMap();
        if (gameMap==null){
            return world.getSpawnLocation();
        }
        if (gameMap.hasSpawn()){
            return         gameMap.getLobbySpawn().toLocation(world.getName());
        }



        return world.getSpawnLocation();
    }

    public void selfUpdate() {

    }
}
