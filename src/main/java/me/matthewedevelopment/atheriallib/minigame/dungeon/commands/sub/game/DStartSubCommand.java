package me.matthewedevelopment.atheriallib.minigame.dungeon.commands.sub.game;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.commands.DungeonCommand;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import me.matthewedevelopment.atheriallib.utilities.ListUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Matthew E on 6/16/2024 at 2:00 PM for the project AtherialLib
 */
public class DStartSubCommand extends AtherialLibSelfSubCommand<Extraction, ExtractionConfig, DungeonCommand> {
    public DStartSubCommand(DungeonCommand parentCommand, Extraction main) {
        super("start", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.D_START_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length!=1){
            CommandUtils.sendCommandUsage(sender, "/"+parentCommand.label+" start", "(name)");
            return;
        }

        DungeonRegistry  dungeonRegistry = DungeonRegistry.get();
        if (!dungeonRegistry.isDungeon(args[0])){
            config.D_DOESNT_EXISTS.send(sender,s -> colorize(s).replace("%name%", args[0]));
            return;

        }


        if (dungeonRegistry.isEditing(args[0])) {
            config.CANT_START_EDITING.send(sender,s -> colorize(s).replace("%name%", args[0]));
            return;
        }

        dungeonRegistry.startDungeon((Player) sender,args[0]);

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

                        .permission(this.permission)
                        .arguments("(name)")
                .command(parentCommand.label + " start").build());
    }
}
