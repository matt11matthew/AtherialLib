package me.matthewedevelopment.atheriallib.config.yaml;

import me.matthewedevelopment.atheriallib.command.spigot.config.SelfCommandConfig;
import me.matthewedevelopment.atheriallib.command.spigot.serializers.SelfCommandConfigSerializer;
import me.matthewedevelopment.atheriallib.command.spigot.serializers.UsageSerializer;
import me.matthewedevelopment.atheriallib.config.sound.AtherialSound;
import me.matthewedevelopment.atheriallib.config.sound.AtherialSoundSerializer;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.AtherialItemBuilderSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.AtherialLibItemSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.DoubleSimpleList;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.IntSimpleList;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.StringSimpleList;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer.DoubleSimpleListSerializer;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer.IntSimpleListSerializer;
import me.matthewedevelopment.atheriallib.config.yaml.serializables.list.serializer.StringSimpleListSerializer;
import me.matthewedevelopment.atheriallib.discord.DiscordEmbed;
import me.matthewedevelopment.atheriallib.discord.DiscordEmbedSerializable;
import me.matthewedevelopment.atheriallib.item.AtherialItemBuilder;
import me.matthewedevelopment.atheriallib.message.message.ActionBarMessage;
import me.matthewedevelopment.atheriallib.message.message.ChatMessage;
import me.matthewedevelopment.atheriallib.message.message.ChatMessages;
import me.matthewedevelopment.atheriallib.message.message.MessageTitle;
import me.matthewedevelopment.atheriallib.message.message.json.ActionBarMessageSerializer;
import me.matthewedevelopment.atheriallib.message.message.json.ChatMessageSerializer;
import me.matthewedevelopment.atheriallib.message.message.json.ChatMessagesSerializer;
import me.matthewedevelopment.atheriallib.message.message.json.TitleJsonSerializer;
import me.matthewedevelopment.atheriallib.utilities.location.AtherialLocation;
import me.matthewedevelopment.atheriallib.utilities.location.AtherialLocationSerializer;
import me.matthewedevelopment.atheriallib.utilities.location.AtherialXYZLocation;
import me.matthewedevelopment.atheriallib.utilities.location.AtherialXYZLocationSerializer;

import java.util.HashMap;
import java.util.Map;

public class CustomTypeRegistry {

    private static final Map<String, ConfigSerializable<?>> registry = new HashMap<>();

    public static <T> void registerType(Class<T> typeClass, ConfigSerializable<T> serializer) {
        registry.put(typeClass.getSimpleName(), serializer);
        System.out.println("Registered config serializer " + typeClass.getSimpleName());
    }

    public static void init() {
        CustomTypeRegistry.registerType(AtherialItemBuilder.class, new AtherialItemBuilderSerializable());
        CustomTypeRegistry.registerType(AtherialLibItem.class, new AtherialLibItemSerializable());


        CustomTypeRegistry.registerType(ActionBarMessage.class, new ActionBarMessageSerializer());
        CustomTypeRegistry.registerType(MessageTitle.class, new TitleJsonSerializer());
        CustomTypeRegistry.registerType(ChatMessage.class, new ChatMessageSerializer());
        CustomTypeRegistry.registerType(ChatMessages.class, new ChatMessagesSerializer());


        CustomTypeRegistry.registerType(IntSimpleList.class, new IntSimpleListSerializer());
        CustomTypeRegistry.registerType(DoubleSimpleList.class, new DoubleSimpleListSerializer());
        CustomTypeRegistry.registerType(StringSimpleList.class, new StringSimpleListSerializer());


        CustomTypeRegistry.registerType(SelfCommandConfig.Usage.class, new UsageSerializer());
        CustomTypeRegistry.registerType(SelfCommandConfig.class, new SelfCommandConfigSerializer());

        CustomTypeRegistry.registerType(AtherialLocation.class, new AtherialLocationSerializer());
        CustomTypeRegistry.registerType(AtherialXYZLocation.class, new AtherialXYZLocationSerializer());

        CustomTypeRegistry.registerType(AtherialSound.class, new AtherialSoundSerializer());

        CustomTypeRegistry.registerType(DiscordEmbed.class, new DiscordEmbedSerializable());
    }

    @SuppressWarnings("unchecked")
    public static <T> ConfigSerializable<T> getSerializer(Class<T> typeClass) {
        return (ConfigSerializable<T>) registry.get(typeClass.getSimpleName());
    }
}