package me.matthewedevelopment.atheriallib.chat;

import me.matthewedevelopment.atheriallib.SchedulerAdapter;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class ChatPromptHandler  implements Listener {
    private Map<UUID,ChatPrompt> listeningMap;
    private Map<UUID,NewChatPrompt> listeningNewMap;

    public ChatPromptHandler() {
        this.listeningMap = new HashMap<>();
        this.listeningNewMap = new HashMap<>();

        SchedulerAdapter.runGlobalRepeatingTask(20,20, () -> {
            Set<UUID> toRemoveList = new HashSet<>();

            for (UUID uuid : listeningMap.keySet()) {
                ChatPrompt chatPrompt = listeningMap.get(uuid);
                if (System.currentTimeMillis()>chatPrompt.getTimeout()){
                    toRemoveList.add(uuid);
                    Player player = Bukkit.getPlayer(uuid);
                    if (player==null||!player.isOnline()){

                        chatPrompt.getChat().onTimeout(Optional.empty());
                    } else {
                        chatPrompt.getChat().onTimeout(Optional.of(player));

                    }
                }
            }
            toRemoveList.forEach(uuid -> listeningMap.remove(uuid));
        });


        SchedulerAdapter.runGlobalRepeatingTask(20,20, () -> {
            Set<UUID> toRemoveList = new HashSet<>();

            for (UUID uuid : listeningNewMap.keySet()) {
                NewChatPrompt chatPrompt = listeningNewMap.get(uuid);
                if (System.currentTimeMillis()>chatPrompt.getTimeout()){
                    toRemoveList.add(uuid);
                    Player player = Bukkit.getPlayer(uuid);
                    if (player==null||!player.isOnline()){

                        chatPrompt.getChat().onTimeout(Optional.empty());
                    } else {
                        chatPrompt.getChat().onTimeout(Optional.of(player));

                    }
                }
            }
            toRemoveList.forEach(uuid -> listeningNewMap.remove(uuid));
        });

    }


    public void chatPrompt(Player player, String message,Chat chat, long timeout){
        player.sendMessage(ChatUtils.colorize(message));
        this.listeningMap.put(player.getUniqueId(),new ChatPrompt(message,chat,System.currentTimeMillis()+timeout));

    }


    public void newChatPrompt(Player player, Component message, Chat chat, long timeoutMillis) {
        // This works if you depend on Spigot-API 1.16.5+ (adventure shaded in)

        AdventureCompat.send(player,message);

        long expiresAt = System.currentTimeMillis() + Math.max(0L, timeoutMillis);
        this.listeningNewMap.put(player.getUniqueId(), new NewChatPrompt(message, chat, expiresAt));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(this.listeningMap.containsKey(event.getPlayer().getUniqueId())){
            ChatPrompt chatPrompt = this.listeningMap.get(event.getPlayer().getUniqueId());
            this.listeningMap.remove(event.getPlayer().getUniqueId());
            chatPrompt.getChat().onTimeout(Optional.empty());
        }



        if(this.listeningNewMap.containsKey(event.getPlayer().getUniqueId())){
            NewChatPrompt chatPrompt = this.listeningNewMap.get(event.getPlayer().getUniqueId());
            this.listeningNewMap.remove(event.getPlayer().getUniqueId());
            chatPrompt.getChat().onTimeout(Optional.empty());
        }
    }

    public boolean hasPrompt(Player player) {
        return listeningMap.containsKey(player.getUniqueId()) || listeningNewMap.containsKey(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

        if (this.listeningNewMap.containsKey(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
            String baseMessage = event.getMessage();
            NewChatPrompt chatPrompt = this.listeningNewMap.get(event.getPlayer().getUniqueId());
            String lowerCase = baseMessage.toLowerCase();
            if (System.currentTimeMillis()>chatPrompt.getTimeout()){
                this.listeningNewMap.remove(event.getPlayer().getUniqueId());
                chatPrompt.getChat().onTimeout(Optional.of(event.getPlayer()));
                return;
            }
            if(lowerCase.equals("cancel")) {
                this.listeningNewMap.remove(event.getPlayer().getUniqueId());
                chatPrompt.getChat().onCancel(event.getPlayer());
                return;
            } else {
                this.listeningNewMap.remove(event.getPlayer().getUniqueId());
                chatPrompt.getChat().onChat(event.getPlayer(),event.getMessage());
            }
        }



        if (this.listeningMap.containsKey(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
            String baseMessage = event.getMessage();
            ChatPrompt chatPrompt = this.listeningMap.get(event.getPlayer().getUniqueId());
            String lowerCase = baseMessage.toLowerCase();
            if (System.currentTimeMillis()>chatPrompt.getTimeout()){
                this.listeningMap.remove(event.getPlayer().getUniqueId());
                chatPrompt.getChat().onTimeout(Optional.of(event.getPlayer()));
                return;
            }
            if(lowerCase.equals("cancel")) {
                this.listeningMap.remove(event.getPlayer().getUniqueId());
                chatPrompt.getChat().onCancel(event.getPlayer());
                return;
            } else {
                this.listeningMap.remove(event.getPlayer().getUniqueId());
                chatPrompt.getChat().onChat(event.getPlayer(),event.getMessage());
            }
        }
    }
}
