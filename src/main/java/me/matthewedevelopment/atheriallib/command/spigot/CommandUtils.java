package me.matthewedevelopment.atheriallib.command.spigot;

import me.matthewedevelopment.atheriallib.AtherialLib;
import org.bukkit.command.CommandSender;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;
import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.message;


/**
 * Created by Matthew E on 5/25/2019 at 5:57 PM for the project atherialapi
 */
public class CommandUtils {

    public static void sendCommandUsage(CommandSender sender, String command, String... arguments) {
        if (arguments == null || arguments.length == 0) {
            message(sender, colorize(AtherialLib.COMMAND_MESSAGES.getCorrectCommandUsage()).replaceAll("%command%", command));
            return;
        }
        StringBuilder argumentsString = new StringBuilder();
        for (String argument : arguments) {
            argumentsString.append(argument).append(" ");
        }
        if (argumentsString.toString().endsWith(" ")) {
            argumentsString = new StringBuilder(argumentsString.toString().trim());
        }
        message(sender, colorize(AtherialLib.COMMAND_MESSAGES.getCorrectCommandArgumentUsage()).replaceAll("%command%", command).replaceAll("%arguments%", argumentsString.toString()));
    }

    public static void sendNoPermissionMessage(CommandSender sender, String permission) {
        if (permission != null) {
            message(sender, colorize(AtherialLib.COMMAND_MESSAGES.getNoPermissionMessage()).replaceAll("%permission%", permission));
        } else {
            message(sender, colorize(AtherialLib.COMMAND_MESSAGES.getNoPermissionMessage()));
        }
    }

    public static void sendNoPermissionMessage(CommandSender sender) {
        sendNoPermissionMessage(sender, null);
    }

    public static void sendCommandHelp(CommandSender sender, final String command, HelpSubCommand... helpSubCommands) {
        message(sender, colorize(AtherialLib.COMMAND_MESSAGES.getHelpHeader()).replaceAll("%command%", command.replaceAll("/", "")));
        for (HelpSubCommand helpSubCommand : helpSubCommands) {
            StringBuilder commandString = new StringBuilder(helpSubCommand.getCommand().replaceAll("/", ""));
            if (helpSubCommand.getArguments() != null && helpSubCommand.getArguments().length > 0) {
                commandString.append(" ").append(colorize(AtherialLib.COMMAND_MESSAGES.getHelpArgumentsColor()));
                for (String argument : helpSubCommand.getArguments()) {
                    commandString.append(argument).append(" ");
                }
                if (commandString.toString().endsWith(" ")) {
                    commandString = new StringBuilder(commandString.toString().trim());
                }
            }
            if ((helpSubCommand.getPermission() != null) && !sender.hasPermission(helpSubCommand.getPermission())) {
                continue;
            }
            message(sender, colorize(AtherialLib.COMMAND_MESSAGES.getHelpLine()).replaceAll("%description%", helpSubCommand.getDescription()).replaceAll("%command%", commandString.toString()));
        }
        message(sender, colorize(AtherialLib.COMMAND_MESSAGES.getHelpFooter()).replaceAll("%command%", command.replaceAll("/", "")));
    }

    public static void sendPlayerOnlyMessage(CommandSender sender) {
        message(sender, colorize(AtherialLib.COMMAND_MESSAGES.getPlayerOnlyCommandMessage()));
    }
}