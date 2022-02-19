package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public record CommandSource(CommandSender sender) {

    public Optional<Player> getPlayer() {
        if (sender instanceof Player player) {
            return Optional.of(player);
        }
        return Optional.empty();
    }

    public void sendMessage(String message) {
        sender.sendMessage(message);
    }
}
