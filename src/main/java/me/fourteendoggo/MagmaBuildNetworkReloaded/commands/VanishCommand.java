package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.managers.CommandHandler;
import org.jetbrains.annotations.NotNull;

public class VanishCommand extends CommandHandler {

    public VanishCommand(MBNPlugin plugin) {
        super(plugin, "vanish", "vanish");
    }

    @Override
    protected boolean execute(@NotNull String[] args) {
        return false;
    }
}
