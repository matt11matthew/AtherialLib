package me.matthewedevelopment.atheriallib.minigame.dungeon.commands.sub.game;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.commands.DungeonCommand;
import me.matthewe.extraction.dungeon.load.LoadedDungeon;
import me.matthewe.extraction.dungeon.load.game.GameLoadedDungeon;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * Created by Matthew E on 6/16/2024 at 2:00 PM for the project AtherialLib
 */
public class DEndSubCommand extends AtherialLibSelfSubCommand<Extraction, ExtractionConfig, DungeonCommand> {
    public DEndSubCommand(DungeonCommand parentCommand, Extraction main) {
        super("end", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.D_END_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length!=0){
            CommandUtils.sendCommandUsage(sender, "/"+parentCommand.label+" end");
            return;
        }

        DungeonRegistry  dungeonRegistry = DungeonRegistry.get();


        Optional<LoadedDungeon> currentGameDungeon = GameLoadedDungeon.getCurrentGameDungeon((Player) sender);
        if (!currentGameDungeon.isPresent()) {
            config.D_NOT_IN_GAME_MSG.send(sender);
            return;
        }
        LoadedDungeon loadedDungeon = currentGameDungeon.get();
        final String name = loadedDungeon.getDungeon().getName();
        if (loadedDungeon instanceof GameLoadedDungeon) {
            ((GameLoadedDungeon) loadedDungeon).setForceEnd(true);
        }
        config.D_END_CMD_MSG.send(sender,s -> colorize(s).replace("%name%",name));

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
