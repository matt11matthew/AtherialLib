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
    public static String getMessageFromArgs(String[] args) {
        return getMessageFromArgs(0, args);

    }
    public static String getMessageFromArgs(int start, String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            stringBuilder.append(args[i]);
            if (i < args.length-1) {
                stringBuilder.append(' ');
            }
        }
        return stringBuilder.toString();
    }
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String translateHexColorCodes(String message) {
        if (message==null||message.isEmpty())return message;
        char colorChar = '§';
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 32);

        while(matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "§x§" + group.charAt(0) + '§' + group.charAt(1) + '§' + group.charAt(2) + '§' + group.charAt(3) + '§' + group.charAt(4) + '§' + group.charAt(5));
        }

        String finalString =  matcher.appendTail(buffer).toString();

       return ChatColor.translateAlternateColorCodes('&', finalString);
    }

//    public static String translateHexColorCodes(String message) {
//        final char COLOR_CHAR = '§';
//        // This pattern matches hex color codes in the format "#FFFFFF"
//        final Pattern hexPattern = Pattern.compile("#[a-fA-F0-9]{6}");
//        Matcher matcher = hexPattern.matcher(message);
//
//        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
//        while (matcher.find()) {
//            String group = matcher.group();
//            // Replace the hex color code with the Spigot format
//            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
//                    + COLOR_CHAR + group.charAt(1) + COLOR_CHAR + group.charAt(2)
//                    + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4)
//                    + COLOR_CHAR + group.charAt(5) + COLOR_CHAR + group.charAt(6));
//        }
//        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
//    }
    public static String splitSentence(String sentence, int maxLineLength) {
        if (maxLineLength < 2) {
            return sentence;
        }

        final Pattern colorPattern = Pattern.compile("§[a-fA-F0-9k-orx]|#(?:[a-fA-F0-9]{6})");
        Matcher matcher = colorPattern.matcher(sentence);

        StringBuilder result = new StringBuilder();
        int lastSplit = 0; // Last index where the sentence was split
        int visibleCount = 0; // Count of visible characters since last split

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            // Process substring between last split and start of the color code or hex code
            String substring = sentence.substring(lastSplit, start);
            for (int i = 0; i < substring.length(); i++) {
                char c = substring.charAt(i);

                // Increment count and append character
                visibleCount++;
                result.append(c);

                // Check for split condition
                if (visibleCount >= maxLineLength && c == ' ') {
                    result.append("\n");
                    visibleCount = 0;
                }
            }

            // Append the color or hex code
            result.append(sentence.substring(start, end));

            // Update the last split and reset visible character count if needed
            lastSplit = end;
            if (visibleCount >= maxLineLength) {
                result.append("\n");
                visibleCount = 0;
            }
        }

        // Process any remaining characters after the last color or hex code
        String remaining = sentence.substring(lastSplit);
        for (int i = 0; i < remaining.length(); i++) {
            char c = remaining.charAt(i);
            visibleCount++;
            result.append(c);

            if (visibleCount >= maxLineLength && c == ' ') {
                result.append("\n");
                visibleCount = 0;
            }
        }

        return result.toString();
    }
    public static String applyBold(String input) {
        // Regular expression to find the style sequence
        String stylePattern = "(&[0-9a-fk-or]*[l-o])";

        // Insert the underline code &n after the style sequence
        return input.replaceAll(stylePattern, "$1&l");
    }

    public static String applyStrikeThrough(String input) {
        // Regular expression to find the style sequence
        String stylePattern = "(&[0-9a-fk-or]*[l-o])";

        // Insert the underline code &n after the style sequence
        return input.replaceAll(stylePattern, "$1&m");
    }
    public static String applyUnderline(String input) {
        // Regular expression to find the style sequence
        String stylePattern = "(&[0-9a-fk-or]*[l-o])";

        // Insert the underline code &n after the style sequence
        return input.replaceAll(stylePattern, "$1&n");
    }
//    public static String splitSentence(String sentence, int maxLineLength) {
//        if (maxLineLength < 2) {
//            return sentence;
//        }
//
//        StringBuilder result = new StringBuilder();
//        int count = 0;
//
//        for (int i = 0; i < sentence.length(); i++) {
//            char c = sentence.charAt(i);
//
//            // Check if a color code is about to be split
//            if (count >= maxLineLength - 1 && c == '§' && i + 2 <= sentence.length()) {
//                result.append("\n");
//                count = 0;
//            }
//
//            // Split at space after maxLineLength characters
//            if (count >= maxLineLength && c == ' ') {
//                result.append("\n");
//                count = 0;
//            } else {
//                result.append(c);
//                count++;
//            }
//        }
//
//        return result.toString();
//    }
    public static String formatEnum(String input) {
        if (input.contains("_")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : input.split("_")) {

                stringBuilder.append(formatEnum(s));
                stringBuilder.append(" ");
            }
            return stringBuilder.toString().trim();
        } else {
            return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase().trim();
        }
    }
    public static String colorize(String message){
        if (message==null)return null;
        return translateHexColorCodes(message);
    }
}
