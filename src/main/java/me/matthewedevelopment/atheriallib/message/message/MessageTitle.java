package me.matthewedevelopment.atheriallib.message.message;

import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.message.title.AtherialTitle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;


public class MessageTitle extends Message {
    public String subTitle;
    public int stay;
    public int fadeIn;
    public int fadeOut;

    public MessageTitle(String title, String subTitle, int stay, int fadeIn, int fadeOut) {
        super(title);
        this.subTitle = subTitle;
        this.stay = stay;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
    }

    private MessageTitle(Builder builder) {
        super(builder.message);
        subTitle = builder.subTitle;
        stay = builder.stay;
        fadeIn = builder.fadeIn;
        fadeOut = builder.fadeOut;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(MessageTitle copy) {
        Builder builder = new Builder();
        builder.message = copy.message;
        builder.subTitle = copy.getSubTitle();
        builder.stay = copy.getStay();
        builder.fadeIn = copy.getFadeIn();
        builder.fadeOut = copy.getFadeOut();
        return builder;
    }

    @Override
    public void send(CommandSender sender) {
        if (sender==null){
            return;
        }
        if (sender instanceof Player) {
            sendTitle((Player) sender);
        }
    }

    @Override
    public Message getMessageReplaced(StringReplacer stringReplacer) {
        Builder builder = MessageTitle.builder();
        if (this.message != null) {
            builder.title(stringReplacer.replace(colorize(this.message)));
        }
        if (this.subTitle != null) {
            builder.subTitle(stringReplacer.replace(colorize(this.subTitle)));
        }
        return builder.stay(this.stay)
                .fadeIn(this.fadeIn)
                .fadeOut(this.fadeOut).build();
    }

    public String getSubTitle() {
        return subTitle;
    }

    public int getStay() {
        return stay;
    }

    public int getFadeIn() {
        return fadeIn;
    }

    public int getFadeOut() {
        return fadeOut;
    }

    @Override
    public String send(CommandSender sender, StringReplacer stringReplacer) {
        if (sender==null){
            return "";
        }
        if (sender instanceof Player) {
            sendTitle((Player) sender, stringReplacer);
        }
        return super.send(sender, stringReplacer);
    }

    public void sendTitle(Player player) {
        sendTitle(player, null);
    }

    public void sendTitle(Player player, StringReplacer stringReplacer) {
        AtherialTitle.Builder builder = AtherialTitle.builder()
                .stay(stay == 0 ? 40 : stay)
                .fadeIn(fadeIn)
                .fadeOut(fadeOut);
        if ((this.message != null) && !this.message.isEmpty()) {
            builder.title(stringReplacer != null ? stringReplacer.replace(colorize(message).replace("%player%", player.getName())) : colorize(message).replace("%player%", player.getName()));
        }
        if ((this.subTitle != null) && !this.subTitle.isEmpty()) {
            builder.subTitle(stringReplacer != null ? stringReplacer.replace(colorize(subTitle).replace("%player%", player.getName())) : colorize(subTitle).replace("%player%", player.getName()));
        }
        builder.build().send(player);

    }

    /**
     * {@code MessageTitle} builder static inner class.
     */
    public static final class Builder {
        private String message;
        private String subTitle;
        private int stay;
        private int fadeIn;
        private int fadeOut;

        private Builder() {
        }

        /**
         * Sets the {@code message} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param title the {@code message} to set
         * @return a reference to this Builder
         */
        public Builder title(String title) {
            this.message = title;
            return this;
        }

        /**
         * Sets the {@code subTitle} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param subTitle the {@code subTitle} to set
         * @return a reference to this Builder
         */
        public Builder subTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        /**
         * Sets the {@code stay} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param stay the {@code stay} to set
         * @return a reference to this Builder
         */
        public Builder stay(int stay) {
            this.stay = stay;
            return this;
        }

        /**
         * Sets the {@code fadeIn} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param fadeIn the {@code fadeIn} to set
         * @return a reference to this Builder
         */
        public Builder fadeIn(int fadeIn) {
            this.fadeIn = fadeIn;
            return this;
        }

        /**
         * Sets the {@code fadeOut} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param fadeOut the {@code fadeOut} to set
         * @return a reference to this Builder
         */
        public Builder fadeOut(int fadeOut) {
            this.fadeOut = fadeOut;
            return this;
        }

        /**
         * Returns a {@code MessageTitle} built from the parameters previously set.
         *
         * @return a {@code MessageTitle} built with parameters of this {@code MessageTitle.Builder}
         */
        public MessageTitle build() {
            return new MessageTitle(this);
        }
    }
}