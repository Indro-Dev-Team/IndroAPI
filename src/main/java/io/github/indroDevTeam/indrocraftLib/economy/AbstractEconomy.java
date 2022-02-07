package io.github.indroDevTeam.indrocraftLib.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

@SuppressWarnings("unused")
public abstract class AbstractEconomy implements Economy {
    public boolean isBankOwner(String bankName, String playerName) {
        OfflinePlayer player = Bukkit.getPlayer(playerName);
        return isBankOwner(bankName, player);
    }

    public boolean isBankMember(String bankName, String playerName) {
        OfflinePlayer player = Bukkit.getPlayer(playerName);
        return isBankMember(bankName, player);
    }
}
