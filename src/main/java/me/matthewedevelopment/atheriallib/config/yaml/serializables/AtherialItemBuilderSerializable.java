package me.matthewedevelopment.atheriallib.config.yaml.serializables;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.item.AtherialItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew E on 12/23/2023 at 8:42 PM for the project AtherialLib
 */
public class AtherialItemBuilderSerializable implements ConfigSerializable<AtherialItemBuilder> {

    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }



    @Override
    public Map<String, Object> serializeComplex(AtherialItemBuilder atherialItemBuilder) {
        Map<String, Object> serializedItem = new HashMap<>();

        ItemStack itemStack = atherialItemBuilder.getItemStack();
        serializedItem.put("type", itemStack.getType().toString());
        serializedItem.put("slot", atherialItemBuilder.getSlot());

        if (itemStack.getAmount() > 1) {
            serializedItem.put("amount", itemStack.getAmount());
        }
        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta.hasDisplayName()) {
                serializedItem.put("displayName", itemMeta.getDisplayName().replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "&"));
            }
            if (itemMeta.hasLore()) {
                List<String> newLore = new ArrayList<>();
                for (String s : itemMeta.getLore()) {
                    newLore.add(s.replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "&"));
                }
                serializedItem.put("lore", newLore);
            }
            // Handle additional properties here
        }

        return serializedItem;
    }

    @Override
    public AtherialItemBuilder deserializeComplex(Map<String, Object> map) {
        Material material = Material.valueOf((String) map.get("type"));
        int slot = (Integer) map.getOrDefault("slot", -1);
        int amount = (Integer) map.getOrDefault("amount", 1);

        AtherialItemBuilder.Builder builder = new AtherialItemBuilder.Builder(material, amount);

        if (map.containsKey("displayName")) {
            String displayName = ChatColor.translateAlternateColorCodes('&', (String) map.get("displayName"));
            builder = builder.displayName(displayName);
        }
        if (map.containsKey("lore")) {
            List<String> lore = new ArrayList<>();
            ((List<String>) map.get("lore")).forEach(line -> lore.add(ChatColor.translateAlternateColorCodes('&', line)));
            builder = builder.lore(lore);
        }

        // Handle additional properties here

        return builder.build().slot(slot);
    }
}