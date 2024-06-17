package me.matthewedevelopment.atheriallib.minigame.commands.sub.game;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import me.matthewedevelopment.atheriallib.minigame.GameMapConfig;
import me.matthewedevelopment.atheriallib.minigame.GameMapRegistry;
import me.matthewedevelopment.atheriallib.minigame.commands.GameMapCommand;
import me.matthewedevelopment.atheriallib.minigame.load.LoadedGameMap;
import me.matthewedevelopment.atheriallib.minigame.load.game.GameLoadedGameMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Created by Matthew E on 6/16/2024 at 2:00 PM for the project AtherialLib
 */
public class GameEndSubCommand extends  AtherialLibSelfSubCommand<AtherialLib, GameMapConfig, GameMapCommand> {
    public GameEndSubCommand(GameMapCommand parentCommand, AtherialLib main) {
        super("end", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.GAME_MAP_END_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length!=0){
            CommandUtils.sendCommandUsage(sender, "/"+parentCommand.label+" end");
            return;
        }

        GameMapRegistry dungeonRegistry = GameMapRegistry.get();


        Optional<LoadedGameMap> currentGameDungeon = GameLoadedGameMap.getCurrentGameDungeon((Player) sender);
        if (!currentGameDungeon.isPresent()) {
            config.NOT_IN_GAME_MSG.send(sender);
            return;
        }
        LoadedGameMap loadedDungeon = currentGameDungeon.get();
        final String name = loadedDungeon.getGameMap().getName();
        if (loadedDungeon instanceof GameLoadedGameMap) {
            ((GameLoadedGameMap) loadedDungeon).setForceEnd(true);
        }
        config.GAME_MAP_END_CMD_MSG.send(sender,s -> colorize(s).replace("%name%",name));

    }
    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
       return new ArrayList<>();
    }

    @Override
    public List<HelpSubCommand> getHelp(String[] args) {
        return Arrays.asList(HelpSubCommand.builder()

                        .permission(this.permission)
                .command(parentCommand.label + " end").build());
    }
}
