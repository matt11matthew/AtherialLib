package me.matthewedevelopment.atheriallib.command.spigot.config;

import me.matthewedevelopment.atheriallib.command.spigot.CommandUtils;
import me.matthewedevelopment.atheriallib.config.yaml.YamlConfig;
import org.bukkit.command.CommandSender;

/**
 * Created by Matthew E on 12/30/2023 at 9:43 PM for the project AtherialLib
 */
public class SelfCommandConfig  {
    public String PERMISSION;
    public String HELP;
    public Usage USAGE;

    public SelfCommandConfig(String PERMISSION, String HELP, Usage USAGE) {
        this.PERMISSION = PERMISSION;
        this.HELP = HELP;
        this.USAGE = USAGE;
    }



    public static class Usage{
        public String command;
        public String[] arguments = new String[0];

        public Usage(String command, String... arguments) {
            this.command = command;
            if ((arguments != null) && (arguments.length > 0)) {
                this.arguments = arguments;
            }
        }

        public void send(CommandSender sender, String label, String[] args) {
            CommandUtils.sendCommandUsage(sender, new String(this.command).replaceAll("%label%", label), args);
        }

        public void send(CommandSender sender, String label) {
            CommandUtils.sendCommandUsage(sender, new String(this.command).replaceAll("%label%", label), arguments);
        }
    }

}
