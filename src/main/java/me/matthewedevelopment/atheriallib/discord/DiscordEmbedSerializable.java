package me.matthewedevelopment.atheriallib.discord;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import org.bukkit.configuration.MemorySection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscordEmbedSerializable implements ConfigSerializable<DiscordEmbed> {
    @Override
    public Map<String, Object> serializeComplex(DiscordEmbed object) {
        Map<String, Object> map = new HashMap<>();

        // Serialize DeleteConfig
        Map<String, Object> deleteConfigMap = new HashMap<>();
        deleteConfigMap.put("enabled", object.getDeleteConfig().isEnabled());
        deleteConfigMap.put("seconds", object.getDeleteConfig().getSeconds());
        map.put("delete", deleteConfigMap);

        // Serialize other fields
        map.put("title", object.getTitle());
        map.put("embed-color", object.getEmbedColor());
        map.put("description", object.getDescription());
        map.put("footer", object.getFooter());

        return map;
    }

    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }

    @Override
    public DiscordEmbed deserializeComplex(Map<String, Object> map) {
        // Deserialize DeleteConfig
        Map<String, Object> deleteConfigMap = (Map<String, Object>) ((MemorySection)map.get("delete")).getValues(false);
        boolean deleteEnabled = (boolean) deleteConfigMap.get("enabled");
        int deleteSeconds = (int) deleteConfigMap.get("seconds");
        DiscordEmbed.DeleteConfig deleteConfig = new DiscordEmbed.DeleteConfig(deleteEnabled, deleteSeconds);

        // Deserialize other fields
        String title = (String) map.get("title");
        String embedColor = (String) map.get("embed-color");
        List<String> description = (List<String>) map.get("description");
        String footer = (String) map.get("footer");

        // Build and return the deserialized object
        return new DiscordEmbed.Builder()
                .setDeleteConfig(deleteEnabled, deleteSeconds)
                .setTitle(title)
                .setEmbedColor(embedColor)
                .setDescription(description)
                .setFooter(footer)
                .build();
    }


}
