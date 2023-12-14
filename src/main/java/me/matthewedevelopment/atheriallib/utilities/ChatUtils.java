package me.matthewedevelopment.atheriallib.utilities;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Matthew E on 11/16/2023 at 1:11 PM for the project AtherialLib
 */
public class ChatUtils {
    public static void message(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(message);
    }

    public static String splitSentence(String sentence, int maxLineLength) {
        if (maxLineLength < 2) {
            return sentence;
        }

        StringBuilder result = new StringBuilder();
        int count = 0;

        for (int i = 0; i < sentence.length(); i++) {
            char c = sentence.charAt(i);

            // Check if a color code is about to be split
            if (count >= maxLineLength - 1 && c == '§' && i + 2 <= sentence.length()) {
                result.append("\n");
                count = 0;
            }

            // Split at space after maxLineLength characters
            if (count >= maxLineLength && c == ' ') {
                result.append("\n");
                count = 0;
            } else {
                result.append(c);
                count++;
            }
        }

        return result.toString();
    }

    public static String formatEnum(String input) {
        if (input.contains("_")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : input.split("_")) {

                stringBuilder.append(formatEnum(s));
                stringBuilder.append(" ");
            }
            return stringBuilder.toString();
        } else {
            return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
        }
    }
    public static String colorize(String message){
        if (message==null)return null;

//        return new String(message).replaceAll("&", "\u00A7");
        return translateHexColorCodes(message);
    }
    public static String translateHexColorCodes(String message) {
        final char COLOR_CHAR = '§';
        // This pattern matches hex color codes in the format "#FFFFFF"
        final Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = hexPattern.matcher(message);

        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group();
            // Replace the hex color code with the Spigot format
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2)
                    + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4)
                    + COLOR_CHAR + group.charAt(5) + COLOR_CHAR + group.charAt(6));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
}
