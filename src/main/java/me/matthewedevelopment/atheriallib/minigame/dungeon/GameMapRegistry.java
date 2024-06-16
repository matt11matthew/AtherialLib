package me.matthewedevelopment.atheriallib.minigame.dungeon;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.database.registry.DataObjectRegistry;
import me.matthewedevelopment.atheriallib.minigame.dungeon.load.LoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.dungeon.load.edit.EditLoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.GameLoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.GameState;
import me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.events.GameLobbyOpenEvent;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;
import me.matthewedevelopment.atheriallib.utilities.file.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

public class GameMapRegistry extends DataObjectRegistry<GameMap> {

    private Map<UUID, LoadedGameMap> loadedDungeonMap;
    private GameMapConfig config;
    public GameMapRegistry( ) {
        super(GameMap.class);
        config=GameMapConfig.get();

        this.loadedDungeonMap= new HashMap<>();

    }

    public static GameMapRegistry get() {
        return AtherialLib.getInstance().getArenaHandler().getDungeonRegistry();
    }

    @Override
    public void onRegister() {
        File file = new File(AtherialLib.getInstance().getDataFolder(), "worlds");
        if (!file.exists())file.mkdirs();


    }

    public Map<UUID, LoadedGameMap> getLoadedDungeonMap() {
        return loadedDungeonMap;
    }

    public boolean isEditing(String dungeonName) {
        for (LoadedGameMap value : loadedDungeonMap.values()) {
            if (value.getGameMap().getName().equalsIgnoreCase(dungeonName)){

                if (value.getGameMap().isEditing()) {
                    return true;
                }
            }
        }
        return false;
    }

    public GameMap getByName(String name) {
        for (GameMap value : map.values()) {
            if (value.getName().equalsIgnoreCase(name))return value;
        }
        return null;
    }


    public boolean isDungeon(String name) {
       return map.values().stream().anyMatch(gameMap -> gameMap.getName().equalsIgnoreCase(name));
    }

    public void deleteDungeon(GameMap gameMap, Runnable runnable) {
        List<LoadedGameMap> toUnload  = new ArrayList<>();
        final String zipFileName = gameMap.getZipFileName();
        try {
            for (LoadedGameMap value : loadedDungeonMap.values()) {
                if (value.getDungeonID().equals(gameMap.getUUID())){
                    toUnload.add(value);
                }
            }
            for (LoadedGameMap loadedGameMap : toUnload) {
                unloadAsync(loadedGameMap,() -> {
                }, true);
            }
        } catch (Exception e) {
            System.err.println("ERROR");
            System.err.println("ERROR");
            System.err.println("ERROR");
            e.printStackTrace();
        }
        toUnload.clear();

        AtherialTasks.runAsync( () -> {
            deleteSync(gameMap.getUUID());
            File worldFile =new File(AtherialLib.getInstance().getDataFolder(), "worlds/"+ zipFileName);

            worldFile.delete();
            runnable.run();

        });


        //TODO
    }

    public void createDungeon(Player player, GameMap gameMap){

        gameMap.setEditing(true);

        String zipFileName = gameMap.getUUID().toString()+".zip";
        File newFileName =new File(AtherialLib.getInstance().getDataFolder(), "worlds/"+zipFileName);
        try {
            Files.copy(new File(AtherialLib.getInstance().getDataFolder(),config.DEFAULT_WORLD_ZIP).toPath(),
                    newFileName.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();

        }

        gameMap.setZipFileName(zipFileName);

        if (AtherialLib.getInstance().isDebug()){
            player.sendMessage(zipFileName);
        }
        insertAsync(gameMap,() -> {
            config.GAME_MAP_CREATE_MSG.send(player,s -> colorize(s).replace("%name%", gameMap.getName()));
            EditLoadedGameMap editLoadedDungeon = new EditLoadedGameMap(gameMap.getUUID(), UUID.randomUUID());
            gameMap.setEditSessionId(editLoadedDungeon.getSessionId());
            loadedDungeonMap.put(editLoadedDungeon.getSessionId(),editLoadedDungeon);
            editLoadedDungeon.loadAsync(loadedDungeon -> {
                Location spawnLocation = loadedDungeon.getSpawnLocation();
                player.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            });

        });
    }


    public void unloadAsync(LoadedGameMap value, Runnable runnable, boolean delete) {
        FloorRegistry.get().saveAllFloorsByDungeonIdAsync(value.getDungeonID(), ()->{});
        World world = value.getWorld();
        for (Player player : world.getPlayers()) {
            value.onSessionEnd(player);
            player.teleport(Extraction.getInstance().getMainSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
        Bukkit.unloadWorld(world, value.getGameMapMode() == DungeonMode.EDIT);
        Bukkit.getWorlds().remove(world);
        String worldName = value.getWorldName();


        AtherialTasks.runAsync(() -> {
            if (!delete) {
            }
            if (value.getGameMapMode()==DungeonMode.EDIT) {

                map.get(value.getDungeonID()).setEditing(false);
                map.get(value.getDungeonID()).setEditSessionId(null);
            } else {
                Bukkit.getServer().broadcastMessage(colorize(  config.DUNGEON_END).replace("%name%",map.get(value.getDungeonID()).getName()));
            }
            if (loadedDungeonMap.containsKey(value.getSessionId())){
                loadedDungeonMap.remove(value.getSessionId());
            }
            if (value.getGameMapMode() == DungeonMode.EDIT) {
                value.cleanupFileForZipping();
                value.zipAndMove();
                File file = new File(worldName);
                if (file.exists()) {
                    try {
                        FileUtils.deleteDirectoryRecursively(file.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                runnable.run();
            } else {
                File file = new File(worldName);
                if (file.exists()) {
                    try {
                        FileUtils.deleteDirectoryRecursively(file.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                runnable.run();
            }

        });




    }

    public void unloadAll() {
        for (LoadedGameMap value : loadedDungeonMap.values()) {
            World world = value.getWorld();
            for (Player player : world.getPlayers()) {
                player.teleport(Extraction.getInstance().getMainSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                LobbyHandler.get().onTeleportToSpawn(player);
            }

            String worldName = value.getWorldName();
            Bukkit.unloadWorld(world, value.getGameMapMode()== DungeonMode.EDIT);
            Bukkit.getWorlds().remove(world);

            if (value.getGameMapMode()== DungeonMode.EDIT){

                value.cleanupFileForZipping();
                value.zipAndMove();
                File file = new File(worldName);
                if (file.exists()) {
                    try {
                        FileUtils.deleteDirectoryRecursively(file.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                File file = new File(worldName);
                if (file.exists()) {
                    try {
                        FileUtils.deleteDirectoryRecursively(file.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
    }

    public EditLoadedGameMap getEditingDungeon(String name) {
        UUID toSearch = null;
        for (LoadedGameMap value : loadedDungeonMap.values()) {
            if (value.getGameMap().getName().equalsIgnoreCase(name)){
                toSearch = value.getGameMap().getEditSessionId();
                break;
            }

        }
        if (toSearch!=null){
            if (loadedDungeonMap.containsKey(toSearch)){

                return (EditLoadedGameMap)loadedDungeonMap.get(toSearch);
            }
        }
        return null;
    }
    public void startGame(Player player, String name) {
        GameMap byName = getByName(name);
        if (byName!=null){
            GameMap gameMap = map.get(byName.getUUID());
            if (gameMap.getLobbySpawn()==null){
                if (player!=null) {
                    config.DUNGEON_NOT_READY.send(player);
                }
                return;
            }
            GameLoadedGameMap gameLoadedDungeon = new GameLoadedGameMap(gameMap.getUUID(), UUID.randomUUID());
            gameLoadedDungeon.setGameState(GameState.LOBBY);
            Bukkit.getServer().broadcastMessage(colorize(config.D_OPEN).replace("%name%", gameMap.getName()));

            loadedDungeonMap.put(gameLoadedDungeon.getSessionId(),gameLoadedDungeon);
            GameLobbyOpenEvent gameLobbyOpenEvent =new GameLobbyOpenEvent(gameLoadedDungeon);
            Bukkit.getPluginManager().callEvent(gameLobbyOpenEvent);
            gameLoadedDungeon.loadAsync(loadedDungeon -> {
                if (player!=null){

                    player.teleport(loadedDungeon.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }

            });
        }
    }
    public void editDungeon(Player player, String name) {
        GameMap byName = getByName(name);
        if (byName!=null){
            GameMap gameMap = map.get(byName.getUUID());
            gameMap.setEditing(true);
            EditLoadedDungeon editLoadedDungeon = new EditLoadedDungeon(gameMap.getUUID(), UUID.randomUUID());
            gameMap.setEditSessionId(editLoadedDungeon.getSessionId());
            map.put(byName.getUUID(), gameMap);
            loadedDungeonMap.put(editLoadedDungeon.getSessionId(),editLoadedDungeon);
            editLoadedDungeon.loadAsync(loadedDungeon -> {

                player.teleport(loadedDungeon.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            });
        }
    }

    public void setDungeonLobby(UUID dungeonID, Location location) {
        if (map.containsKey(dungeonID)) {

            GameMap gameMap = map.get(dungeonID);
            gameMap.setLobbySpawn(ExtractionLocation.fromLocation(location));
            updateAsync(dungeonID,() -> {});
        }
    }

    public int getActiveCount(Player p) {

        int count = 0;
        for (LoadedGameMap ld : loadedDungeonMap.values()) {
            if (ld.getGameMapMode()==DungeonMode.EDIT){
                if (!p.hasPermission(config.D_EDIT_PERM)){
                    continue;
                }
            }
            count++;
        }
        return count;
    }
}
