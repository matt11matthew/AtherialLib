package me.matthewedevelopment.atheriallib.message.message;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import org.bukkit.command.CommandSender;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

public abstract class Message {
    public String message;

    public static String PREFIX = "";

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String replacePrefix(String message) {
        return colorize(message).replaceAll("%prefix%", colorize(PREFIX));
    }


    public void send(CommandSender sender) {
    }


    public abstract Message getMessageReplaced(StringReplacer stringReplacer);

    public String send(CommandSender sender, StringReplacer stringReplacer) {
        return "";
    }
}