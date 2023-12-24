package me.matthewedevelopment.atheriallib.message.message;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.io.StringReplacer;
import me.matthewedevelopment.atheriallib.message.title.AtherialTitle;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;


/**
 * Created by Matthew E on 8/15/2019 at 6:19 PM for the project atherialapi
 */
public class ActionBarMessage extends Message {
    private int duration = 0;


    public ActionBarMessage(String message, int duration) {
        super(message);
        this.duration = duration;
    }

    @Override
    public String send(CommandSender sender, StringReplacer stringReplacer) {
        if (sender == null) {
            return "";
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;

            AtherialLib atherialPlugin = AtherialTitle.getAtherialPlugin();
            if (atherialPlugin != null) {
                if (duration > 0) {

                    atherialPlugin.getVersionProvider().sendActionBarMessage(player, stringReplacer.replace(colorize(message)), duration);
                } else {
                    atherialPlugin.getVersionProvider().sendActionBarMessage(player, stringReplacer.replace(colorize(message)));

                }
            }

        }
        return "";
    }

    @Override
    public void send(CommandSender sender) {
        if (sender == null) {
            return;
        }
        if (sender instanceof Player) {
            send(sender, ChatUtils::colorize);
        }
    }

    public ActionBarMessage(Builder builder) {
        super(builder.message);
        duration = builder.duration;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ActionBarMessage copy) {
        Builder builder = new Builder();
        builder.message = copy.message;
        builder.duration = copy.getDuration();
        return builder;
    }


    public int getDuration() {
        return duration;
    }

    @Override
    public Message getMessageReplaced(StringReplacer stringReplacer) {
        return new ActionBarMessage(stringReplacer.replace(message), duration);
    }

    /**
     * {@code ActionBarMessage} builder static inner class.
     */
    public static final class Builder {
        private String message;
        private int duration;

        private Builder() {
        }

        /**
         * Sets the {@code message} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param message the {@code message} to set
         * @return a reference to this Builder
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Sets the {@code duration} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param duration the {@code duration} to set
         * @return a reference to this Builder
         */
        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Returns a {@code ActionBarMessage} built from the parameters previously set.
         *
         * @return a {@code ActionBarMessage} built with parameters of this {@code ActionBarMessage.Builder}
         */
        public ActionBarMessage build() {
            return new ActionBarMessage(this);
        }
    }
}
