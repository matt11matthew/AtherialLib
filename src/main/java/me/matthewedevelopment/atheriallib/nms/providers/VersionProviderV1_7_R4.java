package me.matthewedevelopment.atheriallib.nms.providers;


import io.netty.channel.Channel;
import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.nms.NbtAPI;
import me.matthewedevelopment.atheriallib.nms.Version;
import me.matthewedevelopment.atheriallib.nms.VersionProvider;
import me.matthewedevelopment.atheriallib.nms.WorldBorderAction;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import  net.minecraft.server.v1_7_R4.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

/**
 * Created by Matthew Eisenberg on 5/20/2018 at 8:44 PM for the project atherialapi
 */
public class VersionProviderV1_7_R4 extends VersionProvider {
    private NbtAPI nbtAPI;

    public VersionProviderV1_7_R4(AtherialLib atherialPlugin) {
        super(Version.V1_7_R4,atherialPlugin);

        this.nbtAPI = new NbtAPI() {
            @Override
            public Version getVersion() {
                return Version.V1_7_R4;
            }

            @Override
            public VersionProvider getVersionProvider() {
                return VersionProviderV1_7_R4.this;
            }

            @Override
            public ItemStack setTagInt(ItemStack itemStack, String key, int value) {
                NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                nbtTagCompound.setInt(key, value);
                return setTag(itemStack, nbtTagCompound);
            }

            public NBTTagCompound getNbtTagCompound(ItemStack itemStack) {
                net.minecraft.server.v1_7_R4.ItemStack itemStack1 = CraftItemStack.asNMSCopy(itemStack);
                return (itemStack1.hasTag() && itemStack1.getTag() != null) ? itemStack1.getTag() : new NBTTagCompound();
            }

            @Override
            public int getTagInt(ItemStack itemStack, String key) {
                NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                return hasTagKey(itemStack, key) ? nbtTagCompound.getInt(key) : -1;
            }

            public ItemStack setTag(ItemStack itemStack, NBTTagCompound nbtTagCompound) {
                net.minecraft.server.v1_7_R4.ItemStack itemStack1 = CraftItemStack.asNMSCopy(itemStack);
                itemStack1.setTag(nbtTagCompound);
                return CraftItemStack.asBukkitCopy(itemStack1);

            }

            @Override
            public String getTagString(ItemStack itemStack, String key) {
                NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                return hasTagKey(itemStack, key) ? nbtTagCompound.getString(key) : null;
            }

            @Override
            public boolean hasTagKey(ItemStack itemStack, String key) {
                NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                return nbtTagCompound.hasKey(key);
            }

            @Override
            public ItemStack removeTag(ItemStack itemStack, String key) {
                net.minecraft.server.v1_7_R4.NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                net.minecraft.server.v1_7_R4.ItemStack itemStack1 = org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asNMSCopy(itemStack);
                if (nbtTagCompound.hasKey(key)) {

                    nbtTagCompound.remove(key);
                }
                itemStack1.setTag(nbtTagCompound);
                return org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack.asBukkitCopy(itemStack1);
            }

            @Override
            public ItemStack setTagString(ItemStack itemStack, String key, String value) {
                NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                nbtTagCompound.setString(key, value);
                return setTag(itemStack, nbtTagCompound);
            }
        };

    }


    @Override
    public void sendRawActionBarMessage(Player player, String message) {

    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int stay, int fadeIn, int fadeOut) {

    }

    @Override
    public void setBlockSuperFast(Block block, int blockId, byte data, boolean applyPhysics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBlockLightLevel(Block block, int lightLevel) {
//        int x = block.getX();
//        int y= block.getY();
//        int z= block.getZ();
//        ((CraftWorld) block.getWorld()).getHandle().a(EnumSkyBlock.BLOCK, x, y, z, lightLevel);
    }

    @Override
    public void inject(PacketInjector packetInjector) {
        try {
            CraftPlayer craftPlayer = (CraftPlayer) packetInjector.getPlayer();
            Field m = craftPlayer.getHandle().playerConnection.networkManager.getClass().getDeclaredField("m");
            m.setAccessible(true);

            Channel channel = (Channel) m.get(Channel.class);
            if (channel.pipeline().get(packetInjector.getName()) == null) {
                channel.pipeline().addBefore("packet_handler", packetInjector.getName(), packetInjector);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void disableAI(Entity entity) {
        throw new UnsupportedOperationException("Not in 1.7");
    }

    @Override
    public NbtAPI getNbtAPI() {
        return nbtAPI;
    }

    @Override
    public void hideArrow(Player player, Arrow arrow) {
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(((CraftArrow) arrow).getHandle().getId());
        sendPacket(player, packetPlayOutEntityDestroy);
    }

    @Override
    public void hidePlayer(Player player, Entity entity) {
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(((CraftPlayer) entity).getHandle().getId());
        sendPacket(player, packetPlayOutEntityDestroy);
    }

    @Override
    public void sendWorldBorderPacket(Player player, World world, int centerX, int centerY, int size, WorldBorderAction worldBorderAction) {
        throw new UnsupportedOperationException("Not supported in 1.7");
    }
}
