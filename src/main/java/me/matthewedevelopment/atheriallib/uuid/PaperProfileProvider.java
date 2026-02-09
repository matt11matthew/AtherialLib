package me.matthewedevelopment.atheriallib.uuid;

import com.destroystokyo.paper.profile.PlayerProfile;

import java.util.UUID;

public abstract class PaperProfileProvider<A> {
    public abstract Class<A> getClazz();

    public abstract A handleLoading(UUID uuid, String username);
}
