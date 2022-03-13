package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.UserSnapshot;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UserinfoCommand extends CommandBase {

    public UserinfoCommand(MBNPlugin plugin) {
        super(plugin, Permission.MODERATOR, null);
    } // TODO

    @SuppressWarnings("deprecation")
    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        if (args.length > 0) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target != null) {
                handleResult(plugin.getCache().getUser(target).getData(), target, source.sender());
            } else {
                CommandResult[] result = new CommandResult[1];
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if (!offlinePlayer.hasPlayedBefore()) return CommandResult.TARGET_NOT_FOUND;
                plugin.getStorage().loadUser(offlinePlayer.getUniqueId()).thenAccept(s -> {
                    if (s != null) {
                        handleResult(s, null, source.sender());
                    } else {
                        result[0] = CommandResult.TARGET_NOT_FOUND;
                    }
                });
                if (result[0] == CommandResult.TARGET_NOT_FOUND) return CommandResult.TARGET_NOT_FOUND;
            }
        }
        return CommandResult.SUCCESS;
    }

    private void handleResult(UserSnapshot snapshot, @Nullable Player viewStatsFor, CommandSender sendTo) {
        StringBuilder builder = new StringBuilder();
        String homesLimit;
        if (viewStatsFor == null) {
            homesLimit = "?";
        } else {
            homesLimit = String.valueOf(Permission.MODERATOR.has(viewStatsFor) ? NewHomeCommand.MODERATOR_HOMES_LIMIT : NewHomeCommand.DEFAULT_HOMES_LIMIT);
        }
        String output = builder.append("&e------------ &7[&eUser Info&7] &e------------\n")
                .append("&6UUID: &7").append(snapshot.getId())
                .append("\n&6Playtime: &7").append(snapshot.getStatisticsProfile().getMinutesPlayed())
                .append("\n&6Homes: &7").append("[").append(snapshot.getHomesAmount()).append("/")
                    .append(homesLimit).append("]")
                .append("\n&6Level: &7").append(snapshot.getStatisticsProfile().getLevel())
                .append("\n&6Kingdom: &7").append(snapshot.getMembershipProfile().getKingdom().getName())
                .toString();
        sendTo.sendMessage(Utils.colorize(output));
    }

    @Override
    protected @Nullable List<String> onTabComplete(CommandSource source, String[] args) {
        return null;
    }
}
