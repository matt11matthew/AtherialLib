package me.matthewedevelopment.atheriallib.minigame.load.game;

import me.matthewedevelopment.atheriallib.minigame.GameMapConfig;
import me.matthewedevelopment.atheriallib.minigame.GameMapRegistry;
import me.matthewedevelopment.atheriallib.minigame.load.GameMapMode;
import me.matthewedevelopment.atheriallib.minigame.load.LoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.events.GameStartEvent;
import me.matthewedevelopment.atheriallib.minigame.events.GameStopEvent;
import me.matthewedevelopment.atheriallib.playerdata.io.NameInfo;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class GameLoadedGameMap<T extends LoadedGameMap<T>> extends LoadedGameMap<T> {
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


//    public static <T extends LoadedGameMap<T>> Optional<T> getCurrentGameDungeon(Player player, Class<T > clazz) {
//       return (Optional<T>) GameMapRegistry.get().getLoadedDungeonMap().values()
//                .stream()
//               .filter(loadedGameMap -> loadedGameMap.getClazz().getSimpleName().equals(clazz.getSimpleName()))
//                .filter(loadedDungeon -> loadedDungeon.getGameMapMode() == GameMapMode.EDIT)
//                .filter(loadedDungeon -> loadedDungeon.getPlayers().contains(player)).findFirst();
//    }
  public static Optional<LoadedGameMap> getCurrentGameMap(Player player) {
       return  GameMapRegistry.get().getUuidLoadedGameMapMap().values()
                .stream()
//                .filter(loadedDungeon -> loadedDungeon.getGameMapMode() == GameMapMode.EDIT)
                .filter(loadedDungeon -> loadedDungeon.getPlayers().contains(player)).findFirst();
    }



    @Override
    public void update() {
        if (getWorld()!=null) {

            boolean b = handleEndConditions();
            if (b) return;
        }




        updatePlayers();


    }

    public abstract boolean handleEndConditions();

    protected boolean forceEnd = false;

    public boolean isForceEnd() {
        return forceEnd;
    }

    public void setForceEnd(boolean forceEnd) {
        this.forceEnd = forceEnd;
    }


    public boolean defaultEndConditions() {
        boolean end = false;
        if (forceEnd){
            stop();
            return true;
        }
        if (countdown){
            if (isCountdownOver()) {
                countdown=false;
                onStart();
                return false;
            }
            return false;
        }


        if (System.currentTimeMillis()> timeLeft && timeLeft!=0) {
            end = true;
        }

        if (end) {
           stop();
            return true;
        }
        return false;
    }
    public void stop() {
        this.gameState = GameState.DONE;
        GameStopEvent event = new GameStopEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        GameMapRegistry.get().unloadAsync(this, () -> {
        }, false);
    }

    @Override
    public void fastUpdate() {
//        sendActionBars();
    }


    public boolean isStarted() {
        return gameState==GameState.STARTED;
    }


    public GameState getGameState() {
        return gameState;
    }

    public void removePlayer(Player player, boolean death) {
        GameMapConfig config =  GameMapConfig.get();
       onSessionEnd(player);
        if (players.contains(NameInfo.of(player))){

            players.remove(NameInfo.of(player));
            if (!death) {

                onLeave(NameInfo.of(player), false);
//                sendMessage(colorize(config.D_GAME_P_LEAVE).replace("%player%", player.getName()));
            } else {
                onLeave(NameInfo.of(player), true);
//                sendMessage(colorize(config.D_GAME_P_LEAVE_DEATH).replace("%player%", player.getName()));

            }

        }
    }

    public abstract void onLeave(NameInfo player, boolean death);
    public abstract void onJoin(NameInfo player);


    public abstract long getGameTimeMinutes();
    private long timeStarted = 0;
    private void onStart() {
        GameStartEvent event = new GameStartEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        this.timeLeft = TimeUnit.MINUTES.toMillis(getGameTimeMinutes())+System.currentTimeMillis();
//        this.timeLeft = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);

        timeStarted = System.currentTimeMillis()+1000L;
        gameState=GameState.STARTED;





    }

    protected long timeLeft = 0;



    public boolean isSpectator(UUID uuid){
        return false;
    }


    @Override
    public void onWorldLoad(World world) {
        getPlayers().forEach(player -> {
            if (!isSpectator(player.getUniqueId())) {
                addPlayer(player);

            }
        }); //Important
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


//        for (Player player : getPlayers()) {
//            gameDungeonScoreboard.checkBoard(player);
//        }

        for (NameInfo nameInfo : otherRemoveList) {
            if (players.contains(nameInfo)){

                players.remove(nameInfo);
                onLeave(nameInfo,false);

            }
        }


    }
    @Override
    public void onLoad() {


    }





    public void addPlayer(Player player) {
        NameInfo of = NameInfo.of(player);
        players.add(of);
        onJoin(of);
    }


    public abstract void onLobbyOpen();

    public abstract void onCountDownStart();

    private boolean countdown;

    public abstract boolean isCountdownOver();
    public void startCountDown() {
        countdown=true;
        onCountDownStart();


    }
}
