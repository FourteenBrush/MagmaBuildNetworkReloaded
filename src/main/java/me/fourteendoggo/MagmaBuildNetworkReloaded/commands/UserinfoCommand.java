package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.jetbrains.annotations.NotNull;

public class UserinfoCommand extends CommandBase {

    public UserinfoCommand(MBNPlugin plugin) {
        super(plugin, Permission.MODERATOR);
    }

    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        return null;
    }

    @Override
    protected @NotNull String getUsage() {
        return null;
    }
}
