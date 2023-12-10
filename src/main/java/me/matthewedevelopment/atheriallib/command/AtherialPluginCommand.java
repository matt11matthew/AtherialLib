package me.matthewedevelopment.atheriallib.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Matthew Eisenberg on 8/18/2018 at 5:00 PM for the project atherialapi
 */
public abstract class AtherialPluginCommand extends AnnotationlessAtherialCommand {
    private Plugin plugin;
    private String[] subCommands;

    public AtherialPluginCommand(Plugin plugin, String... subCommands) {
        super(plugin.getName().replaceAll(" ", "").toLowerCase(), "/" + plugin.getName().replaceAll(" ", ""), plugin.getDescription().getName() + "'s plugin command.");
        this.plugin = plugin;

        List<String> subCommandStringList = new ArrayList<>();
        subCommandStringList.addAll(Arrays.asList(subCommands));
        subCommandStringList.add("reload");
        subCommandStringList.add("help");

        this.subCommands = subCommandStringList.toArray(new String[0]);
    }


    public abstract List<String> getHelpLines();

    public void sendHelp(CommandSender sender) {
        this.getHelpLines().forEach(helpLine -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', helpLine)));
    }

    public abstract void onReloadCommand(CommandSender sender);

    public abstract String getPermission();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            return;
        }
        if (args.length >= 1) {
            boolean hasSubCommand = false;
            for (String subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand)) {
                    hasSubCommand = true;
                }
            }
            if (!hasSubCommand) {
                this.sendHelp(sender);
                return;
            }
            this.executeSubCommand(sender, args[0].toLowerCase(),args);
        }
    }

    public abstract void onSubCommand(CommandSender sender, String subCommand, String[] args);

    public void executeSubCommand(CommandSender sender, String subCommand, String[] args) {
        switch (subCommand) {
            case "help":
                this.sendHelp(sender);
                break;
            case "reload":
                this.onReloadCommand(sender);
                break;
            default:
                this.onSubCommand(sender, subCommand, args);
                break;
        }
    }
}
