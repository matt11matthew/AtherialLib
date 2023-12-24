package me.matthewedevelopment.atheriallib.utilities.message;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Created by Matthew E on 5/25/2019 at 11:35 AM for the project atherialapi
 */
public abstract class Message<T extends Message<T,F>, F extends Message.Builder<T>> implements Sendable {

    public abstract F newBuilder();

    public static abstract class Builder<T extends Message>  implements Sendable {

        public abstract T build();

        @Override
        public void send(CommandSender... commandSenders) {
            build().send(commandSenders);
        }

        @Override
        public void send(List<CommandSender> commandSenders) {
            build().send(commandSenders);
        }

        @Override
        public void send(CommandSender sender) {
            build().send(sender);
        }
    }

}
