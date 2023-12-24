package me.matthewedevelopment.atheriallib.config;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;

/**
 * Created by Matthew Eisenberg on 5/20/2018 at 5:23 PM for the project atherialapi
 */
public interface Config {
    void loadConfig();

    String getPath();

    Plugin getPlugin();

    File getFile();

    void reload();

    default boolean exists() {
        File file = this.getFile();
        return (file != null) && (file.exists());
    }

    public default void message(CommandSender sender, String text,  StringReplacer stringReplacer) {
        sender.sendMessage(stringReplacer.replace(colorize(text)));
    }

    void saveDefaultConfig();
}
