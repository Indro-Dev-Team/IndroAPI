package io.github.indroDevTeam.indrocraftLib.economy;

import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public interface Economy {
    boolean createAccount(Player player);
    void setBank(Player player, int amount);
    void setWallet(Player player, int amount);
    Integer getWallet(Player player);
    Integer getBank(Player player);
    boolean sendMoney(Player sender, Player receiver, int amount);
    boolean transferMoney(Player source, String type, int amount);
    boolean hasAccount(Player player);
    void addWallet(Player player, int amount);
    void minusWallet(Player player, int amount);
}
