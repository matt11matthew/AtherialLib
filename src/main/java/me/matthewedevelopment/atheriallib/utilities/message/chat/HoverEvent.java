package me.matthewedevelopment.atheriallib.utilities.message.chat;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Matthew E on 5/25/2019 at 12:01 PM for the project atherialapi
 */
public class HoverEvent {
    private final Action action;
    private List<String> text;

    public HoverEvent(Action action, List<String> text) {
        this.action = action;
        this.text = text;
    }

    public static HoverEvent showText(String[] text, Object... objects) {
        List<String> textList = new ArrayList<>();
        for (String s : text) {
            String format = String.format(s, objects);
            textList.add(format);
        }
        return new HoverEvent(Action.SHOW_TEXT, textList);
    }

    public static HoverEvent showText(String[] text, StringReplacer stringReplacer) {
        List<String> textList = new ArrayList<>();
        for (String s : text) {
            textList.add(stringReplacer.replace(s));
        }
        return new HoverEvent(Action.SHOW_TEXT, textList);
    }

    public static HoverEvent showText(String... text) {
        return new HoverEvent(Action.SHOW_TEXT, Arrays.asList(text));
    }

    public static HoverEvent showItem(ItemStack itemStack) {
        List<String> lines = new ArrayList<>();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            lines.add("Air");
        } else {
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                lines.add(itemStack.getItemMeta().getDisplayName());
            } else {
                lines.add(ChatUtils.formatEnum(itemStack.getType().toString()));
            }
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                lines.addAll(itemStack.getItemMeta().getLore());
            }
        }

        return new HoverEvent(Action.SHOW_ITEM, lines);
    }

    public Action getAction() {
        return action;
    }

    public List<String> getText() {
        return text;
    }


    public static enum Action {
        SHOW_TEXT,
        SHOW_ITEM;

        private Action() {
        }
    }
}
