package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.NewCommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;

public class NewHomeCommand extends NewCommandBase {

    public NewHomeCommand(MBNPlugin plugin) {
        super(plugin, Permission.DEFAULT, null);
        addSubCommand("create", 2, false, args -> createHome(args[0]));
        addSubCommand("delete", 2, false, args -> deleteHome(args[0]));
        addSubCommand("teleport", 2, false, args -> teleportToHome(args[0]));
    }

    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        return null;
    }

    private void createHome(String name) {

    }

    private void deleteHome(String name) {

    }

    private void teleportToHome(String name) {

    }
}
