package me.matthewedevelopment.atheriallib.minigame.dungeon.commands.sub.edit;

import me.matthewe.extraction.Extraction;
import me.matthewe.extraction.ExtractionConfig;
import me.matthewe.extraction.dungeon.Dungeon;
import me.matthewe.extraction.dungeon.DungeonRegistry;
import me.matthewe.extraction.dungeon.commands.DungeonCommand;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSelfSubCommand;
import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.command.spigot.HelpSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ArenaCreateSubCommand extends AtherialLibSelfSubCommand<Extraction, ExtractionConfig, DungeonCommand> {
    public ArenaCreateSubCommand(DungeonCommand parentCommand, Extraction main) {
        super("create", parentCommand, main);
        this.playerOnly =true;
        this.permission=config.D_CREATE_PERM;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length!=1){
            CommandUtils.sendCommandUsage(sender, "/"+parentCommand.label+" create", "(name)");
            return;
        }

        DungeonRegistry  dungeonRegistry = DungeonRegistry.get();
        if (dungeonRegistry.isDungeon(args[0])){
            config.D_ALREADY_EXISTS.send(sender,s -> colorize(s).replace("%name%", args[0]));
            return;

        }



        dungeonRegistry.createDungeon(player,new Dungeon(UUID.randomUUID(),
                args[0]));

        return;
    }

    @Override
    public List<HelpSubCommand> getHelp(String[] args) {
        return Arrays.asList(HelpSubCommand.builder()
                        .permission(config.D_CREATE_PERM)
                        .arguments("(name)")
                .command(parentCommand.label + " create").build());
    }
}
