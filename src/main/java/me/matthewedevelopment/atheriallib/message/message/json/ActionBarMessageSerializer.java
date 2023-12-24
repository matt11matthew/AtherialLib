package me.matthewedevelopment.atheriallib.message.message.json;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.message.message.ActionBarMessage;
import org.bukkit.entity.ComplexLivingEntity;

import java.util.HashMap;
import java.util.Map;


public class ActionBarMessageSerializer implements ConfigSerializable<ActionBarMessage> {





    @Override
    public Map<String, Object> serializeComplex(ActionBarMessage object) {
        Map<String, Object> map = new HashMap<>();

        map.put("message", object.getMessage());
        if( object.getDuration()>0){
            map.put("duration", object.getDuration());
        }
        return map;
    }

    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }

    @Override
    public ActionBarMessage deserializeComplex(Map<String, Object> map) {
        return ActionBarMessage.builder().message((String) map.get("message")).duration(map.containsKey("duration") ? (int) map.get("duration") : 0).build();

    }
}
