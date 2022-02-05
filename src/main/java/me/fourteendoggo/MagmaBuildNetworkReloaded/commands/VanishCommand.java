package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.VanishManager;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.jetbrains.annotations.NotNull;

public class VanishCommand extends CommandBase {
    private final VanishManager vanishManager;

    public VanishCommand(MBNPlugin plugin) {
        super(plugin, "vanish", Permission.MODERATOR, true);
        vanishManager = new VanishManager(plugin);
    }

    @Override
    protected @NotNull CommandResult execute(CommandSource source, @NotNull String[] args) {
        if (args.length == 0) {
            if (source.getPlayer().isPresent()) {
                vanishManager.vanish(source.getPlayer().get());
            } else {
                return CommandResult.PLAYER_ONLY;
            }
        } else if (args.length == 1) {
            switch (args[0]) {
                case "list":
                    vanishManager.sendVanishedPlayerListTo(source.getSender());
                    break;
                case "fakequit":
                    vanishManager
            }
        }
        return CommandResult.SUCCESS;
    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.VANISH_COMMAND_HELP.get();
    }
}
