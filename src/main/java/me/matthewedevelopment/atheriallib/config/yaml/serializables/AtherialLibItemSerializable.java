package me.matthewedevelopment.atheriallib.config.yaml.serializables;

import me.matthewedevelopment.atheriallib.config.yaml.AtherialLibItem;
import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import org.bukkit.Material;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AtherialLibItemSerializable implements ConfigSerializable<AtherialLibItem> {

    @Override
    public Map<String, Object> serializeComplex(AtherialLibItem item) {
        Map<String, Object> serializedData = new HashMap<>();
        serializedData.put("type", item.getType().toString());  // Mandatory

        // Optional fields
        if (item.getAmount()>1){
            serializedData.put("amount", item.getAmount());
        }
        if (item.getDisplayName()!=null){
            serializedData.put("displayName", item.getDisplayName());

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
        if (item.getSlot()!=-1){
            serializedData.put("slot", item.getSlot());
        }

        return serializedData;
    }

    @Override
    public AtherialLibItem deserializeComplex(Map<String, Object> map) {
        Material type = Material.valueOf((String) map.get("type"));  // Mandatory


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

        int slot = map.containsKey("slot") ? (int) map.get("slot") : -1;
        int amount = map.containsKey("amount") ? (int) map.get("amount") : 1;
        AtherialLibItem atherialLibItem = new AtherialLibItem(type, amount, displayName, lore, skullOwner, slot);
        if (saveData){
            atherialLibItem=atherialLibItem.setData(dt);
        }

        return atherialLibItem;
    }

    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }
}