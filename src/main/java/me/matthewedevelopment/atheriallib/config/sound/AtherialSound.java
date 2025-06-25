package me.matthewedevelopment.atheriallib.config.sound;

import me.matthewedevelopment.atheriallib.AtherialLib;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class AtherialSound {
    private String name;
    private float volume, pitch;
    private boolean enabled = true;

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

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public AtherialSound(String name, float volume, float pitch, boolean enabled) {
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
        this.enabled =enabled;
    }

    public AtherialSound(String name, float volume, float pitch) {
        this(name,volume,pitch,true);

    }

    public void play(Player p){
        if (!enabled)return;
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
