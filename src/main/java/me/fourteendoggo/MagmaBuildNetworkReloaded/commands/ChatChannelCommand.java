package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ChatChannelCommand extends CommandBase {

    public ChatChannelCommand(MBNPlugin plugin) {
        super(plugin, Permission.DEFAULT, null);
    }

    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        if (source.getPlayer() == null) return CommandResult.PLAYER_ONLY;
        if (args.length == 1 && args[0].equals("list")) {
            sendChannels(plugin.getCache().getUser(source.getPlayer()));
        } else if (args.length == 2) {
            switch (args[0]) {
                case "join" -> joinChannel(args[1], plugin.getCache().getUser(source.getPlayer()));
                case "leave" -> leaveChannel(args[1], plugin.getCache().getUser(source.getPlayer()));
            }
        }
        return CommandResult.SUCCESS;
    }

    private void joinChannel(String name, User user) {
        getChannel(name, user).ifPresent(channel -> channel.join(user, true));
    }

    private void leaveChannel(String name, User user) {
        getChannel(name, user).ifPresent(channel -> channel.leave(user, true));
    }

    private void sendChannels(User user) {
        StringBuilder builder = new StringBuilder();
    }

    private Optional<ChatChannel> getChannel(String name, User user) {
        ChatChannel channel = plugin.getCache().getChatChannel(name);
        if (channel == null) {
            Lang.CHANNEL_NOT_FOUND.sendTo(user);
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    @Override
    protected @Nullable List<String> onTabComplete(CommandSource source, String[] args) {
        return super.onTabComplete(source, args);
    }
}
