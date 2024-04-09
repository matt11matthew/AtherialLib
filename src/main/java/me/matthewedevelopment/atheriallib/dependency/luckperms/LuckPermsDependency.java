package me.matthewedevelopment.atheriallib.dependency.luckperms;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.dependency.Dependency;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.platform.PlayerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    public String getPrefix(UUID uuid) {
        String prefix = ChatColor.GRAY+"";

        User user = giveMeADamnUser(uuid);


        if (user!=null){

            CachedMetaData metaData =  user.getCachedData().getMetaData();
            if (metaData!=null){
                prefix=metaData.getPrefix();
            }
        }
        if (prefix==null){
            prefix= ChatColor.GRAY+"";
        }
        return prefix;
    }

    public void setRank(Player player, String rank) {
        this.luckPerms.getUserManager().modifyUser(player.getUniqueId(), (User user) -> {

            // Remove all other inherited groups the user had before.
            user.data().clear(NodeType.INHERITANCE.predicate(inheritanceNode -> inheritanceNode.getGroupName().equalsIgnoreCase(rank)));

            // Create a node to add to the player.
            Node node = InheritanceNode.builder(rank).build();

            // Add the node to the user.
            user.data().add(node);

        });
    }
    public void removeRank(Player player, String rank) {
        this.luckPerms.getUserManager().modifyUser(player.getUniqueId(), (User user) -> {

            // Remove all other inherited groups the user had before.
            user.data().clear(NodeType.INHERITANCE.predicate(inheritanceNode ->{
                System.err.println( inheritanceNode.getGroupName());
              return   inheritanceNode.getGroupName().equalsIgnoreCase(rank);
            }));

        });
    }

    public void removePermission() {

    }

    public Rank getRank(Player player){
        PlayerAdapter<Player> playerAdapter = luckPerms.getPlayerAdapter(Player.class);
        if (playerAdapter!=null){

            CachedMetaData metaData = playerAdapter.getMetaData((Player) player);
            if (metaData!=null){
                return new Rank(metaData.getPrimaryGroup(), metaData.getPrefix(), metaData.getWeight());
            }
        }
        return null;
    }

    public static String getPrefixedName(UUID player) {
        if (player==null)return "NULL";
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
        if (offlinePlayer==null||!offlinePlayer.hasPlayedBefore())return null;
        LuckPermsDependency luckPermsDependency = get();
        if (luckPermsDependency==null)return offlinePlayer.getName();


        return luckPermsDependency.getPrefix(player)+offlinePlayer.getName();
    }

    public User giveMeADamnUser(UUID uniqueId) {
        UserManager userManager = luckPerms.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(uniqueId);

        return userFuture.join(); // ouch! (block until the User is loaded)
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


    public static boolean hasPermission(UUID uuid, String perm) {
        LuckPermsDependency luckPermsDependency = get();
        return luckPermsDependency.luckPerms.getUserManager().getUser(uuid).getCachedData().getPermissionData().checkPermission(perm).asBoolean();
    }

}
