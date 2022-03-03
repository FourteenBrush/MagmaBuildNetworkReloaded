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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public abstract class NewCommandBase implements CommandExecutor {
    private final MBNPlugin plugin;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(permission)) {
            Lang.NO_PERMISSION.sendTo(sender);
            return true;
        }
        CommandResult result = CommandResult.FAILED;
        SubCommand subCommand = subCommands.get(cmd.getName());
        try {
            if (subCommand == null) {
                result = execute(new CommandSource(sender), args);
            } else if (!(sender instanceof Player || subCommand.isAllowConsole())) {
                result = CommandResult.PLAYER_ONLY;
            } else if (subCommand.getArgsLength() == args.length) {
                result = subCommand.execute(new CommandSource(sender), args);
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "An error occurred whilst executing that command! Please contact a staff member");
            plugin.getLogger().log(Level.SEVERE, "An exception occurred whilst executing command '" + cmd.getName() + "' for sender " + sender.getName(), e);
        }
        switch (result) {
            case FAILED -> sender.sendMessage(ChatColor.RED + "Something went wrong!");
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

    protected void addSubCommand(String name, int argsLength, boolean allowConsole, Consumer<String[]> command) {
        subCommands.put(name, new SubCommand(argsLength, allowConsole) {
            @Override
            public CommandResult execute(CommandSource source, String[] args) {
                command.accept(args);
                return CommandResult.SUCCESS;
            }
        });
    }

    protected void addSubCommand(String name, int argsLength, boolean allowConsole, BiConsumer<User, String[]> command) {
        subCommands.put(name, new SubCommand(argsLength, allowConsole) {
            @Override
            public CommandResult execute(CommandSource source, String[] args) {
                command.accept(source.getUser(), args);
                return CommandResult.SUCCESS;
            }
        });
    }

    private List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return null;
    }

    protected abstract CommandResult execute(CommandSource source, String[] args);
}
