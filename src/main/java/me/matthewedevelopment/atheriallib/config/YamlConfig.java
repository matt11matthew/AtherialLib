package me.matthewedevelopment.atheriallib.config;

import me.matthewedevelopment.atheriallib.item.AtherialItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class YamlConfig implements Config {
    private String path;
    private Plugin plugin;
    private Map<String, Object> defaultMap;
    private File file;

    public YamlConfig(String path, Plugin plugin) {
        this.path = path;
        this.plugin = plugin;
        this.defaultMap = new HashMap<>();
        this.loadConfig();
    }

    public void addDefault(String path, Object value) {
        if (defaultMap.containsKey(path)) {
            defaultMap.remove(path);
        }
        this.defaultMap.put(path, value);
    }

    @Override
    public void reload() {
        this.loadConfig();
    }

    @Override
    public void loadConfig() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdirs();
        }
        File file = this.getFile();
        if (!this.exists()) {
            File parentFile = file.getParentFile();
            while (!parentFile.exists()) {
                parentFile.mkdirs();
                parentFile = parentFile.getParentFile();
            }
            if (!this.exists()) {
                this.saveDefaultConfig();
                file = this.getFile();
            }
        }
        this.file = file;
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        File finalFile = file;

        for (Map.Entry<String, Object> stringObjectEntry : defaultMap.entrySet()) {
            if (!yamlConfiguration.isSet(stringObjectEntry.getKey())) {
                try {
                    yamlConfiguration.set(stringObjectEntry.getKey(), stringObjectEntry.getValue());
                    yamlConfiguration.save(finalFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Arrays.stream(getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(SerializedName.class))
                .filter(field -> !field.isAnnotationPresent(IgnoreValue.class))
                .forEach(field -> {
                    SerializedName annotation = field.getAnnotation(SerializedName.class);
                    if (!yamlConfiguration.isSet(annotation.value())) {
                        try {
                            yamlConfiguration.set(annotation.value(), field.get(field.getType()));
                            yamlConfiguration.save(finalFile);
                        } catch (IllegalAccessException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        field.set(field.getType(), field.getType().cast(yamlConfiguration.get(annotation.value())));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

    }

    public AtherialItemBuilder.Builder getAtherialItemBuilder(String path) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(getFile());
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(path);
        String type = configurationSection.getString("type");
        Material material;
        try {
            material = Material.getMaterial(type);
        } catch (Exception e) {
            return null;
        }
        int amount = configurationSection.getInt("amount", 1);
        int durability = configurationSection.getInt("durability", 0);
        String displayName = configurationSection.getString("displayName", "");
        List<String> loreStringList = new ArrayList<>();
        if (configurationSection.isSet("lore") && configurationSection.isList("lore")) {
            loreStringList = configurationSection.getStringList("lore");
        }

        return AtherialItemBuilder.builder()
                .type(material)
                .displayName(displayName)
                .lore(loreStringList)
                .durability((short) durability)
                .amount(amount);

    }

    @Override
    public void saveDefaultConfig() {
        if (!file.exists()) {
            this.plugin.saveResource(this.path, true);
        }
        this.file = getFile();
    }

    @Override
    public File getFile() {
        if (this.file == null) {
            this.file = new File(getPlugin().getDataFolder() + File.separator + getPath());
        }
        return file;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getPath() {
        return path;
    }
}