package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.managers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.IConsoleCommand;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CommandHandler implements CommandExecutor, TabCompleter {
    protected final MBNPlugin plugin;
    private final String permission;
    protected CommandSender sender;
    protected Player executor;

    @SuppressWarnings("all")
    public CommandHandler(MBNPlugin plugin, String cmd, String permission, boolean tabComplete) {
        this.plugin = plugin;
        this.permission = permission;
        if (tabComplete) {
            Bukkit.getPluginCommand(cmd).setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!isAuthorized()) return true;
        try {
            return execute(args);
        } catch (Exception e) {
            plugin.getLogger().severe("An error occurred whilst executing command " + cmd.getName());
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return onTabComplete();
    }

    private boolean isAuthorized() {
        if (sender instanceof BlockCommandSender) return false;
        if (!(this instanceof IConsoleCommand || sender instanceof Player)) {
            sender.sendMessage(Lang.NO_CONSOLE.get());
            return false;
        }
        if (sender instanceof Player) {
            executor = (Player) sender;
            return executor.hasPermission(permission);
        }
        return true;
    }

    protected List<String> onTabComplete() {
        return null;
    }

    protected abstract boolean execute(@NotNull String[] args);
}
