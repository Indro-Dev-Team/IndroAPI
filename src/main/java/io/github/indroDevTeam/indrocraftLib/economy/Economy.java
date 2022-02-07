package io.github.indroDevTeam.indrocraftLib.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

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


    void createBank(String bankName, OfflinePlayer bankOwner);


    void deleteBank(String bankName);


    Integer bankBalance(String bankName);

    boolean bankHas(String bankName, int value);

    int bankWithdraw(String bankName, int value);

    int bankDeposit(String bankName, int value);

    boolean isBankOwner(String bankName, String playerName);

    boolean isBankOwner(String bankName, OfflinePlayer bankOwner);

    boolean isBankMember(String bankName, String playerName);

    boolean isBankMember(String bankName, OfflinePlayer bankMember);

    List<String> getBanks();
}
