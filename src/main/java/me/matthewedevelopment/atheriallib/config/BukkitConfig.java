package me.matthewedevelopment.atheriallib.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

    public String getString(String key) {
        return this.configuration.getString(key);
    }

    public String getString(String key, String defaultValue) {
        return this.configuration.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return this.configuration.getInt(key, defaultValue);
    }

    public Location getBlockLocation(String key) {
        ConfigurationSection section = configuration.getConfigurationSection(key);
        if (section == null) return null;
        String worldName = section.getString("world");
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        int x = section.getInt("x", 0);
        int y = section.getInt("y", 0);
        int z = section.getInt("y", 0);

        return new Location(world, x, y, z);
    }

    public double getDouble(String key, double defaultValue) {
        return this.configuration.getDouble(key, defaultValue);
    }

    public Map<String, Object> getMap(String key) {
        MemorySection o = (MemorySection) this.configuration.get(key);
        return o.getValues(false);
    }

    public Map<String, Object> getMapWithinSection(String key, ConfigurationSection section) {
        MemorySection o = (MemorySection) section.get(key);
        return o.getValues(false);
    }

    public Map<String, ConfigurationSection> getSectionByKeys(String key) {
        Map<String, ConfigurationSection> map = new HashMap<String, ConfigurationSection>();
        for (String s : configuration.getConfigurationSection(key).getKeys(false)) {
            map.put(s, configuration.getConfigurationSection(key + "." + s));
        }
        return map;
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
