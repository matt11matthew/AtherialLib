package me.matthewedevelopment.atheriallib.minigame.dungeon.commands.sub.edit;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.commands.DungeonCommand;
import me.matthewe.extraction.dungeon.load.edit.EditLoadedDungeon;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import me.matthewedevelopment.atheriallib.utilities.ListUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Matthew E on 6/16/2024 at 2:00 PM for the project AtherialLib
 */
public class DSaveSubCommand extends AtherialLibSelfSubCommand<Extraction, ExtractionConfig, DungeonCommand> {
    public DSaveSubCommand(DungeonCommand parentCommand, Extraction main) {
        super("save", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.D_SAVE_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length!=1){
            CommandUtils.sendCommandUsage(sender, "/"+parentCommand.label+" save", "(name)");
            return;
        }

        DungeonRegistry  dungeonRegistry = DungeonRegistry.get();
        if (!dungeonRegistry.isDungeon(args[0])){
            config.D_DOESNT_EXISTS.send(sender,s -> colorize(s).replace("%name%", args[0]));
            return;

        }


        if (!dungeonRegistry.isEditing(args[0])) {
            config.D_NOT_EDITING_MSG.send(sender,s -> colorize(s).replace("%name%", args[0]));
            return;
        }
        EditLoadedDungeon editLoadedDungeon = dungeonRegistry.getEditingDungeon(args[0]);

        dungeonRegistry.unloadAsync(editLoadedDungeon,() -> {
            config.D_SAVE_MSG.send(sender,s -> colorize(s).replace("%name%", args[0]));
        }, false);
    }
    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        ArrayList<String> strings = new ArrayList<>(DungeonRegistry.get().getMap().values().stream().map(loadedDungeon -> loadedDungeon.getName()).collect(Collectors.toList()));
        if (args.length==0){
            return strings;
        }
        if (args.length==1){
            return ListUtils.filterStartsWith(strings, args[0]);
        }
        return super.getTabCompleter(sender, args);
    }

    @Override
    public List<HelpSubCommand> getHelp(String[] args) {
        return Arrays.asList(HelpSubCommand.builder()

                        .permission(config.D_SAVE_PERM)
                        .arguments("(name)")
                .command(parentCommand.label + " save").build());
    }
}
