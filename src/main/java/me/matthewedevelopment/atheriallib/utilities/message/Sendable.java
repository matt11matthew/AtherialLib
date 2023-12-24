package me.matthewedevelopment.atheriallib.utilities.message;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Matthew E on 5/25/2019 at 11:44 AM for the project atherialapi
 */
public interface Sendable {
    void send(CommandSender sender);

    default void send(CommandSender... commandSenders) {
        Arrays.stream(commandSenders).forEach(this::send);
    }
    default void send(List<CommandSender> commandSenders) {
        commandSenders.forEach(this::send);
    }
}
