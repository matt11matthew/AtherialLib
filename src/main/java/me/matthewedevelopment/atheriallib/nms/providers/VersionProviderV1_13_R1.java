package me.matthewedevelopment.atheriallib.nms.providers;


import io.netty.channel.Channel;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.nms.NbtAPI;
import me.matthewedevelopment.atheriallib.nms.Version;
import me.matthewedevelopment.atheriallib.nms.VersionProvider;
import me.matthewedevelopment.atheriallib.nms.WorldBorderAction;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Matthew Eisenberg on 5/20/2018 at 8:44 PM for the project atherialapi
 */
public class VersionProviderV1_13_R1 extends VersionProvider {
    private NbtAPI nbtAPI;


    public VersionProviderV1_13_R1(AtherialLib atherialPlugin) {
        super(Version.V1_13_R1,atherialPlugin);

        this.nbtAPI = new NbtAPI() {
            @Override
            public Version getVersion() {
                return Version.V1_13_R1;
            }

            @Override
            public VersionProvider getVersionProvider() {
                return VersionProviderV1_13_R1.this;
            }

            @Override
            public ItemStack setTagInt(ItemStack itemStack, String key, int value) {
                NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                nbtTagCompound.setInt(key, value);
                return setTag(itemStack, nbtTagCompound);
            }
            @Override
            public ItemStack removeTag(ItemStack itemStack, String key) {
                net.minecraft.server.v1_13_R1.NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                net.minecraft.server.v1_13_R1.ItemStack itemStack1 = org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack.asNMSCopy(itemStack);
                if (nbtTagCompound.hasKey(key)) {

                    nbtTagCompound.remove(key);
                }
                itemStack1.setTag(nbtTagCompound);
                return org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack.asBukkitCopy(itemStack1);
            }
            public NBTTagCompound getNbtTagCompound(ItemStack itemStack) {
                net.minecraft.server.v1_13_R1.ItemStack itemStack1 = CraftItemStack.asNMSCopy(itemStack);
                return (itemStack1.hasTag() && itemStack1.getTag() != null) ? itemStack1.getTag() : new NBTTagCompound();
            }

            @Override
            public int getTagInt(ItemStack itemStack, String key) {
                NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                return hasTagKey(itemStack, key) ? nbtTagCompound.getInt(key) : -1;
            }

            public ItemStack setTag(ItemStack itemStack, NBTTagCompound nbtTagCompound) {
                net.minecraft.server.v1_13_R1.ItemStack itemStack1 = CraftItemStack.asNMSCopy(itemStack);
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
            public ItemStack setTagString(ItemStack itemStack, String key, String value) {
                NBTTagCompound nbtTagCompound = getNbtTagCompound(itemStack);
                nbtTagCompound.setString(key, value);
                return setTag(itemStack, nbtTagCompound);
            }
        };
    }

    @Override
    public void hidePlayer(Player player, Entity entity) {
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(((CraftPlayer) entity).getHandle().getId());
        sendPacket(player, packetPlayOutEntityDestroy);
    }

    @Override
    public void sendRawActionBarMessage(Player player, String message) {
        org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer craftPlayer = (org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer) player;
        net.minecraft.server.v1_13_R1.IChatBaseComponent chatBaseComponent = net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer.a(message);
        net.minecraft.server.v1_13_R1.PacketPlayOutChat packetPlayOutChat = new net.minecraft.server.v1_13_R1.PacketPlayOutChat(chatBaseComponent, net.minecraft.server.v1_13_R1.ChatMessageType.GAME_INFO);
        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int stay, int fadeIn, int fadeOut) {
        net.minecraft.server.v1_13_R1.IChatBaseComponent chatTitle = net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \""+ title +"\"}");
        net.minecraft.server.v1_13_R1.IChatBaseComponent subChatTitle = net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer.a("{\"text\": \""+ subTitle +"\"}");

        net.minecraft.server.v1_13_R1.PacketPlayOutTitle packetPlayOutTitle = new net.minecraft.server.v1_13_R1.PacketPlayOutTitle( net.minecraft.server.v1_13_R1.PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        net.minecraft.server.v1_13_R1.PacketPlayOutTitle packetPlayOutSubTitle = new net.minecraft.server.v1_13_R1.PacketPlayOutTitle( net.minecraft.server.v1_13_R1.PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subChatTitle);
        net.minecraft.server.v1_13_R1.PacketPlayOutTitle length = new net.minecraft.server.v1_13_R1.PacketPlayOutTitle(fadeIn, stay, fadeOut);


        ((org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutTitle);
        ((org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutSubTitle);
        ((org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    @Override
    public void setBlockSuperFast(Block block, int blockId, byte data, boolean applyPhysics) {
        net.minecraft.server.v1_13_R1.World w = ((CraftWorld)block.getWorld()).getHandle();
        net.minecraft.server.v1_13_R1.Chunk chunk = w.getChunkAt(block.getX() >> 4, block.getZ() >> 4);
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        int combined = blockId + (data << 12);
        IBlockData ibd = net.minecraft.server.v1_13_R1.Block.getByCombinedId(combined);
        if (applyPhysics) {
            w.setTypeAndData(bp, ibd, 3);
        } else {
            w.setTypeAndData(bp, ibd, 2);
        }
        chunk.a(bp, ibd,applyPhysics);
    }

    @Override
    public void setBlockLightLevel(Block block, int lightLevel) {

    }

    @Override
    public void inject(PacketInjector packetInjector) {
        try {
            CraftPlayer craftPlayer = (CraftPlayer) packetInjector.getPlayer();
            Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
            if (channel.pipeline().get(packetInjector.getName()) == null) {
                channel.pipeline().addBefore("packet_handler", packetInjector.getName(), packetInjector);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void disableAI(Entity entity) {

    }

    @Override
    public void hideArrow(Player player, Arrow arrow) {
        net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy(((CraftArrow) arrow).getHandle().getId());
        sendPacket(player, packetPlayOutEntityDestroy);
    }

    @Override
    public NbtAPI getNbtAPI() {
        return nbtAPI;
    }

    @Override
    public void sendWorldBorderPacket(Player player, World world, int centerX, int centerY, int size, WorldBorderAction worldBorderAction) {
        WorldBorder border = new WorldBorder();
        border.setCenter(centerX, centerY);
        border.setSize(size);
        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(border, PacketPlayOutWorldBorder.EnumWorldBorderAction.valueOf(worldBorderAction.name()));

        sendPacket(player, packet);
    }
}
