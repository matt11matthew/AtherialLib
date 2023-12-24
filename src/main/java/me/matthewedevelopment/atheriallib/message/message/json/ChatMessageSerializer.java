package me.matthewedevelopment.atheriallib.message.message.json;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.message.message.ChatMessage;

/**
 * Created by Matthew E on 12/23/2023 at 9:59 PM for the project AtherialLib
 */
public class ChatMessageSerializer implements ConfigSerializable<ChatMessage > {
    @Override
    public SerializeType getComplexity() {
        return SerializeType.SIMPLE;
    }

    @Override
    public ChatMessage deserializeSimple(Object value) {
        return new ChatMessage(String.valueOf(value));
    }

    @Override
    public Object serializeSimple(ChatMessage object) {
        return object.getMessage();
    }
}
