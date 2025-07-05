package me.matthewedevelopment.atheriallib.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonConfigManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File configDir;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public JsonConfigManager( File configDir) {
        this.configDir = configDir;
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public <T> T loadOrCreate(String fileName, Class<T> clazz) {
        File file = new File(configDir, fileName + ".json");
        try {
            T result;

            if (!file.exists()) {
                // File does not exist — create a default instance
                result = clazz.getDeclaredConstructor().newInstance();
                save(fileName, result);
            } else {
                // Load and parse the file
                try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                    result = gson.fromJson(reader, clazz);
                }

                // Fallback: if file was corrupted or partially missing fields
                if (result == null) {
                    result = clazz.getDeclaredConstructor().newInstance();
                    save(fileName, result); // Overwrite bad file
                }
            }

            cache.put(fileName, result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + fileName, e);
        }
    }

    public void save(String fileName, Object obj) {
        File file = new File(configDir, fileName + ".json");

        try {
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                gson.toJson(obj, writer);
            }

            cache.put(fileName, obj);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config: " + fileName, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getCached(String fileName, Class<T> clazz) {
        return (T) cache.get(fileName);
    }
}
