package me.matthewedevelopment.atheriallib.minigame.commands;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.spigot.AtherialLibSpigotCommand;
import me.matthewedevelopment.atheriallib.minigame.GameMapConfig;
import me.matthewedevelopment.atheriallib.minigame.dungeon.commands.sub.edit.*;
import me.matthewedevelopment.atheriallib.minigame.dungeon.commands.sub.game.DEndSubCommand;
import me.matthewedevelopment.atheriallib.minigame.dungeon.commands.sub.game.DStartSubCommand;
import org.bukkit.command.CommandSender;

/**
 * Created by Matthew E on 6/16/2024 at 2:00 PM for the project AtherialLib
 */
public class GameMapCommand extends AtherialLibSpigotCommand<GameMapConfig, AtherialLib> {
    public GameMapCommand(GameMapConfig config, AtherialLib main) {
        super("gamemap", config, main, "game");
        this.playerOnly = true;
        addSubCommand(new ArenaCreateSubCommand(this,main));
        addSubCommand(new ArenaEditSubCommand(this,main));
        addSubCommand(new DSaveSubCommand(this,main));
        addSubCommand(new DSetSpawnCommand(this,main));
        addSubCommand(new ArenaDeleteSubCommand(this,main));
        addSubCommand(new DStartSubCommand(this,main));
        addSubCommand(new DEndSubCommand(this,main));
//        addSubCommand(new DSetTopFloorCommand(this,main));
    }

    @Override
    public void run(CommandSender sender, String[] args) {

    }
}
