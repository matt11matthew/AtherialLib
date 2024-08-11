package me.matthewedevelopment.atheriallib.message.message.json;

import me.matthewedevelopment.atheriallib.config.yaml.ConfigSerializable;
import me.matthewedevelopment.atheriallib.config.yaml.SerializeType;
import me.matthewedevelopment.atheriallib.message.message.ChatMessage;
import me.matthewedevelopment.atheriallib.message.message.ChatMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew E on 12/23/2023 at 9:59 PM for the project AtherialLib
 */
public class ChatMessagesSerializer implements ConfigSerializable<ChatMessages > {
    @Override
    public SerializeType getComplexity() {
        return SerializeType.SIMPLE;
    }

    @Override
    public ChatMessages deserializeSimple(Object value) {
        List<String> messages = (List<String>) value;
        return new ChatMessages(messages.toArray(new String[]{}));
    }

    @Override
    public Object serializeSimple(ChatMessages object) {
        List<String> messages = new ArrayList<>();
        for (String line : object.getLines()) {
            messages.add(line);
        }
        return messages;
    }
}
