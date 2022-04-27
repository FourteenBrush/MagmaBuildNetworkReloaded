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
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class NewCommandBase implements CommandExecutor {
    private static final List<String> EMPTY_TAB_COMPLETE = Collections.emptyList();
    protected final MBNPlugin plugin;
    private final String permission;
    private final Lang usage;
    private final Map<String, SubCommand> subCommands;

    public NewCommandBase(MBNPlugin plugin, Permission permission, Lang usage) {
        this(plugin, permission.toString(), usage);
    }

    public NewCommandBase(MBNPlugin plugin, String permission, Lang usage) {
        this.plugin = plugin;
        this.permission = permission;
        this.usage = usage;
        subCommands = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(permission)) {
            Lang.NO_PERMISSION.sendTo(sender);
            return true;
        }
        CommandResult result = CommandResult.SHOW_USAGE;
        SubCommand subCommand = lookupSubcommand(args); // subcommands for aliases will also work
        try {
            if (subCommand == null) { // subcommand not found
                if (args.length == 0) {
                    result = executeRoot(new CommandSource(sender, plugin), args);
                }
            } else if (!(sender instanceof Player || subCommand.isAllowConsole())) {
                result = CommandResult.PLAYER_ONLY;
            } else if (subCommand.getArgsLength() == args.length) {
                result = subCommand.execute(new CommandSource(sender, plugin), args);
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred whilst executing that command! Please contact a staff member");
            plugin.getLogger().log(Level.SEVERE, "An exception occurred whilst executing command '" + cmd.getName() + "' for sender " + sender.getName(), e);
        }
        switch (result) {
            case INTERNAL_ERROR -> sender.sendMessage(ChatColor.RED + "Something went wrong, please contact staff!");
            case NO_PERMISSION -> Lang.NO_PERMISSION.sendTo(sender);
            case PLAYER_ONLY -> Lang.NO_CONSOLE.sendTo(sender);
            case TARGET_NOT_FOUND -> Lang.PLAYER_NOT_FOUND.sendTo(sender);
            case SHOW_USAGE -> usage.sendTo(sender);
        }
        return true;
    }

    private SubCommand lookupSubcommand(String[] args) {
        return subCommands.entrySet().stream().filter(entry -> entry.getValue().getArgsLength() == args.length
            && entry.getKey().equals(args[entry.getValue().getSubCommandAt()])).map(Map.Entry::getValue).findFirst().orElse(null);
    }

    public void register(String cmdName, boolean asTabCompleter) {
        PluginCommand cmd = plugin.getCommand(cmdName);
        Validate.notNull(cmd, "Please register your '" + cmdName + "' command inside your plugin.yml file");
        cmd.setExecutor(this);
        if (asTabCompleter) {
            cmd.setTabCompleter(this::onTabComplete);
        }
    }

    protected static void handleStorageResult(Throwable throwable, User user, Lang error, Lang success, String... successArgs) {
        if (throwable != null) {
            error.sendTo(user);
        } else {
            success.sendTo(user, successArgs);
        }
    }

    protected void addSubCommand(String name, int argsLength, int subCommandAt, Consumer<String[]> command) {
        addSubCommand(name, argsLength, subCommandAt, false, command);
    }

    protected void addSubCommand(String name, int argsLength, int subCommandAt, boolean allowConsole, Consumer<String[]> command) {
        subCommands.put(name, new SubCommand(argsLength, subCommandAt, allowConsole) {
            @Override
            public CommandResult execute(CommandSource source, String[] args) {
                command.accept(args);
                return CommandResult.SUCCESS;
            }
        });
    }

    protected void addSubCommand(String name, int argsLength, int subCommandAt, BiConsumer<User, String[]> command) {
        addSubCommand(name, argsLength, subCommandAt, false, command);
    }

    protected void addSubCommand(String name, int argsLength, int subCommandAt, boolean allowConsole, BiConsumer<User, String[]> command) {
        subCommands.put(name, new SubCommand(argsLength, subCommandAt, allowConsole) {
            @Override
            public CommandResult execute(CommandSource source, String[] args) {
                command.accept(source.getUser(), args);
                return CommandResult.SUCCESS;
            }
        });
    }

    protected void addSubCommand(String name, SubCommand subCommand) {

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
        return possibleMatches.length + 100; // just something that is bigger than the size
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

    private List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        return onTabComplete(new CommandSource(sender, plugin), args);
    }

    protected abstract CommandResult executeRoot(CommandSource source, String[] args);
}
