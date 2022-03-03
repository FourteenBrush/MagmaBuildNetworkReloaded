package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

public enum Permission {

    ADMIN("magmabuildnetwork.permissions.admin"),
    MODERATOR("magmabuildnetwork.permissions.moderator"),
    SEE_VANISHED("magmabuildnetwork.permissions.see-vanished"),
    DEFAULT("magmabuildnetwork.permissions.default");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public boolean has(Permissible permissible) {
        return permissible.hasPermission(permission);
    }

    public boolean has(User user) {
        return has(user.getPlayer());
    }

    public boolean hasOr(Player player, String permission) {
        if (has(player)) return true;
        return player.hasPermission(permission);
    }

    @Override
    public String toString() {
        return permission;
    }
}
