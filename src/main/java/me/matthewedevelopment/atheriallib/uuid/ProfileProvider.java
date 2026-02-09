package me.matthewedevelopment.atheriallib.uuid;

import me.matthewedevelopment.atheriallib.io.Callback;

import java.util.UUID;

public abstract class ProfileProvider<A> {
    public abstract Class<A> getClazz();

    public abstract void handleLoading(UUID uuid, String username, Callback<A>  callback);
}
