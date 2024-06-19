package me.matthewedevelopment.atheriallib.minigame.commands.sub.edit;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import me.matthewedevelopment.atheriallib.minigame.GameMap;
import me.matthewedevelopment.atheriallib.minigame.GameMapConfig;
import me.matthewedevelopment.atheriallib.minigame.GameMapHandler;
import me.matthewedevelopment.atheriallib.minigame.GameMapRegistry;
import me.matthewedevelopment.atheriallib.minigame.commands.GameMapCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameCreateSubCommand extends AtherialLibSelfSubCommand<AtherialLib, GameMapConfig, GameMapCommand> {
    public GameCreateSubCommand(GameMapCommand parentCommand, AtherialLib main) {
        super("create", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.GAME_MAP_CREATE_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length!=1){
            CommandUtils.sendCommandUsage(sender, "/"+parentCommand.label+" create", "(name)");
            return;
        }

        GameMapRegistry dungeonRegistry = GameMapRegistry.get();
        if (dungeonRegistry.isGameMap(args[0])){
            config.GAME_MAP_ALREADY_EXISTS.send(sender,s -> colorize(s).replace("%name%", args[0]));
            return;

        }



        dungeonRegistry.createMap(player,new GameMap(UUID.randomUUID(),
                args[0], GameMapHandler.get().getLiveClass(), GameMapHandler.get().getEditClass(),GameMapHandler.get().getGameDataClass()));

        return;
    }

    @Override
    public List<HelpSubCommand> getHelp(String[] args) {
        return Arrays.asList(HelpSubCommand.builder()
                        .permission(permission)
                        .arguments("(name)")
                .command(parentCommand.label + " create").build());
    }
}
