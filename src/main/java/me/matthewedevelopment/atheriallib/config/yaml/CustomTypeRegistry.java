package me.matthewedevelopment.atheriallib.config.yaml;

import java.util.HashMap;
import java.util.Map;

public class CustomTypeRegistry {

    private static final Map<String, ConfigSerializable<?>> registry = new HashMap<>();

    public static <T> void registerType(Class<T> typeClass, ConfigSerializable<T> serializer) {
        registry.put(typeClass.getSimpleName(), serializer);
        System.out.println("Registered config serializer " + typeClass.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public static <T> ConfigSerializable<T> getSerializer(Class<T> typeClass) {
        return (ConfigSerializable<T>) registry.get(typeClass.getSimpleName());
    }
}