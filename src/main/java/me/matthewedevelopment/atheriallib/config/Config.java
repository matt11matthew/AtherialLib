package me.matthewedevelopment.atheriallib.config;

import org.bukkit.plugin.Plugin;

import java.io.File;

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

    void saveDefaultConfig();
}
