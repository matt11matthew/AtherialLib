package me.matthewedevelopment.atheriallib.minigame.commands.sub.edit;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import me.matthewedevelopment.atheriallib.minigame.GameMapConfig;
import me.matthewedevelopment.atheriallib.minigame.GameMapRegistry;
import me.matthewedevelopment.atheriallib.minigame.commands.GameMapCommand;
import me.matthewedevelopment.atheriallib.minigame.events.GameEnterEvent;
import me.matthewedevelopment.atheriallib.minigame.load.edit.EditLoadedGameMap;
import me.matthewedevelopment.atheriallib.utilities.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameEditSubCommand extends  AtherialLibSelfSubCommand<AtherialLib, GameMapConfig, GameMapCommand> {
    public GameEditSubCommand(GameMapCommand parentCommand, AtherialLib main) {
        super("edit", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.GAME_MAP_EDIT_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length!=1){
            CommandUtils.sendCommandUsage(sender, "/"+parentCommand.label+" edit", "(name)");
            return;
        }

        GameMapRegistry dungeonRegistry = GameMapRegistry.get();
        if (!dungeonRegistry.isGameMap(args[0])){
            config.GAME_MAP_DOESNT_EXISTS.send(sender,s -> colorize(s).replace("%name%", args[0]));
            return;

        }


        if (dungeonRegistry.isEditing(args[0])){
            EditLoadedGameMap editLoadedDungeon = dungeonRegistry.getEditingGame(args[0]);
            GameEnterEvent gameEnterEvent =new GameEnterEvent(player, editLoadedDungeon);
            Bukkit.getPluginManager().callEvent(gameEnterEvent);
            ((Player) sender).teleport(editLoadedDungeon.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            return;
        }
        config.GAME_MAP_EDIT_MSG.send(sender,s -> colorize(s).replace("%name%", args[0]));

        dungeonRegistry.editGameMap(player,args[0]);

    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        ArrayList<String> strings = new ArrayList<>(GameMapRegistry.get().getMap().values().stream().map(loadedDungeon -> loadedDungeon.getName()).collect(Collectors.toList()));
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

                        .permission(config.GAME_MAP_EDIT_PERM)
                        .arguments("(name)")
                .command(parentCommand.label + " edit").build());
    }
}
