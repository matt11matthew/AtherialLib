package me.matthewedevelopment.atheriallib.config.yaml.serializables;

import me.matthewedevelopment.atheriallib.config.yaml.AtherialLibItem;
import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.IntSimpleList;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer.IntSimpleListSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtherialLibItemSerializable implements ConfigSerializable<AtherialLibItem> {

    @Override
    public Map<String, Object> serializeComplex(AtherialLibItem item) {
        Map<String, Object> serializedData = new HashMap<>();
        if (item.getType()!=null) {
            serializedData.put("type", item.getType().toString());  // Mandatory
        }
        if (item.getModelId()!=0) {
            serializedData.put("modelId", item.getModelId());  // Mandatory
        }
        // Optional fields
        if (item.getAmount()>1){
            serializedData.put("amount", item.getAmount());
        }
        if (item.getDisplayName()!=null){
            serializedData.put("displayName", item.getDisplayName());

        }
        if (item.getMultiSlots()!=null){
            serializedData.put("multiSlots", new IntSimpleListSerializer().serializeSimple(item.getMultiSlots()));
        }
        if (item.getData()==-1){

        } else {
            serializedData.put("data", item.getData());
        }
        if (item.getLore()!=null&&!item.getLore().isEmpty()){
            serializedData.put("lore", item.getLore());
        }
        if (item.getSkullOwner()!=null){
            serializedData.put("skullOwner", item.getSkullOwner());

        }
        if (item.getHeadDatabaseHead()!=null){
            serializedData.put("headDatabaseHead", item.getHeadDatabaseHead());

        }
        if (item.getSlot()!=-1){
            serializedData.put("slot", item.getSlot());
        }
        if (item.getEnchantments()!=null&&!item.getEnchantments().isEmpty()) {
            Map<String, Integer> enchants = new HashMap<>();
            for (String s : item.getEnchantments().keySet()) {
                int i = item.getEnchantments().get(s);
                enchants.put(s,i);
            }
            serializedData.put("enchantments",enchants);
        }

        return serializedData;
    }

    @Override
    public AtherialLibItem deserializeComplex(Map<String, Object> map) {
        Material type =map.containsKey("type")?Material.valueOf((String) map.get("type")):null;  // Mandatory


        boolean saveData = false;
        int dt = -1;

        if (map.containsKey("data")){
            int data = (int) map.get("data");
            if (data!=-1) {
                saveData=true;
                dt=data;

            }
        }

        List<String> lore = map.containsKey("lore") ? (List<String>) map.get("lore") : null;
        String skullOwner = map.containsKey("skullOwner") ? (String) map.get("skullOwner") : null;
        String displayName = map.containsKey("displayName") ? (String) map.get("displayName") : null;


        IntSimpleList multiSlots = map.containsKey("multiSlots") ? new IntSimpleListSerializer().deserializeSimple( (String)map.get("multiSlots")) : null;


        int modelId = map.containsKey("modelId") ? (int) map.get("modelId") : 0;

        String headDatabaseHead = map.containsKey("headDatabaseHead") ? (String) map.get("headDatabaseHead") : null;
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
        if (saveData){
            atherialLibItem=atherialLibItem.setData(dt);
        }
        if (multiSlots!=null) {
            atherialLibItem.setMultiSlots(multiSlots);
        }
        if (headDatabaseHead!=null){
            atherialLibItem= atherialLibItem.setHeadDatabaseHead(headDatabaseHead);
        }
        if (modelId!=0){
            atherialLibItem = atherialLibItem.setModelId(modelId);
        }

        return atherialLibItem;
    }

    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }
}