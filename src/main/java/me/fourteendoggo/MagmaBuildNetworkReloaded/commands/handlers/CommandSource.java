package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record CommandSource(CommandSender sender, MBNPlugin plugin) {

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Nullable
    public Player getPlayer() {
        return sender instanceof Player player ? player : null;
    }

    public User getUser() {
        return sender instanceof Player player ? plugin.getCache().getUser(player) : null;
    }
}
