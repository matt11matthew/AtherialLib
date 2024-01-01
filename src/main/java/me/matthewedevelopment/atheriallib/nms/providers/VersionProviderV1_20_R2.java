package me.matthewedevelopment.atheriallib.nms.providers;

import io.netty.channel.Channel;
import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.nms.NbtAPI;
import me.matthewedevelopment.atheriallib.nms.Version;
import me.matthewedevelopment.atheriallib.nms.VersionProvider;
import me.matthewedevelopment.atheriallib.nms.WorldBorderAction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.World;
import org.bukkit.block.Block;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class VersionProviderV1_20_R2 extends VersionProvider {
    private NbtAPI nbtAPI;


    public VersionProviderV1_20_R2(AtherialLib atherialPlugin) {
        super(Version.V1_20_R2,atherialPlugin);
        this.nbtAPI = new NbtAPI() {
            @Override
            public Version getVersion() {
                return Version.V1_20_R1;
            }

            @Override
            public VersionProvider getVersionProvider() {
                return VersionProviderV1_20_R2.this;
            }

            @Override
            public ItemStack setTagInt(ItemStack itemStack, String key, int value) {
//                RtagItem tag = new RtagItem(itemStack);
//                tag.set(key, value);
//
//                return tag.getItem();
                return itemStack;
            }

            @Override
            public int getTagInt(ItemStack itemStack, String key) {
//                RtagItem tag = new RtagItem(itemStack);
//                 return tag.get(key);
                return -1;
            }

            @Override
            public String getTagString(ItemStack itemStack, String key) {
//                RtagItem tag = new RtagItem(itemStack);
//                return tag.get(key);
                return null;
            }

            @Override
            public boolean hasTagKey(ItemStack itemStack, String key) {
                return false;
//                return new RtagItem(itemStack).hasTag(key);
            }

            @Override
            public ItemStack removeTag(ItemStack itemStack, String key) {
                return itemStack;
//
//                RtagItem rtagItem = new RtagItem(itemStack);
//                rtagItem.remove(key);
//
//                return rtagItem.getItem();
            }

            @Override
            public ItemStack setTagString(ItemStack itemStack, String key, String value) {
//                RtagItem rtagItem = new RtagItem(itemStack);
//                rtagItem.set(key,value);
//
//                return rtagItem.getItem();
                return itemStack;
            }
        };


    }

    @Override
    public void hidePlayer(Player player, Entity entity) {
//        sendPacket(player, packetPlayOutEntityDestroy);
    }

    @Override
    public void sendRawActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
//        CraftPlayer craftPlayer = (CraftPlayer) player;
//        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(message);
//        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatBaseComponent, ChatMessageType.GAME_INFO, player.getUniqueId());
//        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }

    @Override
    public void sendTitle(Player player, String title, String subTitle, int stay, int fadeIn, int fadeOut) {
//        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \""+ title +"\"}");
//        IChatBaseComponent subChatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \""+ subTitle +"\"}");
//
//        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle( PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
//        PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle( PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subChatTitle);
//        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
//
//
//        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutTitle);
//        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutSubTitle);
//        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
        player.sendTitle(title,subTitle,fadeIn, stay, fadeOut);
    }

    @Override
    public void setBlockSuperFast(Block block, int blockId, byte data, boolean applyPhysics) {
//        net.minecraft.server.v1_16_R1.World w = ((CraftWorld)block.getWorld()).getHandle();
//        Chunk chunk = w.getChunkAt(block.getX() >> 4, block.getZ() >> 4);
//        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
//        int combined = blockId + (data << 12);
//        IBlockData ibd = net.minecraft.server.v1_16_R1.Block.getByCombinedId(combined);
//        if (applyPhysics) {
//            w.setTypeAndData(bp, ibd, 3);
//        } else {
//            w.setTypeAndData(bp, ibd, 2);
//        }
//        chunk.setType(bp, ibd,applyPhysics);
    }

    @Override
    public void setBlockLightLevel(Block block, int lightLevel) {

    }

    @Override
    public void inject(PacketInjector packetInjector) {
//        try {
//            CraftPlayer craftPlayer = (CraftPlayer) packetInjector.getPlayer();
//            Channel channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
//            if (channel.pipeline().get(packetInjector.getName()) == null) {
//                channel.pipeline().addBefore("packet_handler", packetInjector.getName(), packetInjector);
//            }
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
    }

    @Override
    public void disableAI(Entity entity) {

    }

    @Override
    public void hideArrow(Player player, Arrow arrow) {
//        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(((CraftArrow) arrow).getHandle().getId());
//        sendPacket(player, packetPlayOutEntityDestroy);
    }

    @Override
    public NbtAPI getNbtAPI() {
        return nbtAPI;
    }

    @Override
    public void sendWorldBorderPacket(Player player, World world, int centerX, int centerY, int size, WorldBorderAction worldBorderAction) {
//        WorldBorder border = new WorldBorder();
//        border.setCenter(centerX, centerY);
//        border.setSize(size);
//        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(border, PacketPlayOutWorldBorder.EnumWorldBorderAction.valueOf(worldBorderAction.name()));
//
//        sendPacket(player, packet);
    }
}
