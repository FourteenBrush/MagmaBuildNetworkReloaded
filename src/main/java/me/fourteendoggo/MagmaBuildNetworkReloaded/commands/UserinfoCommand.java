package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UserinfoCommand extends CommandBase {

    public UserinfoCommand(MBNPlugin plugin) {
        super(plugin, Permission.MODERATOR);
    }

    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) return CommandResult.TARGET_NOT_FOUND;
            StringBuilder builder = new StringBuilder();
        }
        return CommandResult.SUCCESS;
    }

    @Override
    protected @NotNull String getUsage() {
        return null;
    }
}
