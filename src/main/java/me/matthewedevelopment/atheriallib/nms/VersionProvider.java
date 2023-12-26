package me.matthewedevelopment.atheriallib.nms;


import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.nms.providers.PacketInjector;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.matthewedevelopment.atheriallib.utilities.ChatUtils.colorize;


/**
 * Created by Matthew Eisenberg on 5/20/2018 at 8:39 PM for the project atherialapi
 */
public abstract class VersionProvider {
    private Version version;
    private Map<String, VersionProviderAddonHandler<?>> addonMap;
    private boolean debug;
    protected Map<Player, BukkitTask> pendingMessageMap;
    protected AtherialLib atherialPlugin;

    public VersionProvider(Version version, AtherialLib atherialPlugin) {
        this.version = version;
        this.atherialPlugin = atherialPlugin;
        this.addonMap = new ConcurrentHashMap<>();
        this.pendingMessageMap = new ConcurrentHashMap<>();

    }

    public <T extends VersionProviderAddon, D extends VersionProviderAddonHandler<T>> void addAddonHandler(D addonHandler) {
        if (!addonMap.containsKey(addonHandler.getName())) {
            addonHandler.setup(this.version);
            addonMap.put(addonHandler.getName(), addonHandler);
        }
    }


    public void sendActionBarMessage(Player player, String message, int duration) {
        if (!atherialPlugin.isNmsEnabled()) {
            return;
        }
        cancelPendingMessages(player);
        final BukkitTask messageTask = new BukkitRunnable() {
            private int count = 0;

            @Override
            public void run() {
                if (count >= (duration - 3)) {
                    this.cancel();
                }
                sendActionBarMessage(player, message);
                count++;
            }
        }.runTaskTimer(atherialPlugin, 0L, 20L);
        pendingMessageMap.put(player, messageTask);
    }

    public abstract void sendRawActionBarMessage(Player player, String message);

    public void sendActionBarMessage(Player player, String message) {
        if (!atherialPlugin.isNmsEnabled()) {
            return;
        }
        if (!atherialPlugin.getVersionProvider().isGreaterThan1_13()){
            sendRawActionBarMessage(player, "{\"text\": \"" + colorize(message) + "\"}");

        } else {
            sendRawActionBarMessage(player,  colorize(message));

        }
    }

    private void cancelPendingMessages(Player player) {
        if (pendingMessageMap.containsKey(player)) {
            pendingMessageMap.get(player).cancel();
            pendingMessageMap.remove(player);
        }
    }

    public abstract void sendTitle(Player player, String title, String subTitle, int stay, int fadeIn, int fadeOut);

    public <T extends VersionProviderAddon> T getAddon(Class<T> clazz) {
        for (VersionProviderAddonHandler<?> value : addonMap.values()) {

            if (debug) {
                Bukkit.getServer().broadcastMessage(value.getClass().getName() + ":" + clazz.getName());
            }
            if (value.getBaseClass().getName().equalsIgnoreCase(clazz.getName())) {
                return (T) value.getAddon();
            }
        }
        return null;
    }

    public Version getVersion() {
        return version;
    }

    public Class<?> getNMSClass(String simpleName) {
        return version.getNMSClass(simpleName);
    }

    public void sendPacket(Player player, Object packet) {

        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public abstract void setBlockSuperFast(Block block, int blockId, byte data, boolean applyPhysics);


    public abstract void setBlockLightLevel(Block block, int lightLevel);

    public abstract void inject(PacketInjector packetInjector);


    public abstract void disableAI(Entity entity);

    public abstract NbtAPI getNbtAPI();

    public abstract void hideArrow(Player player, Arrow arrow);

    public abstract void hidePlayer(Player player, Entity entity);

    public abstract void sendWorldBorderPacket(Player player, World world, int centerX, int centerY, int size, WorldBorderAction worldBorderAction);

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isGreaterThan1_13() {
        switch (version) {

            case V1_7_R4:
            case V1_8_R3:
            case V1_9_R1:
            case V1_9_R2:
            case V1_10:
            case V1_11:
            case V1_12_R1:
                return false;
            default:
                return true;
        }

    }
}