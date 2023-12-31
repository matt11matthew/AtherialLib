package me.matthewedevelopment.atheriallib.command.spigot;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.spigot.config.SelfCommandConfig;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Matthew E on 12/30/2023 at 9:49 PM for the project AtherialLib
 */
public  abstract class AtherialLibSelfSubCommand<A extends AtherialLib, T extends YamlConfig, C extends AtherialLibSpigotCommand<T, A>> extends SpigotCommand {
    protected C parentCommand;
    protected A main;
    protected T config;



    public AtherialLibSelfSubCommand(String name, C parentCommand, A main) {
        super(name);
        this.parentCommand = parentCommand;
        this.main = main;
        this.config = parentCommand.config;


    }



}
