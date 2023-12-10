package me.matthewedevelopment.atheriallib.utilities;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Matthew E on 11/16/2023 at 1:11 PM for the project AtherialLib
 */
public class ChatUtils {
    public static void message(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(message);
    }
    public static String formatEnum(String input) {
        if (input.contains("_")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : input.split("_")) {

                stringBuilder.append(formatEnum(s));
                stringBuilder.append(" ");
            }
            return stringBuilder.toString();
        } else {
            return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
        }
    }
    public static String colorize(String message){
        return new String(message).replaceAll("&", "\u00A7");
    }
}
