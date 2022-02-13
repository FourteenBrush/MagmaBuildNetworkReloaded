package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CommandBase implements CommandExecutor {
    protected final MBNPlugin plugin;
    private final String permission;

    public CommandBase(MBNPlugin plugin, Permission permission) {
        this(plugin, permission.toString());
    }

    public CommandBase(MBNPlugin plugin, String permission) {
        this.plugin = plugin;
        this.permission = permission;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Lang.NO_PERMISSION.get());
            return true;
        }
        try {
            CommandResult result = execute(new CommandSource(sender), args);
            switch (result) {
                // already handled above, but it can occur that a command requires extra privileges
                case NO_PERMISSION -> sender.sendMessage(Lang.NO_PERMISSION.get());
                case PLAYER_ONLY -> sender.sendMessage(Lang.NO_CONSOLE.get());
                // todo send message
                case BAD_ARGS, SHOW_USAGE -> sender.sendMessage(Utils.colorize(getUsage()));
                case TARGET_NOT_FOUND -> sender.sendMessage(Lang.PLAYER_NOT_FOUND.get());
            }
        } catch (Exception e) {
            plugin.getLogger().severe("An error occurred whilst executing command " + cmd.getName() + ":");
            e.printStackTrace();
        }
        return true;
    }

    public void register(String cmdName, boolean asTabCompleter) {
        PluginCommand cmd = plugin.getCommand(cmdName);
        Validate.notNull(cmd, "Please register your command into your plugin.yml (" + cmdName + ")");
        cmd.setExecutor(this);
        if (asTabCompleter) {
            cmd.setTabCompleter(this::onTabComplete);
        }
    }

    @Nullable
    private List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return onTabComplete(sender, args);
    }

    protected static List<String> tabcomplete(String token, Iterable<String> iterable) {
        return StringUtil.copyPartialMatches(token, iterable, new ArrayList<>());
    }

    protected abstract CommandResult execute(CommandSource source, String[] args);

    @NotNull
    protected abstract String getUsage();

    @Nullable
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
