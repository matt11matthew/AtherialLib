package me.matthewedevelopment.atheriallib.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
public final class CenterTagResolver {
    private static final net.kyori.adventure.text.minimessage.MiniMessage MM =
            net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();
    private static final net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer LEGACY =
            net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection();

    private static final int CENTER_PX = 154; // 320px chat, 100% size
    private static final int SPACE_PX  = 4;

    private CenterTagResolver() {}

    public static net.kyori.adventure.text.Component applyCenterIfNeeded(String input, org.bukkit.entity.Player player) {
        if (player != null) input = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, input);

        String lower = input.toLowerCase();
        int open = lower.indexOf("<center>");
        if (open == -1) {
            // no tag at all
            return MM.deserialize(input);
        }

        int close = lower.indexOf("</center>", open + 8);

        // parts that will be MiniMessage-parsed (without the tags themselves)
        String before = input.substring(0, open);
        String block;     // the block we will center
        String after;

        if (close != -1) {
            // paired <center>...</center> → center only the inner block
            block = input.substring(open + 8, close);
            after = input.substring(close + 9); // skip </center>
        } else {
            // single <center> → center everything after the tag
            block = input.substring(open + 8);
            after = "";
        }

        net.kyori.adventure.text.Component beforeC = before.isEmpty() ? net.kyori.adventure.text.Component.empty() : MM.deserialize(before);
        net.kyori.adventure.text.Component blockC  = MM.deserialize(block);
        net.kyori.adventure.text.Component afterC  = after.isEmpty() ? net.kyori.adventure.text.Component.empty() : MM.deserialize(after);

        int px = measurePx(blockC);
//        int padSpaces = Math.max(0, Math.round((CENTER_PX - (px / 2f)) / SPACE_PX));
//        net.kyori.adventure.text.Component padding =
//                padSpaces == 0 ? net.kyori.adventure.text.Component.empty()
//                        : net.kyori.adventure.text.Component.text(repeatSpaces(padSpaces));

        int padPx = CENTER_PX - (px / 2);
        Component padding = padPx <= 0 ? Component.empty() : buildPadding(padPx);

        

        return beforeC.append(padding).append(blockC).append(afterC);
    }
    private static Component buildPadding(int px) {
        StringBuilder sb = new StringBuilder();
        while (px >= 4) {
            sb.append(' ');
            px -= 4;
        }
        if (px == 3) sb.append('\u2006'); // 3px space
        else if (px == 2) sb.append('\u2009'); // 2px thin space
        else if (px == 1) sb.append('\u200A'); // 1px hair space

        return Component.text(sb.toString());
    }

    // ------- helpers -------

    private static String repeatSpaces(int n) {
        char[] c = new char[n];
        java.util.Arrays.fill(c, ' ');
        return new String(c);
    }

    private static int measurePx(net.kyori.adventure.text.Component c) {
        String s = LEGACY.serialize(c);
        int px = 0;
        boolean bold = false;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '§' && i + 1 < s.length()) {
                char code = Character.toLowerCase(s.charAt(++i));
                if (code == 'l') bold = true;
                else if (code == 'r' || isColor(code) || code == 'k' || code == 'm' || code == 'n' || code == 'o') bold = false;
                continue;
            }
            px += getCharWidth(ch, bold);
        }
        return px;
    }

    private static boolean isColor(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == 'x';
    }

    private static int getCharWidth(char c, boolean bold) {
        int w;
        switch (c) {
            case ' ': w = SPACE_PX; break;

            case 'i': case '!': case '.': case ',': case ':': case ';': case '|':
            case '¡': case '¦': case '·':
                w = 2; break;

            case '\'': case '`': case 'l': case 'I': case '(': case ')':
            case '[': case ']': case '{': case '}': case '¨':
                w = 3; break;

            case 't': case 'f': case 'k': case 'J': case '*': case '<': case '>': case '^':
                w = 4; break;

            case 'r': case 's': case 'x': case 'z': case 'c': case 'v':
            case 'y': case 'j': case '"': case '~': case '/': case '\\':
                w = 5; break;

            default:
                w = 6; break;
        }
        if (w == SPACE_PX) return SPACE_PX; // space doesn't get bold padding
        return bold ? w + 1 : w;
    }
}
