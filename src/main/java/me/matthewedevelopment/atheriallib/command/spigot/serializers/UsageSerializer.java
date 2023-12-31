package me.matthewedevelopment.atheriallib.command.spigot.serializers;

import me.matthewedevelopment.atheriallib.command.spigot.config.SelfCommandConfig;
import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.StringSimpleList;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer.StringSimpleListSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew E on 12/30/2023 at 9:58 PM for the project AtherialLib
 */
public class UsageSerializer implements ConfigSerializable<SelfCommandConfig.Usage> {
    @Override
    public SerializeType getComplexity() {
        return SerializeType.COMPLEX;
    }

    @Override
    public SelfCommandConfig.Usage deserializeComplex(Map<String, Object> map) {
        String command = (String) map.get("command");

        StringSimpleList arguments = new StringSimpleListSerializer().deserializeSimple(map.get("arguments"));
        arguments.getList().toArray();
        for (String argument : arguments) {

        }
        return new SelfCommandConfig.Usage(command,  arguments.toArray());
    }

    @Override
    public Map<String, Object> serializeComplex(SelfCommandConfig.Usage object) {

        Map<String, Object> map = new HashMap<>();

        map.put("command", object.command);

        map.put("arguments",new StringSimpleListSerializer().serializeSimple(new StringSimpleList(object.arguments)));
        return map;

    }
}
