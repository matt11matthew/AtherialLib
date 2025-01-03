package me.matthewedevelopment.atheriallib.message.message;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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

        ChatUtils.send(sender,replacePrefix, null);
//        ChatUtils.message(sender, replacePrefix);
        return replacePrefix;
    }

    public String send(CommandSender sender, TagResolver resolver) {
        if (message==null||message.equalsIgnoreCase("none"))return null;
        if (sender == null) {
            return "";
        }

        ChatUtils.send(sender,message, resolver);
        return  message;
    }

    @Override
    public void send(CommandSender sender) {
        if (message==null||message.equalsIgnoreCase("none"))return;
        if (sender == null) {
            return;
        }
        ChatUtils.send(sender, replacePrefix(message), null);
    }

    @Override
    public Message getMessageReplaced(StringReplacer stringReplacer) {
        return new ChatMessage(stringReplacer.replace(replacePrefix(new ChatMessage(this).message)));
    }
}