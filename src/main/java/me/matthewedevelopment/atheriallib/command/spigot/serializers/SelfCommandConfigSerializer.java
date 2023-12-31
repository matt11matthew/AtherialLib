package me.matthewedevelopment.atheriallib.command.spigot.serializers;

import me.matthewedevelopment.atheriallib.command.spigot.config.SelfCommandConfig;
import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import org.bukkit.configuration.MemorySection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew E on 12/30/2023 at 10:04 PM for the project AtherialLib
 */
public class SelfCommandConfigSerializer implements ConfigSerializable<SelfCommandConfig> {
    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }

    @Override
    public Map<String, Object> serializeComplex(SelfCommandConfig object) {
        Map<String, Object> map = new HashMap<>();
        map.put("permission", object.PERMISSION);
        map.put("help", object.HELP);
        map.put("usage", new UsageSerializer().serializeComplex(object.USAGE));
        return map;
    }

    @Override
    public SelfCommandConfig deserializeComplex(Map<String, Object> map) {
        String permission = (String) map.get("permission");
        String help = (String) map.get("help");

        MemorySection memorySection = (MemorySection) map.get("usage");
        SelfCommandConfig.Usage  usage = new UsageSerializer().deserializeComplex(memorySection.getValues(false));
        return new SelfCommandConfig(permission, help, usage);

    }
}
