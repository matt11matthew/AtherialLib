package me.matthewedevelopment.atheriallib.config.sound;

import me.matthewedevelopment.atheriallib.AtherialLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class AtherialSound {
    private String name;
    private float volume, pitch;

    public static AtherialSound of(Sound sound, float vol, float pitch) {
        return new AtherialSound(sound.toString(),vol,pitch);
    }

    public String getSound() {
        return name;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public AtherialSound(String name, float volume, float pitch) {
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(Player p){
        Sound sound = null;
        try {
            sound = Sound.valueOf(name);
        }catch (Exception e) {
            sound = null;
        }
        if (sound==null)return;

        if (AtherialLib.getInstance().isNmsEnabled()) {

            if (         AtherialLib.getInstance().getVersionProvider().is1_8()){

                p.playSound(p.getLocation(),sound, volume,pitch);
            } else {

                p.getWorld().playSound(p, sound, volume, pitch);
            }

        } else {

            p.getWorld().playSound(p, sound, volume, pitch);
        }
    }
}
