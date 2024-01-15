package me.matthewedevelopment.atheriallib.dependency.luckperms;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.dependency.Dependency;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.platform.PlayerAdapter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LuckPermsDependency  extends Dependency {
    public LuckPermsDependency(AtherialLib plugin) {
        super("LuckPermsDependency", plugin);
    }
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        this.luckPerms = LuckPermsProvider.get();
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public String getPrefix(Player player) {
        String prefix = ChatColor.GRAY+"";
        PlayerAdapter<Player> playerAdapter = luckPerms.getPlayerAdapter(Player.class);
        if (playerAdapter!=null){

            CachedMetaData metaData = playerAdapter.getMetaData((Player) player);
            if (metaData!=null){
                prefix=metaData.getPrefix();
            }
        }
        if (prefix==null){
            prefix= ChatColor.GRAY+"";
        }
        return prefix;
    }

    public static String getPrefixedName(Player player) {
        if (player==null)return "NULL";
        LuckPermsDependency luckPermsDependency = get();
        if (luckPermsDependency==null)return player.getName();
        return luckPermsDependency.getPrefix(player)+player.getName();
    }
    public static LuckPermsDependency get() {
        return AtherialLib.getInstance().getDependencyManager().getDependency(LuckPermsDependency.class);
    }
    @Override
    public void onPreEnable() {

    }

    @Override
    public void onDisable() {

    }
}
