package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.VanishManager;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VanishCommand extends CommandBase {
    private final VanishManager vanishManager;

    public VanishCommand(MBNPlugin plugin) {
        super(plugin, Permission.MODERATOR);
        vanishManager = new VanishManager(plugin);
    }

    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        if (args.length == 0) { // vanish
            if (source.getPlayer().isEmpty()) return CommandResult.PLAYER_ONLY;
            vanishManager.toggleVanish(source.getPlayer().get(), true);
        } else if (args.length == 1) { // vanish enable
            switch (args[0]) {
                case "list" -> vanishManager.sendVanishedPlayerListTo(source.sender());
                case "enable" -> {
                    if (source.getPlayer().isEmpty()) return CommandResult.PLAYER_ONLY;
                    vanishManager.vanish(source.getPlayer().get(), true);
                }
                case "disable" -> {
                    if (source.getPlayer().isEmpty()) return CommandResult.PLAYER_ONLY;
                    vanishManager.unVanish(source.getPlayer().get(), true);
                }
                case "fakequit" -> {
                    if (source.getPlayer().isEmpty()) return CommandResult.PLAYER_ONLY;
                    vanishManager.doFakeQuit(source.getPlayer().get());
                }
                case "fakejoin" -> {
                    if (source.getPlayer().isEmpty()) return CommandResult.PLAYER_ONLY;
                    vanishManager.doFakeJoin(source.getPlayer().get());
                }
                default -> { // TODO toggle player instead of player
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) return CommandResult.TARGET_NOT_FOUND;
                    vanishManager.toggleVanishFor(target, true, source.sender());
                }
            }
        } else if (args.length == 2) { // vanish enable FourteenDoggo
            switch (args[0]) {
                case "enable" -> {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) return CommandResult.TARGET_NOT_FOUND;
                    vanishManager.vanishOther(target, true, source.sender());
                }
                case "disable" -> {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) return CommandResult.TARGET_NOT_FOUND;
                    vanishManager.unVanishOther(target, true, source.sender());
                }
                default -> {
                    return CommandResult.SHOW_USAGE;
                }
            }
        } else {
            return CommandResult.SHOW_USAGE;
        }
        return CommandResult.SUCCESS;
    }

    @Override
    protected @Nullable List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return null;
        }
        if (args.length == 1) {

        }
        // TODO
        return super.onTabComplete(sender, args);
    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.VANISH_COMMAND_HELP.get();
    }
}
