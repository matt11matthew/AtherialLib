package me.matthewedevelopment.atheriallib.events.jump;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerJumpListener  implements Listener {
    private Set<UUID> prevPlayersOnGround = new HashSet<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getVelocity().getY() > 0) {
            double jumpVelocity = (double) 0.42F;
            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                jumpVelocity += (double) ((float) (player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() + 1) * 0.1F);
            }
            if (e.getPlayer().getLocation().getBlock().getType() != Material.LADDER && prevPlayersOnGround.contains(player.getUniqueId())) {
                if (!player.isOnGround() && Double.compare(player.getVelocity().getY(), jumpVelocity) == 0) {
                  PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
                    Bukkit.getPluginManager().callEvent(playerJumpEvent);
                }
            }
        }
        if (player.isOnGround()) {
            prevPlayersOnGround.add(player.getUniqueId());
        } else {
            prevPlayersOnGround.remove(player.getUniqueId());
        }
    }
}
