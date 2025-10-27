package me.matthewedevelopment.atheriallib.config.yaml.serializables;

import me.matthewedevelopment.atheriallib.config.yaml.AtherialLibItem;
import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.IntSimpleList;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer.IntSimpleListSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtherialLibItemSerializable implements ConfigSerializable<AtherialLibItem> {

    @Override
    public Map<String, Object> serializeComplex(AtherialLibItem item) {
        Map<String, Object> serializedData = new HashMap<>();
        if (item.getType() != null) {
            serializedData.put("type", item.getType().toString());  // Mandatory
        }


        if (item.getCommands() != null && item.getCommands().size() > 0) {
            serializedData.put("commands", item.getCommands());
        }
        // Optional fields
        if (item.getAmount() > 1) {
            serializedData.put("amount", item.getAmount());
        }
        if (item.isSnakeCase()) {

            if (item.getDisplayName() != null) {
                serializedData.put("display_name", item.getDisplayName());

            }
        } else {

            if (item.getDisplayName() != null) {
                serializedData.put("display_name", item.getDisplayName());

            }
        }
        if (item.getMultiSlots() != null) {
            if (item.isSnakeCase()) {

                serializedData.put("multi_slots", new IntSimpleListSerializer().serializeSimple(item.getMultiSlots()));
            } else {

                serializedData.put("multiSlots", new IntSimpleListSerializer().serializeSimple(item.getMultiSlots()));
            }
        }
        if (item.getData() == -1) {

        } else {
            serializedData.put("data", item.getData());
        }
        if (item.getLore() != null && !item.getLore().isEmpty()) {
            serializedData.put("lore", item.getLore());
        }
        if (item.getSkullOwner() != null) {
            if (item.isSnakeCase()) {

                serializedData.put("skull_owner", item.getSkullOwner());
            } else {
                serializedData.put("skullOwner", item.getSkullOwner());

            }

        }
        if (item.getModelId() != 0) {
            if (item.isSnakeCase()) {

                serializedData.put("model_id", item.getModelId());  // Mandatory
            } else {

                serializedData.put("modelId", item.getModelId());  // Mandatory
            }
        }



        if (item.getCustomModel() != null) {
            if (item.isSnakeCase()) {

                serializedData.put("item_model", item.getCustomModel());  // Mandatory
            } else {

                serializedData.put("itemModel", item.getCustomModel());  // Mandatory
            }
        }
        if (item.getHeadDatabaseHead() != null) {
            if (item.isSnakeCase()) {

                serializedData.put("head_database_head", item.getHeadDatabaseHead());
            } else {
                serializedData.put("headDatabaseHead", item.getHeadDatabaseHead());

            }

        }
        if (item.getSlot() != -1) {
            serializedData.put("slot", item.getSlot());
        }
        if (item.getEnchantments() != null && !item.getEnchantments().isEmpty()) {
            Map<String, Integer> enchants = new HashMap<>();
            for (String s : item.getEnchantments().keySet()) {
                int i = item.getEnchantments().get(s);
                enchants.put(s, i);
            }
            serializedData.put("enchantments", enchants);
        }

        return serializedData;
    }

    @Override
    public AtherialLibItem deserializeComplex(Map<String, Object> map) {
        Material type = map.containsKey("type") ? Material.valueOf((String) map.get("type")) : null;  // Mandatory


        boolean saveData = false;
        int dt = -1;

        if (map.containsKey("data")) {
            int data = (int) map.get("data");
            if (data != -1) {
                saveData = true;
                dt = data;

            }
        }

        List<String> lore = null;
        boolean snakeCase = false;

        if (map.containsKey("lore")) {
            lore = new ArrayList<>();
            Object o = map.get("lore");
            if (o instanceof String) {
                lore.add((String) o);
            } else {
                lore = (List<String>) map.get("lore");
            }
        }

        String itemModel = map.containsKey("itemModel") ? (String) map.get("itemModel") : null;
        if (map.containsKey("item_model")) {
            snakeCase = true;
            itemModel = (String) map.get("item_model");
        }




        String skullOwner = map.containsKey("skullOwner") ? (String) map.get("skullOwner") : null;
        if (map.containsKey("skull_owner")) {
            snakeCase = true;
            skullOwner = (String) map.get("skull_owner");
        }

        String displayName = map.containsKey("displayName") ? (String) map.get("displayName") : null;
        if (map.containsKey("display_name")) {
            snakeCase = true;
            displayName = (String) map.get("display_name");
        }
        List<String> commands = null;

        if (map.containsKey("commands")) {
            commands = new ArrayList<>();
            Object o = map.get("commands");
            if (o instanceof String) {
                commands.add((String) o);
            } else {
                commands = (List<String>) map.get("commands");
            }
        }


        IntSimpleList multiSlots = map.containsKey("multiSlots") ? new IntSimpleListSerializer().deserializeSimple((String) map.get("multiSlots")) : null;
        if (map.containsKey("multi_slots")) {
            snakeCase = true;
            multiSlots = map.containsKey("multi_slots") ? new IntSimpleListSerializer().deserializeSimple((String) map.get("multi_slots")) : null;
        }

        int modelId = map.containsKey("modelId") ? (int) map.get("modelId") : 0;
        if (map.containsKey("model_id")) {

            snakeCase = true;
            modelId = map.containsKey("model_id") ? (int) map.get("model_id") : 0;
        }

        String headDatabaseHead = map.containsKey("headDatabaseHead") ? (String) map.get("headDatabaseHead") : null;
        if (map.containsKey("head_database_head")) {
            headDatabaseHead = map.containsKey("head_database_head") ? (String) map.get("head_database_head") : null;
            snakeCase = true;
        }
        int slot = map.containsKey("slot") ? (int) map.get("slot") : -1;
        int amount = map.containsKey("amount") ? (int) map.get("amount") : 1;
        Map<String, Integer> enchantments = new HashMap<>();
        if (map.containsKey("enchantments")) {
            MemorySection memorySection = (MemorySection) map.get("enchantments");
            Map<String, Object> values = memorySection.getValues(false);

            for (String s : values.keySet()) {
                enchantments.put(s, (int) values.get(s));

            }
        }

        AtherialLibItem atherialLibItem = new AtherialLibItem(type, amount, displayName, lore, skullOwner, slot, enchantments);
        atherialLibItem.setSnakeCase(snakeCase);
        if (saveData) {
            atherialLibItem = atherialLibItem.setData(dt);
        }
        if (multiSlots != null) {
            atherialLibItem.setMultiSlots(multiSlots);
        }
        if (headDatabaseHead != null) {
            atherialLibItem = atherialLibItem.setHeadDatabaseHead(headDatabaseHead);
        }
        if (modelId != 0) {
            atherialLibItem = atherialLibItem.setModelId(modelId);
        }
        if ((commands != null) && !commands.isEmpty()) {
            atherialLibItem = atherialLibItem.setCommands(commands);
        }
        if (itemModel!=null) {
            atherialLibItem = atherialLibItem.setCustomModel(itemModel);
        }

        return atherialLibItem;
    }

    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }
}