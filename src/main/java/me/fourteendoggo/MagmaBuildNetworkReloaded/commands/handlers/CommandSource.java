package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandSource {
    private final CommandSender sender;

    public CommandSource(CommandSender sender) {
        this.sender = sender;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Optional<Player> getPlayer() {
        if (sender instanceof Player) {
            return Optional.of((Player) sender);
        }
        return Optional.empty();
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public void sendMessage(String message) {
        sender.sendMessage(Utils.colorize(message));
    }
}
