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
import org.bukkit.permissions.Permissible;
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
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                handleResult(plugin.getData().getUser(target).getData(), target, source.sender());
            } else {
                CommandResult[] result = new CommandResult[1];
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                plugin.getStorage().loadUser(offlinePlayer.getUniqueId()).whenComplete((s, t) -> {
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

    private void handleResult(UserSnapshot snapshot, @Nullable Permissible viewStatsFor, CommandSender sendTo) {
        StringBuilder builder = new StringBuilder();
        String homesLimit = viewStatsFor != null ? String.valueOf(plugin.getSettings().getHomesLimitFor(viewStatsFor)) : "?";
        String output = builder.append("&e------------ &7[&eUser Info&7] &e------------\n")
                .append("&6UUID: ").append("&7").append(snapshot.getStatisticsProfile().getId())
                .append("\n&6Playtime: ").append("&7").append(snapshot.getStatisticsProfile().getMinutesPlayed())
                .append("\n&6Homes: ").append("&7").append("[").append(snapshot.getHomesAmount()).append("/")
                    .append(homesLimit).append("]")
                .append("\n&6Level:&7 ").append(snapshot.getStatisticsProfile().getLevel())
                .toString();
        sendTo.sendMessage(Utils.colorize(output));
    }

    @Override
    protected @Nullable List<String> onTabComplete(CommandSource source, String[] args) {
        return null;
    }
}
