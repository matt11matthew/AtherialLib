package me.matthewedevelopment.atheriallib.minigame.dungeon.load.game;

import fr.mrmicky.fastboard.FastBoard;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewedevelopment.atheriallib.utilities.number.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

public class GameDungeonScoreboard {
    private GameLoadedGameMap game;
    private  Map<UUID, FastBoard> boards;

    public GameDungeonScoreboard(GameLoadedGameMap game) {
        this.game = game;
        this.boards = new HashMap<>();

    }

    public void cleanUpBoardMap() {
        Set<UUID> toRemove = new HashSet<>();
        for (UUID uuid : boards.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player==null||!player.isOnline()){
                toRemove.add(uuid);
            }

        }
        for (UUID uuid : toRemove) {
           boards.remove(uuid);
        }
    }

    private FastBoard createBoard(Player player) {
        FastBoard board = new FastBoard(player);

        board.updateTitle(colorize(ExtractionConfig.get().SCOREBOARD_TITLE).replace("%name%", DungeonRegistry.get().getMap().get( game.getDungeonID()).getName()));

        return board;
    }
    public void checkBoard(Player player) {
        if (!boards.containsKey(player.getUniqueId())){
            boards.put(player.getUniqueId(),createBoard(player));
        } else {
            updateBoard(player, boards.get(player.getUniqueId()));
        }
    }

    private void updateBoard(Player player, FastBoard fastBoard) {
        ExtractionConfig config = ExtractionConfig.get();

        List<String> scoreboardLines = new ArrayList<>();
        for (String scoreboardLine : config.SCOREBOARD_LINES) {
//            if (scoreboardLine.equalsIgnoreCase("%floor%")) {
//                if (floor!=null) {
//                    for (String s : config.FLOOR_TEXT) {
//
//                        scoreboardLines.add(colorize(s)
//                                        .replace("%floor_time_remaining%",TimeUtils.formatMsTime(game.getExtractionPoints().getTimeUntilNextFloor()-System.currentTimeMillis()) )
//                                .replace("%floor%", floor == null ? "?" : floor.getNumber() + ""));
//                    }
//                }
//            } else {
//
//            }
            scoreboardLines.add(colorize(scoreboardLine)
                    .replace("%mobs%", game.getEnemies().getTotalSpawned()+"")
                    .replace("%players%", game.getPlayerSet().size()+"")
                    .replace("%time%",getTime())
                 );
        }
        fastBoard.updateLines(scoreboardLines);


    }

    private String getTime() {
        if (!game.isStarted()){
            long millis = TimeUnit.SECONDS.toMillis((game.getFloorManager().getTIME() * game.getFloorManager().getCount()) + (game.getFloorManager().getTIME() / 2));
            return TimeUtils.formatMsTime(millis);
        }
       return TimeUtils.formatMsTime(game.getTimeLeft()-System.currentTimeMillis());
    }

    public void end(Player player) {
        if (boards.containsKey(player.getUniqueId())){
            FastBoard fastBoard = boards.get(player.getUniqueId());
            fastBoard.delete();

            boards.remove(player.getUniqueId());
        }
    }
}
