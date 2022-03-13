package me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
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
        CommandResult result = CommandResult.INTERNAL_ERROR;
        try {
            result = execute(new CommandSource(sender, plugin), args);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred whilst executing that command!");
            plugin.getLogger().log(Level.SEVERE, "An error occurred whilst executing command '" + cmd.getName() + "':", e);
        }
        switch (result) {
            case INTERNAL_ERROR -> sender.sendMessage(ChatColor.RED + "Something went wrong!");
            case NO_PERMISSION -> Lang.NO_PERMISSION.sendTo(sender);
            case PLAYER_ONLY -> Lang.NO_CONSOLE.sendTo(sender);
            case TARGET_NOT_FOUND -> Lang.PLAYER_NOT_FOUND.sendTo(sender);
            case SHOW_USAGE -> usage.sendTo(sender);
        }
        return true;
    }

    public void register(String cmdName, boolean asTabCompleter) {
        PluginCommand cmd = plugin.getCommand(cmdName);
        Validate.notNull(cmd, "Please register your '" + cmdName + "' command inside your plugin.yml file");
        cmd.setExecutor(this);
        if (asTabCompleter) {
            cmd.setTabCompleter(this::onTabComplete);
        }
    }

    protected static boolean handleException(Throwable throwable, User user, Lang errorMessage) {
        if (throwable != null) {
            errorMessage.sendTo(user);
            return true;
        }
        return false;
    }

    protected static List<String> tabComplete(String token, String previous, String[] shouldMatch, Collection<String> iterable) {
        if (getIndex(previous, shouldMatch) >= shouldMatch.length) return EMPTY_TAB_COMPLETE;
        return tabComplete(token, iterable);
    }

    protected static List<String> tabComplete(String token, String previous, String[] shouldMatch, String... iterable) {
        if (getIndex(previous, shouldMatch) >= shouldMatch.length) return EMPTY_TAB_COMPLETE;
        return iterable.length != 0 ? tabComplete(token, iterable) : null;
    }

    private static int getIndex(String find, String[] possibleMatches) {
        for (int i = 0; i < possibleMatches.length; i++) {
            if (find.equals(possibleMatches[i])) return i;
        }
        return possibleMatches.length + 100; // idk just something that is bigger than the length
    }

    protected static List<String> tabComplete(String token, String... iterable) {
        return tabComplete(token, Arrays.asList(iterable));
    }

    protected static List<String> tabComplete(String token, Iterable<String> iterable) {
        return StringUtil.copyPartialMatches(token, iterable, new ArrayList<>());
    }

    @Nullable
    protected List<String> onTabComplete(CommandSource source, String[] args) {
        return EMPTY_TAB_COMPLETE;
    }

    private List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return onTabComplete(new CommandSource(sender, plugin), args);
    }

    protected abstract CommandResult execute(CommandSource source, String[] args);
}
