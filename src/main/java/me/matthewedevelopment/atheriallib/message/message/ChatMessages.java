package me.matthewedevelopment.atheriallib.message.message;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import org.bukkit.command.CommandSender;

public class ChatMessages extends Message {


    private String[] lines;
    public ChatMessages(String... lines) {
        super("");
        this.lines = lines;

    }

    public String[] getLines() {
        return lines;
    }

    public ChatMessages(ChatMessages chatMessage) {
        super(chatMessage.message);
    }

    @Override
    public String send(CommandSender sender, StringReplacer stringReplacer) {
        for (String line : lines) {

            if (line==null||line.equalsIgnoreCase("none"))continue;
            if (sender == null) {
                continue;
            }
            String replacePrefix = replacePrefix(stringReplacer.replace(line));

            ChatUtils.message(sender, replacePrefix);

        }
        return "";
    }

    @Override
    public void send(CommandSender sender) {
        for (String line : lines) {

            if (line==null||line.equalsIgnoreCase("none"))continue;
            if (sender == null) {
                continue;
            }
            String replacePrefix = replacePrefix(line);

            ChatUtils.message(sender, replacePrefix);

        }

    }

    @Override
    public Message getMessageReplaced(StringReplacer stringReplacer) {
        return null;
    }
}