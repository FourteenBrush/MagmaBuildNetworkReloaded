package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.bukkit.permissions.Permissible;

public class Settings {

    public int getHomesLimitFor(Permissible permissible) {
        return Permission.MODERATOR.has(permissible) ? Constants.MODERATOR_HOMES_LIMIT : Constants.DEFAULT_HOMES_LIMIT;
    }

    public int getHomesLimitFor(User user) {
        return getHomesLimitFor(user.getPlayer());
    }
}
