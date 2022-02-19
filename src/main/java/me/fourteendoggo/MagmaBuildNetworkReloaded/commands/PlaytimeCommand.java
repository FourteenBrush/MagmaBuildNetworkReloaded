package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlaytimeCommand extends CommandBase {
    private final DateTimeFormatter dateTimeFormatter;

    public PlaytimeCommand(MBNPlugin plugin) {
        super(plugin, Permission.DEFAULT);
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    @Override
    protected CommandResult execute(CommandSource source, @NotNull String[] args) {
        if (args.length == 0) {
            if (source.getPlayer().isEmpty()) return CommandResult.PLAYER_ONLY;
            User user = plugin.getData().getUser(source.getPlayer().get());
            sendPlaytime(user, Lang.PLAYTIME);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) return CommandResult.TARGET_NOT_FOUND;
            User user = plugin.getData().getUser(target);
            if (user == null) return CommandResult.TARGET_NOT_FOUND;
            // TODO check database
            sendPlaytime(user, Lang.PLAYTIME_OTHER);
        }
        return CommandResult.SUCCESS;
    }

    private String formatPlaytime(Player player) {
        return Utils.minutesToReadable(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
    }

    private void sendPlaytime(User user, Lang message) {
        long firstJoin = user.getData().statisticsProfile().getFirstJoin();
        LocalDateTime dateTime = LocalDateTime.from(Instant.ofEpochMilli(firstJoin));
        Player player = user.getPlayer();
        player.sendMessage(message.get(formatPlaytime(player), dateTime.format(dateTimeFormatter)));
    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.PLAYTIME_COMMAND_HELP.get();
    }
}
