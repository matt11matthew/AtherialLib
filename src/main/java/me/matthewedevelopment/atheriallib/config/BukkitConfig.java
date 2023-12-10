package me.matthewedevelopment.atheriallib.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Matthew Eisenberg on 5/22/2018 at 9:58 AM for the project atherialapi
 */
public class BukkitConfig {
    private File file;
    private FileConfiguration configuration;
    private String path;
    private Plugin plugin;

    public BukkitConfig(String path, Plugin plugin) {
        this.path = path;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder() + File.separator + path);

        if (!this.file.exists()) {
            File parentFile = file.getParentFile();
            while (!parentFile.exists()) {
                parentFile.mkdirs();
                parentFile = parentFile.getParentFile();
            }
            if (!this.file.exists()) {
                this.saveDefaultConfig();
            }
        }

        this.loadConfiguration();
    }


    public void loadConfiguration() {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public void set(String key, Object value) {
        this.configuration.set(key, value);
    }

    public void set(Map<String, Object> map) {
        map.forEach(this::set);
    }

    public void setConfiguration(FileConfiguration configuration) {
        this.configuration = configuration;

    }

    public boolean saveConfiguration() {
        try {
            this.configuration.save(this.file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveDefaultConfig() {
        if (!file.exists()) {
            this.plugin.saveResource(this.path, true);
        }
        this.file = new File(plugin.getDataFolder() + File.separator + path);
    }


}
