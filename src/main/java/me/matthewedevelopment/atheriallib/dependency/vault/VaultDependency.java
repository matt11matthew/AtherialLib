// 
// Decompiled by Procyon v0.5.36
// 

package me.matthewedevelopment.atheriallib.dependency.vault;

import me.matthewedevelopment.atheriallib.AtherialLib;
import me.matthewedevelopment.atheriallib.dependency.Dependency;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultDependency extends Dependency
{
    private Economy economy;
    private Permission permission;

    public VaultDependency(AtherialLib plugin) {
        super("VaultDependency", plugin);
    }


    @Override
    public void onEnable() {
    }
    
    @Override
    public void onPreEnable() {
    }
    
    public boolean init() {
        if (!this.setupEconomy()) {
            plugin.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", plugin.getClass().getSimpleName()));
            return false;
        }
        if (!this.setupPermission()) {
            plugin.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", plugin.getClass().getSimpleName()));
            return false;
        }
        return true;
    }

    public boolean safeWithdraw(OfflinePlayer player, double cost) {
        EconomyResponse withdraw = withdraw(player, cost);
        return withdraw!=null&&withdraw.type== EconomyResponse.ResponseType.SUCCESS;

    }
    public boolean hasPermission(String world, OfflinePlayer player, String perm) {

       return permission.playerHas(world, player,perm);
    }
    public String getPrimaryGroup(final Player player) {
        return this.permission.getPrimaryGroup(player);
    }
    
    public EconomyResponse setBalance(final OfflinePlayer offlinePlayer, final double balance) {
        this.checkAccount(offlinePlayer);
        this.economy.withdrawPlayer(offlinePlayer, this.getBalance(offlinePlayer));
        return this.economy.depositPlayer(offlinePlayer, balance);
    }
    public boolean safeDeposit(final OfflinePlayer offlinePlayer, final double amount) {
        this.checkAccount(offlinePlayer);
        EconomyResponse economyResponse = this.economy.depositPlayer(offlinePlayer, amount);
        if (economyResponse!=null&&economyResponse.type==EconomyResponse.ResponseType.SUCCESS){
            return true;
        }
        return false;
    }
    public EconomyResponse deposit(final OfflinePlayer offlinePlayer, final double amount) {
        this.checkAccount(offlinePlayer);
        return this.economy.depositPlayer(offlinePlayer, amount);
    }
    
    public EconomyResponse withdraw(final OfflinePlayer offlinePlayer, final double amount) {
        this.checkAccount(offlinePlayer);
        return this.economy.withdrawPlayer(offlinePlayer, amount);
    }
    
    public boolean hasEnoughMoney(final OfflinePlayer offlinePlayer, final double amount) {
        return this.getBalance(offlinePlayer) >= amount;
    }
    
    public double getBalance(final OfflinePlayer offlinePlayer) {
        this.checkAccount(offlinePlayer);
        return this.economy.getBalance(offlinePlayer);
    }
    
    private void checkAccount(final OfflinePlayer offlinePlayer) {
    }
    
    public Economy getEconomy() {
        return this.economy;
    }
    
    @Override
    public void onDisable() {
        this.plugin.getLogger().info(String.format("[%s] Disabled Version %s", "VaultDependency", "1.0"));
    }
    
    private boolean setupEconomy() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>)Bukkit.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = (Economy)rsp.getProvider();
        return this.economy != null;
    }
    
    private boolean setupPermission() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            return false;
        }
        final RegisteredServiceProvider<Permission> rsp = (RegisteredServiceProvider<Permission>)Bukkit.getServer().getServicesManager().getRegistration((Class)Permission.class);
        if (rsp == null) {
            return false;
        }
        this.permission = (Permission)rsp.getProvider();
        System.err.println("Permissions setup " + permission.toString());
        return this.permission != null;
    }
}
