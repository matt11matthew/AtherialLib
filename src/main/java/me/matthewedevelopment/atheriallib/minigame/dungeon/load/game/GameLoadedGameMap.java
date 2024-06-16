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
import me.matthewedevelopment.atheriallib.playerdata.io.NameInfo;
import me.matthewedevelopment.atheriallib.utilities.number.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

public class GameLoadedGameMap extends LoadedDungeon {
//    public static final long TIME_FLOOR = 1L
//            ;
    private GameState gameState;
    private HashSet<NameInfo> players;
    private long timeLeft;

    public  HashSet<NameInfo> getPlayerSet() {
        return players;
    }

    private GameEnemies enemies;

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    private GameDungeonScoreboard gameDungeonScoreboard;
    private GameLootChests loot;

    private GameExtractionPoints extractionPts;

    private GameFloorManager floorManager;



    public GameLoadedGameMap(UUID dungeon, UUID sessionId) {
        super(dungeon, sessionId, DungeonMode.LIVE);
        this.gameState=  GameState.LOBBY;

        loot =new GameLootChests(this);


        players= new HashSet<>();
        enemies = new GameEnemies(this);
        gameDungeonScoreboard=new GameDungeonScoreboard(this);
        loadFloors();
        extractionPts=new GameExtractionPoints(this);


    }

    private void loadFloors() {
        floorManager = new GameFloorManager(this);

        Map<Integer, GameFloor> floorMap = new HashMap<>();


        List<Floor> floorIdsByDungeon = FloorRegistry.get().getFloorIdsByDungeon(dungeon).stream().map(uuid -> FloorRegistry.get().getMap().get(uuid)).collect(Collectors.toList());
        floorIdsByDungeon.sort((o1, o2) -> o2.getNumber()-o1.getNumber());


        int high = 0;
        boolean firstPassed = false;

//        long toAdd = TimeUnit.MINUTES.toMillis(1);

        int tim = 1;
        for (int i = 0; i <floorIdsByDungeon.size(); i++) {
            Floor floor = floorIdsByDungeon.get(i);

            high = Math.max(high, floor.getNumber());

            boolean first = !firstPassed;
            boolean last = !(i < floorIdsByDungeon.size()-1);
//            long close = System.currentTimeMillis();
//            if (first){
//                close=1000*60+(30*1000);
//            } else {
//                close = 0;
//
//            }




            GameFloor  gameFloor = new GameFloor(floor.getNumber(),floor.getUuid(),

                    null,false,last, first);
//            toAdd+=TimeUnit.MINUTES.toMillis(1);

            firstPassed=true;

            floorMap.put(floor.getNumber(), gameFloor);

        }
        List<Integer> toIncrease = new ArrayList<>();

        for (Integer i : floorMap.keySet()) {

            if (floorMap.containsKey((i-1))){
                toIncrease.add(i);

            }
        }
        for (Integer i : toIncrease) {
            GameFloor gameFloor = floorMap.get(i);

            gameFloor.setNextFloor(floorMap.get((i-1)).getId());
        }
        floorManager.setTopFloor(high);
        floorManager.setCurrentFloor(high);
        floorManager.setFloorMap(floorMap);
    }

    public GameFloor getFloor(int number) {
        return floorManager.getFloor(number);

    }
    public GameEnemies getEnemies() {
        return enemies;
    }

    public static Optional<LoadedDungeon> getCurrentGameDungeon(Player player) {
       return DungeonRegistry.get().getLoadedDungeonMap().values()
                .stream()
                .filter(loadedDungeon -> loadedDungeon.getDungeonMode() == DungeonMode.LIVE)
                .filter(loadedDungeon -> loadedDungeon.getPlayers().contains(player)).findFirst();
    }

    @Override
    public void onSessionEnd(Player player) {

        gameDungeonScoreboard.end(player);

    }

    private boolean countdown = true;
    private long timeUntilStart = System.currentTimeMillis()+(1000L *Extraction.getInstance().getExtractionConfig().LOBBY_COUNTDOWN);

    @Override
    public void update() {
        if (getWorld()!=null) {
            loot.checkChests();
            extractionPts.update();
            boolean b = handleEndConditions();
            if (b) return;
        }



        if (isStarted()){
            floorManager.update();
            enemies.update();
        }
        gameDungeonScoreboard.cleanUpBoardMap();
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
//        Bukkit.getServer().broadcastMessage(players.size()+"");
        if (noFloorFound) {
            end = true;
        } else if (forceEnd) {
            end = true;

//            Bukkit.getServer().broadcastMessage("No floor found");
        } else if (players.isEmpty()){
            if (isStarted()){

                end = true;
            }
//            Bukkit.getServer().broadcastMessage("Empty");
        } else if (System.currentTimeMillis()> timeLeft && timeLeft!=0) {
//            Bukkit.getServer().broadcastMessage("out of time");
            end = true;
        }
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
    private boolean noFloorFound = false;


    private void sendActionBars() {
        ExtractionConfig config = Extraction.getInstance().getExtractionConfig();
        if (countdown) {
            if (System.currentTimeMillis() > timeUntilStart) {
                timeUntilStart = 0;
                countdown = false;
                getPlayers().forEach(player -> new ActionBarMessage(colorize(config.STARTING)
                        .replace("%time%", TimeUtils.formatMsTime(timeUntilStart - System.currentTimeMillis()))
                        .replace("%name%", getDungeon().getName()), 1).send(player));
                gameState = GameState.STARTED;

                onStart();

            } else {

                getPlayers().forEach(player -> new ActionBarMessage(colorize(config.GAME_IN_LOBBY_CNT_DOWN_ACTION_MESSAGE)
                        .replace("%time%", TimeUtils.formatMsTime(timeUntilStart - System.currentTimeMillis()))
                        .replace("%name%", getDungeon().getName()), 1).send(player));
            }
            return;
        }
        getPlayers().forEach(player -> {
            if (this.gameState == GameState.STARTED) {
                if (hasFloor(player)) {
                    Floor foundFloor = FloorRegistry.get().getMap().get(getCurrentFloorId(player));

                    GameFloor floor = getFloorManager().getFloor(foundFloor.getNumber());
                    int n =floor.getNumber();
                    if (floorManager.isLastFloor(n)){
                        long  l = timeLeft -  System.currentTimeMillis();
                        String time = TimeUtils.formatMsTime(l);
                        new ActionBarMessage(colorize(config.GAME_ON_FLOOR_ACTION_MESSAGE_GAME_ENDING

                        ).replace("%game_time_remaining%",time ).replace("%floor%", foundFloor.getNumber() + "").replace("%name%", getDungeon().getName()), 1).send(player);

                    } else if (floorManager.isCurrentFloor(n)){

                        long  l = getFloorManager().getNextFloorTime() - System.currentTimeMillis();
                        String time = TimeUtils.formatMsTime(l);
                        new ActionBarMessage(colorize(config.GAME_ON_FLOOR_ACTION_MESSAGE_1

                        ).replace("%floor_time_remaining%",time ).replace("%floor%", foundFloor.getNumber() + "").replace("%name%", getDungeon().getName()), 1).send(player);

                    } else {


                        long l = getFloorManager().getCloseTimer() - System.currentTimeMillis();

                        if (l>0){
                            String time = TimeUtils.formatMsTime(l);

                            new ActionBarMessage(colorize(config.GAME_ON_FLOOR_ACTION_MESSAGE

                            ).replace("%floor_time_remaining%", time).replace("%floor%", foundFloor.getNumber() + "").replace("%name%", getDungeon().getName()), 1).send(player);

                        }
                    }
                } else {
                    if (System.currentTimeMillis()>timeStarted&&timeStarted!=0){

                        config.KICKED_D.send(player);
                        player.teleport(Extraction.getInstance().getMainSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);//KICK THEM OUT!!!
                        LobbyHandler.get().onTeleportToSpawn(player);
                    }
                }
            } else {
                new ActionBarMessage(colorize(config.GAME_IN_LOBBY_ACTION_MESSAGE).replace("%name%", getDungeon().getName()), 1).send(player);
            }
        });
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
        extractionPts.load();


    }

    public long getTimeLeft() {
        return timeLeft;
    }



    public void noFloorFound() {
        noFloorFound = true;
    }

    public void addPlayer(Player player) {
        players.add(NameInfo.of(player));
    }

    public boolean isFloorOpen(int floorNumber) {
       return floorManager.isFloorOpen(floorNumber);
    }

    public GameFloorManager getFloorManager() {
        return floorManager;
    }
}
