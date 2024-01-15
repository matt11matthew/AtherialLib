package me.matthewedevelopment.atheriallib.chat;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.utilities.ChatUtils;
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

    public ChatPromptHandler() {
        this.listeningMap = new HashMap<>();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(AtherialLib.getInstance(),() -> {

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
        },20,20);
    }

    public void chatPrompt(Player player, String message,Chat chat, long timeout){
        player.sendMessage(ChatUtils.colorize(message));
        this.listeningMap.put(player.getUniqueId(),new ChatPrompt(message,chat,System.currentTimeMillis()+timeout));

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(this.listeningMap.containsKey(event.getPlayer().getUniqueId())){
            ChatPrompt chatPrompt = this.listeningMap.get(event.getPlayer().getUniqueId());
            chatPrompt.getChat().onTimeout(Optional.empty());
            this.listeningMap.remove(event.getPlayer().getUniqueId());
        }
    }
    public boolean hasPrompt(Player player) {
        return listeningMap.containsKey(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (this.listeningMap.containsKey(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
            String baseMessage = event.getMessage();
            ChatPrompt chatPrompt = this.listeningMap.get(event.getPlayer().getUniqueId());
            String lowerCase = baseMessage.toLowerCase();
            if (System.currentTimeMillis()>chatPrompt.getTimeout()){
                chatPrompt.getChat().onTimeout(Optional.of(event.getPlayer()));
                this.listeningMap.remove(event.getPlayer().getUniqueId());
                return;
            }
            if(lowerCase.equals("n")||lowerCase.equals("cancel")) {
                chatPrompt.getChat().onCancel(event.getPlayer());
                this.listeningMap.remove(event.getPlayer().getUniqueId());
                return;
            } else {
                chatPrompt.getChat().onChat(event.getPlayer(),event.getMessage());
                this.listeningMap.remove(event.getPlayer().getUniqueId());
            }
        }
    }
}
