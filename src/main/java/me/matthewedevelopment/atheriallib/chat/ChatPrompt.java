package me.matthewedevelopment.atheriallib.chat;

public class ChatPrompt {
    private String message;
    private Chat chat;

    private long timeout;

    public ChatPrompt(String message, Chat chat, long timeout) {
        this.message = message;
        this.chat = chat;
        this.timeout = timeout;
    }
    public long getTimeout() {
        return timeout;
    }

    public String getMessage() {
        return message;
    }

    public Chat getChat() {
        return chat;
    }
}
