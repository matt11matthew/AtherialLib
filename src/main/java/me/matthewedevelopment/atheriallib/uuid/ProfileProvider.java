package me.matthewedevelopment.atheriallib.uuid;

import me.matthewedevelopment.atheriallib.io.Callback;

import java.util.UUID;

@FunctionalInterface
public interface  ProfileProvider<A> {
    void handleLoading(UUID uuid, String username, Callback<A> callback);
}
