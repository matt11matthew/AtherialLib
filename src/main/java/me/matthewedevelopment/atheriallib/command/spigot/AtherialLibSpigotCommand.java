package me.matthewedevelopment.atheriallib.command.spigot;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.command.spigot.config.SelfCommandConfig;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Matthew E on 12/30/2023 at 9:46 PM for the project AtherialLib
 */
public abstract class AtherialLibSpigotCommand<T extends YamlConfig, A extends AtherialLib> extends SpigotCommand {
    public T config;
    public A main;
    public String label;
    public AtherialLibSpigotCommand(String name, T config, A main, String... aliases) {
        super(name, aliases);
        this.config = config;
        this.main = main;

        addSubCommand(new AtherialLibSelfSubCommand<A,T,AtherialLibSpigotCommand<T,A>>("help",this,main) {
            @Override
            public void run(CommandSender sender, String[] args) {
                sendHelp(sender, args);
            }

            @Override
            public List<HelpSubCommand> getHelp(String[] args) {
                return Arrays.asList(HelpSubCommand.builder()
                        .description("Help command")
                        .command(label+" help")
                        .build());
            }
        });

    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.label = label;

        if (args.length>0&&args[0].equalsIgnoreCase("help")) {
            sendHelp(sender,args);
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<HelpSubCommand> getHelp(String[] args) {
        List<HelpSubCommand> helpList = new ArrayList<>();
        this.getSubCommandMap().values().forEach(spigotCommand -> helpList.addAll(spigotCommand.getHelp(args)));
        return helpList;
    }
}
