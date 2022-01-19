package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import org.bukkit.entity.Player;

public enum Permission {

    ADMIN("magmabuildnetwork.permissions.admin"),
    MODERATOR("magmabuildnetwork.permissions.moderator"),
    SEE_VANISHED("magmabuildnetwork.permissions.see-vanished");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public boolean has(Player player) {
        return player.hasPermission(permission);
    }

    public boolean hasOr(Player player, String permission) {
        if (has(player)) return true;
        return player.hasPermission(permission);
    }
}
