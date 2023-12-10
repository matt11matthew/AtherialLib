package me.matthewedevelopment.atheriallib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by Matthew Eisenberg on 6/27/2018 at 3:50 PM for the project atherialapi
 */
public abstract class AtherialCommand extends DefaultCommand<CommandSender> implements CommandExecutor, AtherialTabCompleter {

    @Override
    public abstract void execute(CommandSender sender, String[] args);


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        execute(sender, args);
        return true;
    }
}