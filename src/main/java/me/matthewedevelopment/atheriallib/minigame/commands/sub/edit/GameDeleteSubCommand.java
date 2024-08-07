package me.matthewedevelopment.atheriallib.minigame.commands.sub.edit;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import me.matthewedevelopment.atheriallib.minigame.GameMap;
import me.matthewedevelopment.atheriallib.minigame.GameMapConfig;
import me.matthewedevelopment.atheriallib.minigame.GameMapRegistry;
import me.matthewedevelopment.atheriallib.minigame.commands.GameMapCommand;
import me.matthewedevelopment.atheriallib.utilities.ListUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameDeleteSubCommand extends AtherialLibSelfSubCommand<AtherialLib, GameMapConfig, GameMapCommand> {
    public GameDeleteSubCommand(GameMapCommand parentCommand, AtherialLib main) {
        super("delete", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.GAME_MAP_DELETE_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length!=1){
            CommandUtils.sendCommandUsage(sender, "/"+parentCommand.label+" delete", "(name)");
            return;
        }

        GameMapRegistry  dungeonRegistry = GameMapRegistry.get();
        if (!dungeonRegistry.isGameMap(args[0])){
            config.GAME_MAP_DOESNT_EXISTS.send(sender,s -> colorize(s).replace("%name%", args[0]));
            return;
        }
        GameMap byName = dungeonRegistry.getByName(args[0]);

        final String name = byName.getName();
        dungeonRegistry.deleteGameMap(byName, () -> {
            config.GAME_MAP_DELETED.send(sender,s -> colorize(s).replace("%name%", name));
        });
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        ArrayList<String> strings = GameMapRegistry.get().getMap().values().stream().map(GameMap::getName).collect(Collectors.toCollection(ArrayList::new));
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
