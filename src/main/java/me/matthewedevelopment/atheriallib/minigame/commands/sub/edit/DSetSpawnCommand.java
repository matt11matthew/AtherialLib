package me.matthewedevelopment.atheriallib.minigame.commands.sub.edit;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.commands.DungeonCommand;
import me.matthewe.extraction.dungeon.load.DungeonMode;
import me.matthewe.extraction.dungeon.load.LoadedDungeon;
import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import me.matthewedevelopment.atheriallib.minigame.GameMapConfig;
import me.matthewedevelopment.atheriallib.minigame.commands.GameMapCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;


/**
 * Created by Matthew E on 6/16/2024 at 2:00 PM for the project AtherialLib
 */
public class DSetSpawnCommand extends  AtherialLibSelfSubCommand<AtherialLib, GameMapConfig, GameMapCommand> {
    public DSetSpawnCommand(DungeonCommand parentCommand, Extraction main) {
        super("setlobby", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.SET_D_SPAWN_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;


        DungeonRegistry dungeonRegistry = DungeonRegistry.get();
        LoadedDungeon found = dungeonRegistry.getLoadedDungeonMap().values().stream().filter(value -> value.isPlayerInDungeon(player.getUniqueId())).findFirst().orElse(null);
        if (found==null || found.getDungeonMode()!= DungeonMode.EDIT) {
            config.MUST_BE_IN_EDIT_D_MSG.send(sender);
            return;
        }
        dungeonRegistry.setDungeonLobby(found.getDungeonID(),player.getLocation());
        config.D_SET_LOBBY_SPAWN_MSG.send(sender,s -> colorize(s).replace("%id%", found.getDungeonID().toString()));



    }

    @Override
    public List<HelpSubCommand> getHelp(String[] strings) {
        return Arrays.asList(HelpSubCommand.builder()

                .permission(permission)
                .command(parentCommand.label + " setlobby").build());
    }
}
