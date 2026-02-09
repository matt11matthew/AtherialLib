package me.matthewedevelopment.atheriallib.uuid;

import java.util.UUID;

public abstract class ProfileProvider<A> {
    public abstract Class<A> getClazz();

    public abstract A handleLoading(UUID uuid, String username);
}
