package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record CommandSource(CommandSender sender, MBNPlugin plugin) {

    public Player getPlayer() {
        return sender instanceof Player player ? player : null;
    }

    public User getUser() {
        return sender instanceof Player player ? plugin.getCache().getUser(player) : null;
    }
}
