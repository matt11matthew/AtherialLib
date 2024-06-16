package me.matthewedevelopment.atheriallib.minigame.dungeon.load.game;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.floor.Floor;
import me.matthewe.extraction.dungeon.floor.FloorRegistry;
import me.matthewe.extraction.dungeon.load.DungeonMode;
import me.matthewe.extraction.dungeon.load.LoadedDungeon;
import me.matthewe.extraction.dungeon.load.game.enemies.GameEnemies;
import me.matthewe.extraction.dungeon.load.game.events.GameStartEvent;
import me.matthewe.extraction.dungeon.load.game.events.GameStopEvent;
import me.matthewe.extraction.dungeon.load.game.extraction.GameExtractionPoints;
import me.matthewe.extraction.dungeon.load.game.floor.GameFloor;
import me.matthewe.extraction.dungeon.load.game.floor.GameFloorManager;
import me.matthewe.extraction.lobby.LobbyHandler;
import me.matthewedevelopment.atheriallib.message.message.ActionBarMessage;
import me.matthewedevelopment.atheriallib.minigame.dungeon.GameMapRegistry;
import me.matthewedevelopment.atheriallib.minigame.dungeon.load.GameMapMode;
import me.matthewedevelopment.atheriallib.minigame.dungeon.load.LoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.dungeon.load.game.events.GameStartEvent;
import me.matthewedevelopment.atheriallib.playerdata.io.NameInfo;
import me.matthewedevelopment.atheriallib.utilities.number.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

public class GameLoadedGameMap<T extends LoadedGameMap<T>> extends LoadedGameMap<T> {
    private GameState gameState;
    private HashSet<NameInfo> players;

    public  HashSet<NameInfo> getPlayerSet() {
        return players;
    }


    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }



    public GameLoadedGameMap(UUID dungeon, UUID sessionId, Class<T> clazz) {
        super(dungeon, sessionId, GameMapMode.LIVE, clazz);
        this.gameState=  GameState.LOBBY;



        players= new HashSet<>();


    }


    public static <T extends LoadedGameMap<T>> Optional<T> getCurrentGameDungeon(Player player, Class<T > clazz) {
       return (Optional<T>) GameMapRegistry.get().getLoadedDungeonMap().values()
                .stream()
               .filter(loadedGameMap -> loadedGameMap.getClazz().getSimpleName().equals(clazz.getSimpleName()))
                .filter(loadedDungeon -> loadedDungeon.getGameMapMode() == GameMapMode.EDIT)
                .filter(loadedDungeon -> loadedDungeon.getPlayers().contains(player)).findFirst();
    }

    @Override
    public void onSessionEnd(Player player) {


    }


    @Override
    public void update() {
        if (getWorld()!=null) {

            boolean b = handleEndConditions();
            if (b) return;
        }




        updatePlayers();


    }

    private boolean forceEnd = false;

    public boolean isForceEnd() {
        return forceEnd;
    }

    public void setForceEnd(boolean forceEnd) {
        this.forceEnd = forceEnd;
    }

    private boolean handleEndConditions() {
//
//        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//
//        }
        boolean end = false;

        if (System.currentTimeMillis()> timeLeft && timeLeft!=0) {
//            Bukkit.getServer().broadcastMessage("out of time");
            end = true;
        if (end) {
//            Bukkit.getServer().broadcastMessage("Ended");
            this.gameState=GameState.DONE;
            GameStopEvent event= new GameStopEvent(this);
            Bukkit.getPluginManager().callEvent(event);

            for (NameInfo player : players) {
                Player player1 = player.toPlayer();
                if (player1 != null) {
                    gameDungeonScoreboard.end(player1);
                }
            }
            DungeonRegistry.get().unloadAsync(this,() -> {}, false);
            return true;
        }
        return false;
    }

    @Override
    public void fastUpdate() {
        sendActionBars();
    }


    public boolean isStarted() {
        return gameState==GameState.STARTED;
    }


    public GameState getGameState() {
        return gameState;
    }

    public void removePlayer(Player player, boolean death) {
        ExtractionConfig config =  ExtractionConfig.get();
       onSessionEnd(player);
        if (players.contains(NameInfo.of(player))){

            players.remove(NameInfo.of(player));
            if (!death) {

                sendMessage(colorize(config.D_GAME_P_LEAVE).replace("%player%", player.getName()));
            } else {
                sendMessage(colorize(config.D_GAME_P_LEAVE_DEATH).replace("%player%", player.getName()));

            }

        }
    }




    private long timeStarted = 0;

    private void onStart() {
        GameStartEvent event = new GameStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        this.timeLeft = TimeUnit.SECONDS.toMillis((floorManager.getTIME()*floorManager.getCount())+(floorManager.getTIME()/2))+System.currentTimeMillis();
//        this.timeLeft = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
        enemies.start();

        timeStarted = System.currentTimeMillis()+1000L;

        loot.spawn();
        floorManager.start();





    }



    public GameExtractionPoints getExtractionPts() {
        return extractionPts;
    }

    @Override
    public void onWorldLoad(World world) {
        getPlayers().forEach(player -> players.add(NameInfo.of(player))); //Important
    }



    public void updatePlayers (){
        List<Player> toRemoveList = new ArrayList<>();
        List<NameInfo> otherRemoveList = new ArrayList<>();
        for (NameInfo player : players) {
            if (player.isOnline()){
                Player player1 = player.toPlayer();
                if (!player1.getWorld().getName().equalsIgnoreCase(getWorldName())){
                    toRemoveList.add(player1);
                }
            } else {
                otherRemoveList.add(player);

            }
        }
        for (Player p : toRemoveList) {
            removePlayer(p,false);
        }


        for (Player player : getPlayers()) {
            gameDungeonScoreboard.checkBoard(player);
        }

        for (NameInfo nameInfo : otherRemoveList) {
            if (players.contains(nameInfo)){

                players.remove(nameInfo);
                sendMessage(colorize(ExtractionConfig.get().D_GAME_P_LEAVE).replace("%player%", nameInfo.getUsername()));

            }
        }


    }
    @Override
    public void onLoad() {


    }





    public void addPlayer(Player player) {
        players.add(NameInfo.of(player));
    }


}
