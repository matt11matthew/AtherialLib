package me.matthewedevelopment.atheriallib.minigame.dungeon.commands.sub.edit;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.Dungeon;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.commands.DungeonCommand;
import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import me.matthewedevelopment.atheriallib.minigame.GameMapConfig;
import me.matthewedevelopment.atheriallib.utilities.ListUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaDeleteSubCommand extends AtherialLibSelfSubCommand<AtherialLib, GameMapConfig, ArenaCommand> {
    public ArenaDeleteSubCommand(DungeonCommand parentCommand, Extraction main) {
        super("delete", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.ARENA_DELETE_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length!=1){
            CommandUtils.sendCommandUsage(sender, "/"+parentCommand.label+" delete", "(name)");
            return;
        }

        DungeonRegistry  dungeonRegistry = DungeonRegistry.get();
        if (!dungeonRegistry.isDungeon(args[0])){
            config.D_DOESNT_EXISTS.send(sender,s -> colorize(s).replace("%name%", args[0]));
            return;
        }
        Dungeon byName = dungeonRegistry.getByName(args[0]);

        final String name = byName.getName();
        dungeonRegistry.deleteDungeon(byName, () -> {
            config.D_DELETED.send(sender,s -> colorize(s).replace("%name%", name));
        });
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        ArrayList<String> strings = DungeonRegistry.get().getMap().values().stream().map(Dungeon::getName).collect(Collectors.toCollection(ArrayList::new));
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
                .command(parentCommand.label + " delete").build());
    }
}
