package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

public abstract class CommandBase implements CommandExecutor {
    private static final List<String> EMPTY_TAB_COMPLETE = Collections.emptyList();
    protected final MBNPlugin plugin;
    private final String permission;
    private final Lang usage;

    public CommandBase(MBNPlugin plugin, Permission permission, Lang usage) {
        this(plugin, permission.toString(), usage);
    }

    public CommandBase(MBNPlugin plugin, String permission, Lang usage) {
        this.plugin = plugin;
        this.permission = permission;
        this.usage = usage;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(permission)) {
            Lang.NO_PERMISSION.sendTo(sender);
            return true;
        }
        CommandResult result = CommandResult.FAILED;
        try {
            result = execute(new CommandSource(sender), args);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred whilst executing that command!");
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst executing command '" + cmd.getName() + "':", e);
        }
        switch (result) {
            // already handled above, but it can occur that a command requires extra privileges
            case NO_PERMISSION -> Lang.NO_PERMISSION.sendTo(sender);
            case PLAYER_ONLY -> Lang.NO_CONSOLE.sendTo(sender);
            case TARGET_NOT_FOUND -> Lang.PLAYER_NOT_FOUND.sendTo(sender);
            case FAILED -> sender.sendMessage(ChatColor.RED + "Something went wrong!");
            case SHOW_USAGE -> usage.sendTo(sender);
        }
        return true;
    }

    private List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return onTabComplete(new CommandSource(sender), args);
    }

    public void register(String cmdName, boolean asTabCompleter) {
        PluginCommand cmd = plugin.getCommand(cmdName);
        Validate.notNull(cmd, "Please register your '" + cmdName + "' command inside your plugin.yml file");
        cmd.setExecutor(this);
        if (asTabCompleter) {
            cmd.setTabCompleter(this::onTabComplete);
        }
    }

    protected static List<String> tabComplete(String token, String... iterable) {
        return tabComplete(token, Arrays.asList(iterable));
    }

    protected static List<String> tabComplete(String token, Iterable<String> iterable) {
        return StringUtil.copyPartialMatches(token, iterable, new ArrayList<>());
    }

    protected static List<String> tabComplete(String token, String previous, String[] shouldMatch, Collection<String> iteratable) {
        int index = 1;
        String str = shouldMatch[0];
        while (!(previous.equals(str))) {
            str = shouldMatch[index++];
        }
        if (index >= shouldMatch.length) return EMPTY_TAB_COMPLETE;
        return tabComplete(token, iteratable);
    }

    protected static List<String> tabComplete(String token, String previous, String[] shouldMatch, String... iterable) {
        int index = 1;
        String str = shouldMatch[0];
        while (!(previous.equals(str))) {
            str = shouldMatch[index++];
        }
        if (index >= shouldMatch.length) return EMPTY_TAB_COMPLETE;
        return iterable.length == 0 ? null : tabComplete(token, iterable);
    }

    @Nullable
    protected List<String> onTabComplete(CommandSource source, String[] args) {
        return EMPTY_TAB_COMPLETE;
    }

    protected abstract CommandResult execute(CommandSource source, String[] args);
}
