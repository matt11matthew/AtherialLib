package me.matthewedevelopment.atheriallib.minigame;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.database.registry.DataObjectRegistry;
import me.matthewedevelopment.atheriallib.minigame.load.GameMapMode;
import me.matthewedevelopment.atheriallib.minigame.load.LoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.load.edit.EditLoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.load.game.GameLoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.load.game.GameState;
import me.matthewedevelopment.atheriallib.minigame.load.game.events.GameLobbyOpenEvent;
import me.matthewedevelopment.atheriallib.utilities.AtherialTasks;
import me.matthewedevelopment.atheriallib.utilities.file.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

public class GameMapRegistry extends DataObjectRegistry<GameMap> {

    private Map<UUID, LoadedGameMap> uuidLoadedGameMapMap;
    private GameMapConfig config;
    public GameMapRegistry( ) {
        super(GameMap.class);
        config=GameMapConfig.get();

        this.uuidLoadedGameMapMap = new HashMap<>();

    }

    public static GameMapRegistry get() {
        return AtherialLib.getInstance().getGameMapHandler().getDungeonRegistry();
    }

    @Override
    public void onRegister() {
        File file = new File(AtherialLib.getInstance().getDataFolder(), "worlds");
        if (!file.exists())file.mkdirs();


    }

    public Map<UUID, LoadedGameMap> getUuidLoadedGameMapMap() {
        return uuidLoadedGameMapMap;
    }

    public boolean isEditing(String gameName) {
        for (LoadedGameMap value : uuidLoadedGameMapMap.values()) {
            if (value.getGameMap().getName().equalsIgnoreCase(gameName)){

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


    public boolean isGameMap(String name) {
       return map.values().stream().anyMatch(gameMap -> gameMap.getName().equalsIgnoreCase(name));
    }

    public void deleteGameMap(GameMap gameMap, Runnable runnable) {
        List<LoadedGameMap> toUnload  = new ArrayList<>();
        final String zipFileName = gameMap.getZipFileName();
        try {
            for (LoadedGameMap value : uuidLoadedGameMapMap.values()) {
                if (value.getGameID().equals(gameMap.getUUID())){
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

    public void createMap(Player player, GameMap gameMap){

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

            EditLoadedGameMap editLoadedDungeon = null;
            try {
                editLoadedDungeon = (EditLoadedGameMap) gameMap.getEditClass().getConstructor(UUID.class, UUID.class).newInstance(gameMap.getUUID(), UUID.randomUUID());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                e.printStackTrace();

            }

            if (editLoadedDungeon==null){
                System.err.println("NOT LOADED PROPERLY");
                return;
            }
//            .EditLoadedGameMap(gameMap.getUUID(), UUID.randomUUID());
            gameMap.setEditSessionId(editLoadedDungeon.getSessionId());
            uuidLoadedGameMapMap.put(editLoadedDungeon.getSessionId(),editLoadedDungeon);
            editLoadedDungeon.loadAsync(loadedDungeon -> {
//                EditLoadedGameMap editLoadedGameMap = (EditLoadedGameMap) loadedDungeon;;
                Location spawnLocation = gameMap.getLobbySpawn().toLocation();
                player.teleport(spawnLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
            });

        });
    }


    public void unloadAsync(LoadedGameMap value, Runnable runnable, boolean delete) {
//        FloorRegistry.get().saveAllFloorsByDungeonIdAsync(value.getDungeonID(), ()->{});
        World world = value.getWorld();
        for (Player player : world.getPlayers()) {
            value.onSessionEnd(player);
            GameMapHandler.get().teleportToSpawn(player);
//            player.teleport(Extraction.getInstance().getMainSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
        Bukkit.unloadWorld(world, value.getGameMapMode() == GameMapMode.EDIT);
        Bukkit.getWorlds().remove(world);
        String worldName = value.getWorldName();


        AtherialTasks.runAsync(() -> {
            if (!delete) {
            }
            if (value.getGameMapMode()==GameMapMode.EDIT) {

                map.get(value.getGameID()).setEditing(false);
                map.get(value.getGameID()).setEditSessionId(null);
            } else {
//                Bukkit.getServer().broadcastMessage(colorize(  config.GAME_MAP_END_CMD_MSG).replace("%name%",map.get(value.getDungeonID()).getName()));
            }
            if (uuidLoadedGameMapMap.containsKey(value.getSessionId())){
                uuidLoadedGameMapMap.remove(value.getSessionId());
            }
            if (value.getGameMapMode() == GameMapMode.EDIT) {
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
        for (LoadedGameMap value : uuidLoadedGameMapMap.values()) {
            World world = value.getWorld();
            for (Player player : world.getPlayers()) {
                GameMapHandler.get().teleportToSpawn(player);
//                player.teleport(Ath.getInstance().getMainSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
//                LobbyHandler.get().onTeleportToSpawn(player);
            }

            String worldName = value.getWorldName();
            Bukkit.unloadWorld(world, value.getGameMapMode()== GameMapMode.EDIT);
            Bukkit.getWorlds().remove(world);

            if (value.getGameMapMode()== GameMapMode.EDIT){

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

    public EditLoadedGameMap getEditingGame(String name) {
        UUID toSearch = null;
        for (LoadedGameMap value : uuidLoadedGameMapMap.values()) {
            if (value.getGameMap().getName().equalsIgnoreCase(name)){
                toSearch = value.getGameMap().getEditSessionId();
                break;
            }

        }
        if (toSearch!=null){
            if (uuidLoadedGameMapMap.containsKey(toSearch)){

                return (EditLoadedGameMap) uuidLoadedGameMapMap.get(toSearch);
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
                    config.GAME_MAP_NOT_READY.send(player);
                }
                return;
            }


            GameLoadedGameMap gameLoadedDungeon=null;
            try {

                gameLoadedDungeon = (GameLoadedGameMap) gameMap.getGameClass().getConstructor(UUID.class, UUID.class).newInstance(gameMap.getUUID(), UUID.randomUUID());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (gameLoadedDungeon==null)return;

//            GameLoadedGameMap gameLoadedDungeon = new GameLoadedGameMap(gameMap.getUUID(), UUID.randomUUID());
            gameLoadedDungeon.setGameState(GameState.LOBBY);
//            Bukkit.getServer().broadcastMessage(colorize(config.OPEN).replace("%name%", gameMap.getName()));

            uuidLoadedGameMapMap.put(gameLoadedDungeon.getSessionId(),gameLoadedDungeon);
            GameLobbyOpenEvent gameLobbyOpenEvent =new GameLobbyOpenEvent(gameLoadedDungeon);
            Bukkit.getPluginManager().callEvent(gameLobbyOpenEvent);
            gameLoadedDungeon.loadAsync(loadedDungeon -> {
                if (player!=null){
                    GameLoadedGameMap editLoadedGameMap = (GameLoadedGameMap) loadedDungeon;
                    player.teleport(editLoadedGameMap.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }

            });
        }
    }
    public void editGameMap(Player player, String name) {
        GameMap byName = getByName(name);
        if (byName!=null){
            GameMap gameMap = map.get(byName.getUUID());
            gameMap.setEditing(true);

         try {
             EditLoadedGameMap editLoadedDungeon = (EditLoadedGameMap) gameMap.getEditClass().getConstructor(UUID.class, UUID.class).newInstance(gameMap.getUUID(), UUID.randomUUID());

//            EditLoadedGameMap editLoadedDungeon = new EditLoadedDungeon(gameMap.getUUID(), UUID.randomUUID());
             gameMap.setEditSessionId(editLoadedDungeon.getSessionId());
             map.put(byName.getUUID(), gameMap);
             uuidLoadedGameMapMap.put(editLoadedDungeon.getSessionId(),editLoadedDungeon);
             editLoadedDungeon.loadAsync(loadedDungeon -> {

                 EditLoadedGameMap editLoadedGameMap = (EditLoadedGameMap) loadedDungeon;
                 player.teleport(editLoadedGameMap.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
             });
         } catch (Exception e) {
             e.printStackTrace();
         }
        }
    }



    public int getActiveCount(Player p) {

        int count = 0;
        for (LoadedGameMap ld : uuidLoadedGameMapMap.values()) {
            if (ld.getGameMapMode()==GameMapMode.EDIT){
                if (!p.hasPermission(config.GAME_MAP_EDIT_PERM)){
                    continue;
                }
            }
            count++;
        }
        return count;
    }
}
