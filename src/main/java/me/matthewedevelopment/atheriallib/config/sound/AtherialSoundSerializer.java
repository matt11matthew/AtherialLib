package me.matthewedevelopment.atheriallib.config.sound;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;

import java.util.HashMap;
import java.util.Map;

public class AtherialSoundSerializer implements ConfigSerializable<AtherialSound> {

    @Override
    public AtherialSound deserializeComplex(Map<String, Object> map) {

        String sound = (String) map.get("sound");
        float volume = (float) map.get("volume");
        float pitch = (float) map.get("pitch");
        return new AtherialSound(sound, volume,pitch);
    }

    @Override
    public Map<String, Object> serializeComplex(AtherialSound object) {
        Map<String, Object> map =new HashMap<>();
        map.put("sound", object.getSound());
        map.put("volume", object.getVolume());
        map.put("pitch", object.getPitch());

        return map;
    }

    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }
}
