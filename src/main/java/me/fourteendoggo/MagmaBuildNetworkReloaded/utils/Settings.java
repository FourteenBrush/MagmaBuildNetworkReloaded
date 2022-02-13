package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import org.bukkit.entity.Player;

public class Settings {
    boolean debug = false;

    public int getHomesLimit(Player player) {
        return Permission.MODERATOR.has(player) ? Constants.MODERATOR_HOMES_LIMIT : Constants.DEFAULT_HOMES_LIMIT;
    }
}
