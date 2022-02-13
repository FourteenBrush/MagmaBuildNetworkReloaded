package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.UserRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.NewUser;
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
import java.util.Optional;

public class PlaytimeCommand extends CommandBase {
    private final UserRepository userRepository;
    private final DateTimeFormatter dateTimeFormatter;

    public PlaytimeCommand(MBNPlugin plugin) {
        super(plugin, Permission.DEFAULT);
        userRepository = plugin.getBaseRepository().getUserRepository();
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    @Override
    protected CommandResult execute(CommandSource source, @NotNull String[] args) {
        if (args.length == 0) {
            if (source.getPlayer().isEmpty()) return CommandResult.PLAYER_ONLY;
            Player player = source.getPlayer().get();
            Optional<NewUser> user = userRepository.get(player.getUniqueId());
            assert user.isPresent(); // user should be cached when entering server
            sendPlaytime(user.get(), Lang.PLAYTIME);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) return CommandResult.TARGET_NOT_FOUND;
            Optional<NewUser> user = userRepository.get(target.getUniqueId());
            if (user.isEmpty()) return CommandResult.TARGET_NOT_FOUND;
            sendPlaytime(user.get(), Lang.PLAYTIME_OTHER);
        }
        return CommandResult.SUCCESS;
    }

    private String formatPlaytime(Player player) {
        return Utils.minutesToReadable(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
    }

    private void sendPlaytime(NewUser user, Lang message) {
        long firstJoin = user.getSnapshot().statisticsProfile().getFirstJoin();
        LocalDateTime dateTime = LocalDateTime.from(Instant.ofEpochMilli(firstJoin));
        Player player = user.getPlayer();
        player.sendMessage(message.get(formatPlaytime(player), dateTime.format(dateTimeFormatter)));
    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.PLAYTIME_COMMAND_HELP.get();
    }
}
