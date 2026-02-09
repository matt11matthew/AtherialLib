package me.matthewedevelopment.atheriallib.uuid;

import lombok.Getter;
import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.database.registry.DataObjectRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UUIDProfileRegistry  extends DataObjectRegistry<UUIDProfile> implements Listener {
   @Getter
   private final ProfileProvider profileProvider;
    private Map<String, UUID> nameMap = new ConcurrentHashMap<>();
    private Map<UUID, String> reverseNameMap =  new ConcurrentHashMap<>();

    public UUIDProfileRegistry(ProfileProvider profileProvider) {
        super(UUIDProfile.class);
        this.profileProvider = profileProvider;
        instance=this;
    }

    @Deprecated(since = "1.1.61")
    public UUIDProfileRegistry() {
        super(UUIDProfile.class);
        profileProvider = null;
        instance = this;
    }

    @Override
    public void onLoadAll(List<UUIDProfile> list) {
        for (UUIDProfile uuidProfile : list) {
            nameMap.put(uuidProfile.getUsername().toLowerCase(), uuidProfile.getUuid());
            reverseNameMap.put( uuidProfile.getUuid(), uuidProfile.getUsername());
            uuidProfile.load();
        }
        super.onLoadAll(list);
    }

    private static UUIDProfileRegistry instance;

    public static UUIDProfileRegistry get() {
        return instance;
    }

    public UUID getUUIDFromUsername(String username) {
        return nameMap.get(username.toLowerCase());
    }
    public String getUsernameFromUUID(UUID id) {
        return reverseNameMap.get(id);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (!map.containsKey(event.getPlayer().getUniqueId())){

            insert(event.getPlayer());
        }
    }

    public boolean hasPlayedBefore(String username) {
        return nameMap.containsKey(username.toLowerCase());
    }


    public void insert(Player p){
        UUIDProfile uuidProfile = new UUIDProfile(p.getUniqueId(), p.getName());
        uuidProfile.load();
        insertAsync(uuidProfile,() -> {
            nameMap.put(uuidProfile.getUsername().toLowerCase(),uuidProfile.getUuid());
            reverseNameMap.put(uuidProfile.getUuid(),uuidProfile.getUsername());
        });
    }


    @Override
    public void onRegister() {

        Bukkit.getPluginManager().registerEvents(this, AtherialLib.get());

    }
}
