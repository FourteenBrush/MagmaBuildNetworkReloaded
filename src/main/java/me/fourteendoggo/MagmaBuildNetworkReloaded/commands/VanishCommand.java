package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.VanishManager;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VanishCommand extends CommandBase {
    private final VanishManager vanishManager;

    public VanishCommand(MBNPlugin plugin) {
        super(plugin, Permission.MODERATOR, Lang.VANISH_COMMAND_HELP);
        vanishManager = new VanishManager(plugin);
    }

    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        if (args.length == 0) {
            if (source.getPlayer() == null) return CommandResult.PLAYER_ONLY;
            vanishManager.toggleVanish(source.getPlayer(), true);
        } else if (args.length == 1) {
            switch (args[0]) {
                case "list" -> vanishManager.sendVanishedPlayerListTo(source.sender());
                case "enable" -> {
                    if (source.getPlayer() == null) return CommandResult.PLAYER_ONLY;
                    vanishManager.vanish(source.getPlayer(), true);
                }
                case "disable" -> {
                    if (source.getPlayer() == null) return CommandResult.PLAYER_ONLY;
                    vanishManager.unVanish(source.getPlayer(), true);
                }
                case "fakequit" -> {
                    if (source.getPlayer() == null) return CommandResult.PLAYER_ONLY;
                    vanishManager.doFakeQuit(source.getPlayer());
                }
                case "fakejoin" -> {
                    if (source.getPlayer() == null) return CommandResult.PLAYER_ONLY;
                    vanishManager.doFakeJoin(source.getPlayer());
                }
                default -> { return CommandResult.SHOW_USAGE; }
            }
        } else if (args.length == 2) {
            switch (args[0]) {
                case "enable" -> {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) return CommandResult.TARGET_NOT_FOUND;
                    if (source.sender() instanceof Player executor && executor.equals(target)) {
                        vanishManager.vanish(target, true);
                    } else {
                        vanishManager.vanishOther(target, true, source.sender());
                    }
                }
                case "disable" -> {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) return CommandResult.TARGET_NOT_FOUND;
                    if (source.sender() instanceof Player executor && executor.equals(target)) {
                        vanishManager.unVanish(target, true);
                    } else {
                        vanishManager.unVanishOther(target, true, source.sender());
                    }
                }
                case "toggle" -> {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) return CommandResult.TARGET_NOT_FOUND;
                    if (source.sender() instanceof Player executor && executor.equals(target)) {
                        vanishManager.toggleVanish(target, true);
                    } else {
                        vanishManager.toggleVanishFor(target, true, source.sender());
                    }
                }
                default -> { return CommandResult.SHOW_USAGE; }
            }
        } else {
            return CommandResult.SHOW_USAGE;
        }
        return CommandResult.SUCCESS;
    }

    @Override
    protected @Nullable List<String> onTabComplete(CommandSource source, String[] args) {
        return switch (args.length) {
            case 0 -> null;
            case 1 -> tabComplete(args[0], "list", "enable", "disable", "fakequit", "fakejoin", "toggle");
            case 2 -> tabComplete(args[1], args[0], new String[]{"enable", "disable", "toggle"});
            default -> super.onTabComplete(source, args);
        };
    }
}
