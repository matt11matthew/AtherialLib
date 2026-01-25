package me.matthewedevelopment.atheriallib.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.matthewedevelopment.atheriallib.io.StringReplacer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtherialTranslationMessage {
    @Getter
    private String content;

    public String toLegacyString(StringReplacer stringReplacer) {
        return toLegacyString(stringReplacer, null);
    }

    public String toLegacyString() {
        return toLegacyString(null);
    }

    public String toLegacyString(StringReplacer stringReplacer, Player p) {
        Component component = toComponent(stringReplacer, p);
        if (component == null) {
            return "";
        }
        return LegacyComponentSerializer.legacySection().serialize(component);
    }



    public Component toComponent(Player p) {
        return toComponent(null, p);
    }
    public Component toComponent(StringReplacer stringReplacer) {
        return toComponent(stringReplacer, null);
    }



    public Component toComponent() {
        return toComponent(null, null);
    }

    public Component toComponent(StringReplacer stringReplacer, Player p) {
        if (content == null || content.isEmpty() || content.equalsIgnoreCase("none")) {
            return null;
        }

        String replaced = content;
        if (stringReplacer != null) {
            replaced = stringReplacer.replace(content);
        }

        try {
            if (replaced.contains("§")) {
                // Don't parse § strings with MiniMessage — use legacy directly
                return LegacyComponentSerializer.legacySection().deserialize(replaced);
            }

            // Uses your CenterTagResolver logic to apply <center> if present
            return CenterTagResolver.applyCenterIfNeeded(replaced, p);
        } catch (Exception e) {
            e.printStackTrace();
            return LegacyComponentSerializer.legacyAmpersand().deserialize(replaced); // Fallback
        }

    }
    private static final MiniMessage MM = MiniMessage.miniMessage();


    public Component toComponentBasic(StringReplacer stringReplacer) {
        if (content == null || content.isEmpty() || content.equalsIgnoreCase("none")) {
            return null;
        }

        String replaced = content;
        if (stringReplacer != null) {
            replaced = stringReplacer.replace(content);
        }

        try {
            if (replaced.contains("§")) {
                // Don't parse § strings with MiniMessage — use legacy directly
                return LegacyComponentSerializer.legacySection().deserialize(replaced);
            }


            replaced = replaced.replace("<newline>", NEWLINE_PLACEHOLDER);
            replaced = replaced.replace("</center>", "");
            replaced = replaced.replace("<center>", CENTER_PLACEHOLDER);
            return MM.deserialize(replaced);
        } catch (Exception e) {
            e.printStackTrace();
            return LegacyComponentSerializer.legacyAmpersand().deserialize(replaced); // Fallback
        }

    }
    private static  String NEWLINE_PLACEHOLDER = "(New Line)";
    private static  String CENTER_PLACEHOLDER = "(Centered)";

    public static void setNewlinePlaceholder(String newlinePlaceholder) {
        NEWLINE_PLACEHOLDER = newlinePlaceholder;
    }

    public static void setCenterPlaceholder(String centerPlaceholder) {
        CENTER_PLACEHOLDER = centerPlaceholder;
    }
}
