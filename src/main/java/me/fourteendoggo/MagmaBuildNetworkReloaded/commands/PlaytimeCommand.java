package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.UserRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class PlaytimeCommand extends CommandBase {
    private final UserRepository userRepository;
    private final DateTimeFormatter dateTimeFormatter;

    public PlaytimeCommand(MBNPlugin plugin, UserRepository userRepository) {
        super(plugin, "playtime", Permission.DEFAULT, false);
        this.userRepository = userRepository;
        dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    @Override
    protected CommandResult execute(CommandSource source, @NotNull String[] args) {
        if (args.length == 0) {
            if (!source.getPlayer().isPresent()) return CommandResult.PLAYER_ONLY;
            Player player = source.getPlayer().get();
            Optional<User> user = userRepository.get(player.getUniqueId());
            if (!user.isPresent()) return CommandResult.FAILED; // should never happen
            long firstJoin = user.get().getStatisticsProfile().getFirstJoin();
            LocalDateTime dateTime = LocalDateTime.from(Instant.ofEpochMilli(firstJoin));
            source.sendMessage(Lang.PLAYTIME.get(getPlaytime(player), dateTime.format(dateTimeFormatter)));
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) { // try to find offline player
                CraftServer
            }
            target.sendMessage(Lang.PLAYTIME_OTHER.get(getPlaytime(target)));
        }
        return CommandResult.SUCCESS;
    }

    private String getPlaytime(Player player) {
        return Utils.minutesToReadable(player.getStatistic(Statistic.PLAY_ONE_MINUTE));
    }

    private String calculatePlaytime(Player player) {

    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.PLAYTIME_COMMAND_HELP.get();
    }
}
