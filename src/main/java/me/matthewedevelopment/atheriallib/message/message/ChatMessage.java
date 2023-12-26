package me.matthewedevelopment.atheriallib.message.message;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import org.bukkit.command.CommandSender;

public class ChatMessage extends Message {


    public ChatMessage(String message) {
        super(message);
    }

    public ChatMessage(ChatMessage chatMessage) {
        super(chatMessage.message);
    }

    @Override
    public String send(CommandSender sender, StringReplacer stringReplacer) {
        if (message==null||message.equalsIgnoreCase("none"))return null;
        if (sender == null) {
            return "";
        }
        String replacePrefix = replacePrefix(stringReplacer.replace(message));

        ChatUtils.message(sender, replacePrefix);
        return replacePrefix;
    }

    @Override
    public void send(CommandSender sender) {
        if (message==null||message.equalsIgnoreCase("none"))return;
        if (sender == null) {
            return;
        }
        ChatUtils.message(sender, replacePrefix(message));
    }

    @Override
    public Message getMessageReplaced(StringReplacer stringReplacer) {
        return new ChatMessage(stringReplacer.replace(replacePrefix(new ChatMessage(this).message)));
    }
}