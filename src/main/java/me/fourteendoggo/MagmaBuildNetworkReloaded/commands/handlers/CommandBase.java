package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.apache.commons.lang.Validate;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class CommandBase implements CommandExecutor, TabCompleter {

    protected final MBNPlugin plugin;
    private final String permission;

    public CommandBase(MBNPlugin plugin, String name, Permission permission, boolean tabComplete) {
        this(plugin, name, permission.toString(), tabComplete);
    }

    public CommandBase(MBNPlugin plugin, String name, String permission, boolean tabComplete) {
        PluginCommand cmd = plugin.getCommand(name);
        Validate.notNull(cmd, "Please register your command (" + name + ") into your plugin.yml!");
        this.plugin = plugin;
        this.permission = permission;
        cmd.setExecutor(this);
        if (tabComplete) {
            cmd.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Lang.NO_PERMISSION.get());
            return true;
        }
        CommandSource source = new CommandSource(sender);
        try {
            CommandResult result = execute(source, args);
            switch (result) {
                // already handled above but it can occur that a command requires extra privileges
                case NO_PERMISSION:
                    sender.sendMessage(Lang.NO_PERMISSION.get());
                    break;
                case PLAYER_ONLY:
                    sender.sendMessage(Lang.NO_CONSOLE.get());
                    break;
                case BAD_ARGS:
                case SHOW_USAGE:
                    sender.sendMessage(Utils.colorize(getUsage()));
                    break;
            }
        } catch (Exception e) {
            plugin.getLogger().severe("An error occurred whilst executing command " + cmd.getName() + ": ");
            e.printStackTrace();
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }

    @NotNull
    protected abstract CommandResult execute(CommandSource source, @NotNull String[] args);

    @NotNull
    protected abstract String getUsage();
}
