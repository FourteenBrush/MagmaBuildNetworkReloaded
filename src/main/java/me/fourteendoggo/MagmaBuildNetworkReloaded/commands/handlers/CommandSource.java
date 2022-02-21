package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record CommandSource(CommandSender sender) {

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Nullable
    public Player getPlayer() {
        return sender instanceof Player player ? player : null;
    }
}
