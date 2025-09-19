package me.matthewedevelopment.atheriallib.chat;

import net.kyori.adventure.text.Component;

public class NewChatPrompt {
    private Component message;
    private Chat chat;

    private long timeout;

    public NewChatPrompt(Component message, Chat chat, long timeout) {
        this.message = message;
        this.chat = chat;
        this.timeout = timeout;
    }
    public long getTimeout() {
        return timeout;
    }

    public Component getMessage() {
        return message;
    }

    public Chat getChat() {
        return chat;
    }
}
