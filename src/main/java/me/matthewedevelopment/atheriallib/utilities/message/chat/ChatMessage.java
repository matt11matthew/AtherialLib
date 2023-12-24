package me.matthewedevelopment.atheriallib.utilities.message.chat;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.utilities.message.Message;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;


/**
 * Created by Matthew E on 5/25/2019 at 11:54 AM for the project atherialapi
 */
public class ChatMessage extends Message<ChatMessage, ChatMessage.Builder> {
    private TextComponent textComponent = new TextComponent("");

    public StringReplacer stringReplacer;

    public ChatMessage(Builder builder) {
        this.textComponent = builder.textComponent;
    }

    public void setStringReplacer(StringReplacer stringReplacer) {
        this.stringReplacer = stringReplacer;
    }

    public ChatMessage() {
        this.textComponent = new TextComponent("");
    }

    public ChatMessage(TextComponent textComponent) {
        this.textComponent = textComponent;
    }

    @Override
    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.stringReplacer = this.stringReplacer;
        return builder;
    }

    public static Builder builder() {
        return new ChatMessage().newBuilder();
    }

    public TextComponent getTextComponent() {
        return textComponent;
    }

    @Override
    public void send(CommandSender sender) {
//        if (((textComponent.getClickEvent() == null && textComponent.getHoverEvent() == null)) || (!(sender instanceof Player))) {
//            sender.sendMessage(textComponent.getText());
//            return;
//        }
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(textComponent);

        }
    }

    public static class Builder extends Message.Builder<ChatMessage> {
        private TextComponent textComponent = new TextComponent("");

        public StringReplacer stringReplacer;
        private ChatMessage header;
        private ChatMessage footer;


        public Builder replacer(StringReplacer stringReplacer) {
            this.stringReplacer = stringReplacer;
            return this;
        }

        public Builder() {
            this.textComponent = new TextComponent("");
        }

        public Builder header(ChatMessage chatMessage) {
            this.header = chatMessage;
            return this;
        }

        public Builder footer(ChatMessage chatMessage) {
            this.footer = chatMessage;
            return this;
        }

        public Builder header(String text) {
            if (stringReplacer != null) {
                this.header = ChatMessage.builder().text(stringReplacer.replace(text)).build();
            } else {

                this.header = ChatMessage.builder().text(text).build();
            }
            return this;
        }

        public Builder footer(String text) {
            if (stringReplacer != null) {
                this.footer = ChatMessage.builder().text(stringReplacer.replace(text)).build();
            } else {
                this.footer = ChatMessage.builder().text(text).build();

            }
            return this;
        }

        public Builder append(ChatMessage component) {
            this.textComponent.addExtra(component.getTextComponent());
            return this;
        }

        public Builder newLine() {
            this.textComponent.addExtra("\n");
            return this;
        }

        public Builder clickEvent(me.matthewedevelopment.atheriallib.utilities.message.chat.ClickEvent clickEvent) {
            ClickEvent.Action action = clickEvent.getAction().toBungee();
            if (action != null) {
                String value = clickEvent.getValue();
                if (stringReplacer != null) {
                    value = stringReplacer.replace(value);
                }
                this.textComponent.setClickEvent(new ClickEvent(action, value));
            }
            return this;
        }

        public Builder hoverEvent(me.matthewedevelopment.atheriallib.utilities.message.chat.HoverEvent hoverEvent) {
            List<BaseComponent> baseComponents = new ArrayList<>();
            for (int i = 0; i < hoverEvent.getText().size(); i++) {
                String text = hoverEvent.getText().get(i);
                if (stringReplacer != null) {
                    text = stringReplacer.replace(text);
                }
                if (i == hoverEvent.getText().size() - 1) {
                    baseComponents.add(new TextComponent(text));
                } else {
                    baseComponents.add(new TextComponent(text + "\n"));
                }
            }
            this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, baseComponents.toArray(new BaseComponent[0])));
            return this;
        }

        public Builder whiteSpace() {
            textComponent.addExtra(" ");
            return this;
        }

        public Builder append(String message) {
            if (stringReplacer != null) {
                message = stringReplacer.replace(message);
            }
            this.textComponent.addExtra(message);
            return this;
        }

        public Builder text(String message) {
            if (stringReplacer != null) {
                message = stringReplacer.replace(message);
            }
            this.textComponent.setText(colorize(message));
            return this;
        }

        public Builder message(String message) {
            if (stringReplacer != null) {
                message = stringReplacer.replace(message);
            }
            this.textComponent.setText(colorize(message));
            return this;
        }


        @Override
        public ChatMessage build() {
            if (this.header != null) {
                TextComponent newTextComponent = header.textComponent;
                newTextComponent.addExtra("\n");

                newTextComponent.addExtra(this.textComponent);
                this.textComponent = newTextComponent;
            }
            if (footer != null) {
                this.textComponent.addExtra("\n");
                this.textComponent.addExtra(footer.getTextComponent());
            }
            return new ChatMessage(this);
        }
    }
}
