package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.bukkit.entity.Player;

public class Settings {
    private boolean debug = false;

    public int getHomesLimit(Player player) {
        return Permission.MODERATOR.has(player) ? Constants.MODERATOR_HOMES_LIMIT : Constants.DEFAULT_HOMES_LIMIT;
    }

    public int getHomesLimitFor(User user) {
        return getHomesLimit(user.getPlayer());
    }
}
