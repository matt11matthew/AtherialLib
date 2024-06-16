package me.matthewedevelopment.atheriallib.minigame.dungeon.commands.sub.edit;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.commands.DungeonCommand;
import me.matthewe.extraction.dungeon.load.DungeonMode;
import me.matthewe.extraction.dungeon.load.LoadedDungeon;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;


/**
 * Created by Matthew E on 6/16/2024 at 2:00 PM for the project AtherialLib
 */
public class DSetSpawnCommand extends AtherialLibSelfSubCommand<Extraction, ExtractionConfig, DungeonCommand> {
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


//        LoadedDungeon loadedDungeon = new LoadedDungeon(LoadedDungeonState.LOADING, UUID.randomUUID(),"test.zip") {
//
//        };
//
//        sender.sendMessage("Loading world...");
//        sender.sendMessage(loadedDungeon.toString());
//
//        loadedDungeon.load();
//
//        AtherialTasks.runIn(() -> {
//            if (loadedDungeon.getWorld()!=null){
//                player.teleport(loadedDungeon.getWorld().getSpawnLocation());
//            }
//            sender.sendMessage(loadedDungeon.toString());
//        }, 10);
    }

    @Override
    public List<HelpSubCommand> getHelp(String[] strings) {
        return Arrays.asList(HelpSubCommand.builder()

                .permission(permission)
                .command(parentCommand.label + " setlobby").build());
    }
}
