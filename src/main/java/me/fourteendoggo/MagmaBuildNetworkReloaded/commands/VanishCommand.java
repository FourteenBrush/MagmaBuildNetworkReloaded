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
import org.jetbrains.annotations.NotNull;

public class VanishCommand extends CommandBase {
    private final VanishManager vanishManager;

    public VanishCommand(MBNPlugin plugin) {
        super(plugin, "vanish", Permission.MODERATOR, true);
        vanishManager = new VanishManager(plugin);
    }

    @Override
    protected CommandResult execute(CommandSource source, @NotNull String[] args) {
        if (args.length == 0) { // vanish
            if (source.getPlayer().isPresent()) {
                vanishManager.toggleVanish(source.getPlayer().get(), true);
            } else return CommandResult.SHOW_USAGE;
        } else if (args.length == 1) { // vanish enable
            switch (args[0]) {
                case "list":
                    vanishManager.sendVanishedPlayerListTo(source.getSender());
                    break;
                case "enable":
                    if (!source.getPlayer().isPresent()) return CommandResult.PLAYER_ONLY;
                    vanishManager.vanish(source.getPlayer().get(), true);
                    break;
                case "disable":
                    if (!source.getPlayer().isPresent()) return CommandResult.PLAYER_ONLY;
                    vanishManager.unVanish(source.getPlayer().get(), true);
                    break;
                case "fakequit":
                    if (!source.getPlayer().isPresent()) return CommandResult.PLAYER_ONLY;
                    vanishManager.doFakeQuit(source.getPlayer().get());
                    break;
                case "fakejoin":
                    if (!source.getPlayer().isPresent()) return CommandResult.PLAYER_ONLY;
                    vanishManager.doFakeJoin(source.getPlayer().get());
                    break;
                default:
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target == null) return CommandResult.TARGET_NOT_FOUND;
                    vanishManager.toggleVanishFor(target, true, source.getSender());
                    break;
            }
        } else if (args.length == 2) { // vanish enable FourteenDoggo
            switch (args[0]) {
                case "enable":
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) return CommandResult.TARGET_NOT_FOUND;
                    vanishManager.vanishOther(target, true, source.getSender());
                    break;
                case "disable":
                    Player target1 = Bukkit.getPlayer(args[1]);
                    if (target1 == null) return CommandResult.TARGET_NOT_FOUND;
                    vanishManager.unVanishOther(target1, true, source.getSender());
                    break;
                default: return CommandResult.SHOW_USAGE;
            }
        }
        return CommandResult.SUCCESS;
    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.VANISH_COMMAND_HELP.get();
    }
}
