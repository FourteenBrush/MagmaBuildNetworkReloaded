package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeCommand extends CommandBase {

    public HomeCommand(MBNPlugin plugin) {
        super(plugin, "home", Permission.DEFAULT, true);
    }

    @Override
    protected @NotNull CommandResult execute(CommandSource source, @NotNull String[] args) {
        if (!source.getPlayer().isPresent()) return CommandResult.PLAYER_ONLY;
        if (args.length == 2) {
            switch (args[0]) {
                case "create":
                    int homesLimit = Permission.MODERATOR.has(source.getPlayer().get()) ? 5 : 2;
                    createHome(args[1], source.getPlayer().get(), homesLimit);
                    break;
                case "remove":
                case "delete":
                    deleteHome(args[1], source.getPlayer().get());
                    break;
                case "teleport":
                case "tp":
                    teleportToHome(args[1], source.getPlayer().get());
                    break;
                default:
                    return CommandResult.SHOW_USAGE;
            }
        } else if (args.length == 1 && args[0].equals("list")) {
            sendAllHomes(source.getPlayer().get());
        } else {
            return CommandResult.SHOW_USAGE;
        }
        return CommandResult.SUCCESS;
    }

    private void createHome(String name, Player from, int homesLimit) {

    }

    private void deleteHome(String name, Player from) {

    }

    private void teleportToHome(String name, Player from) {

    }

    private void sendAllHomes(Player from) {

    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.HOME_COMMAND_HELP.get();
    }
}
