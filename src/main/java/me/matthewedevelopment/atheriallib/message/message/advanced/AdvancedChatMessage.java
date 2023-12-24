package me.matthewedevelopment.atheriallib.message.message.advanced;


import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.message.message.Message;
import me.matthewedevelopment.atheriallib.utilities.message.chat.ChatMessage;
import me.matthewedevelopment.atheriallib.utilities.message.chat.ClickEvent;
import me.matthewedevelopment.atheriallib.utilities.message.chat.HoverEvent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;


/**
 * Created by Matthew E on 5/25/2019 at 11:54 AM for the project atherialapi
 */
public class AdvancedChatMessage extends Message {
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private AdvancedChatMessage parent;
    private List<AdvancedChatMessage> extra;

    public ClickEvent getClickEvent() {
        return clickEvent;
    }

    public HoverEvent getHoverEvent() {
        return hoverEvent;
    }

    public AdvancedChatMessage getParent() {
        return parent;
    }

    public List<AdvancedChatMessage> getExtra() {
        return extra;
    }

    public AdvancedChatMessage(Builder builder) {
        super(builder.advancedChatMessage.message);
        this.clickEvent = builder.advancedChatMessage.clickEvent;
        this.hoverEvent = builder.advancedChatMessage.hoverEvent;
        this.parent = builder.advancedChatMessage.parent;
        this.extra = builder.advancedChatMessage.extra;
    }


    public AdvancedChatMessage(String text) {
        super(text);
        this.clickEvent = null;
        this.hoverEvent = null;
        this.parent = null;
        this.extra = new ArrayList<>();

    }

    private AdvancedChatMessage toAdvanceChatMessage(StringReplacer stringReplacer) {
        Builder builder = AdvancedChatMessage.builder()
                .text(stringReplacer.replace(colorize(this.message)));

        if (this.hoverEvent != null) {
            List<String> newText = new ArrayList<>();
            for (String s : hoverEvent.getText()) {
                newText.add(stringReplacer.replace(colorize(s).replaceAll("\"", "")));
            }
            builder.hoverEvent(new HoverEvent(hoverEvent.getAction(), newText));
        }
        if (this.clickEvent != null) {
            String newValue = stringReplacer.replace(colorize(clickEvent.getValue()).replaceAll("\"", ""));

            builder.clickEvent(new ClickEvent(clickEvent.getAction(), newValue));
        }
        if (this.extra != null && !this.extra.isEmpty()) {
            for (AdvancedChatMessage advancedChatMessage : this.extra) {
                builder.append(advancedChatMessage.toAdvanceChatMessage(stringReplacer));
            }
        }
        return builder.build();
    }

    private ChatMessage toChatMessage(StringReplacer stringReplacer) {
        ChatMessage.Builder builder = ChatMessage.builder()
                .replacer(stringReplacer)
                .text(this.message);

        if (this.hoverEvent != null) {
            builder.hoverEvent(hoverEvent);
        }
        if (this.clickEvent != null) {
            builder.clickEvent(clickEvent);
        }
        if (this.extra != null && !this.extra.isEmpty()) {
            for (AdvancedChatMessage advancedChatMessage : this.extra) {
                builder.append(advancedChatMessage.toChatMessage(stringReplacer));
            }
        }
        return builder.build();
    }

    @Override
    public void send(CommandSender sender) {
        if (sender==null){
            return;
        }
        toChatMessage(s -> colorize(s));
    }

    @Override
    public Message getMessageReplaced(StringReplacer stringReplacer) {
        return new AdvancedChatMessage(this).toAdvanceChatMessage(stringReplacer);
    }

    @Override
    public String send(CommandSender sender, StringReplacer stringReplacer) {
        if (sender==null){
            return "";
        }
        ChatMessage chatMessage = toChatMessage(stringReplacer);
        chatMessage.send(sender);
        return chatMessage.getTextComponent().getText();
    }

    public void addExtra(AdvancedChatMessage advancedChatMessage) {
        advancedChatMessage.parent = this;
        extra.add(advancedChatMessage);

    }

    public AdvancedChatMessage() {
        this("");
    }

    public AdvancedChatMessage(AdvancedChatMessage advancedChatMessage) {
        super(advancedChatMessage.message);
        this.clickEvent = advancedChatMessage.clickEvent;
        this.hoverEvent = advancedChatMessage.hoverEvent;
        this.parent = advancedChatMessage.parent;
        this.extra = advancedChatMessage.extra;
    }


    public Builder newBuilder() {
        return new Builder();
    }

    public static Builder builder() {
        return new AdvancedChatMessage().newBuilder();
    }


    public static class Builder {
        public AdvancedChatMessage advancedChatMessage;

        public Builder() {
            this.advancedChatMessage = new AdvancedChatMessage("");

        }

//        public Builder header(AdvancedChatMessage chatMessage) {
//            this.header = chatMessage;
//            return this;
//        }
//
//        public Builder footer(AdvancedChatMessage chatMessage) {
//            this.footer = chatMessage;
//            return this;
//        }
//
//        public Builder header(String text) {
//            this.header = AdvancedChatMessage.builder().text(text).build();
//            return this;
//        }
//
//        public Builder footer(String text) {
//            this.footer = AdvancedChatMessage.builder().text(text).build();
//            return this;
//        }

        public Builder append(AdvancedChatMessage component) {
            addExtra(component);
            return this;
        }

        public void addExtra(AdvancedChatMessage advancedChatMessage) {
            this.advancedChatMessage.addExtra(advancedChatMessage);
        }

        public Builder newLine() {
            append("\n");
            return this;
        }

        public Builder clickEvent(ClickEvent clickEvent) {
            this.advancedChatMessage.setClickEvent(clickEvent);
            return this;
        }

        public Builder hoverEvent(HoverEvent hoverEvent) {
            this.advancedChatMessage.setHoverEvent(hoverEvent);
            return this;
        }

        public Builder whiteSpace() {
            addExtra(new AdvancedChatMessage(" "));

            return this;
        }


        public Builder append(String message) {
            addExtra(new AdvancedChatMessage(message));
            return this;
        }

        public Builder text(String message) {
            advancedChatMessage.setText(colorize(message));
            return this;
        }

        public Builder extra(List<AdvancedChatMessage> advancedChatMessages) {
            advancedChatMessages.forEach(this::addExtra);
            return this;
        }

        public Builder message(String message) {
            advancedChatMessage.setText(colorize(message));
            return this;
        }


        public AdvancedChatMessage build() {
//            if (this.header != null) {
//                TextComponent newTextComponent = header.textComponent;
//                newTextComponent.addExtra("\n");
//
//                newTextComponent.addExtra(this.textComponent);
//                this.textComponent = newTextComponent;
//            }
//            if (footer != null) {
//                this.textComponent.addExtra("\n");
//                this.textComponent.addExtra(footer.getTextComponent());
//            }
            return new AdvancedChatMessage(this);
        }
    }

    public void setText(String colorize) {
        this.message = colorize;
    }


    public void setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
    }

    public void setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }
}
