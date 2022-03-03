package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

public class HomeCommand extends CommandBase {
    private static final int DEFAULT_HOMES_LIMIT = 2;
    private static final int MODERATOR_HOMES_LIMIT = 5;

    public HomeCommand(MBNPlugin plugin) {
        super(plugin, Permission.DEFAULT, Lang.HOME_COMMAND_HELP);
    }

    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        if (source.getPlayer() == null) return CommandResult.PLAYER_ONLY;
        User user = plugin.getCache().getUser(source.getPlayer().getUniqueId());
        if (args.length == 2) {
            switch (args[0]) {
                case "create" -> createHome(args[1], user);
                case "remove", "delete" -> deleteHome(args[1], user);
                case "teleport", "tp" -> teleportToHome(args[1], user);
                default -> { return CommandResult.SHOW_USAGE; }
            }
        } else if (args.length == 1 && args[0].equals("list")) {
            sendAllHomes(user);
        } else {
            return CommandResult.SHOW_USAGE;
        }
        return CommandResult.SUCCESS;
    }

    private void createHome(String name, User user) {
        int homesLimit = Permission.MODERATOR.has(user) ? MODERATOR_HOMES_LIMIT : DEFAULT_HOMES_LIMIT;
        if (user.getData().getHomesAmount() >= homesLimit) {
            Lang.HOME_LIMIT_REACHED.sendTo(user);
        } else if (user.getData().getHome(name) != null) {
            Lang.HOME_CANNOT_HAVE_DUPLICATES.sendTo(user);
        } else {
            Home home = new Home(name, user.getId(), user.getPlayer().getLocation());
            user.getData().addHome(home);
            plugin.getStorage().createHome(home).whenComplete((v, t) ->
                    Lang.HOME_CREATED_NEW.sendTo(user, name))
                    .exceptionally(handleException(user, Lang.ERROR_CREATING_HOME));
        }
    }

    private void deleteHome(String name, User user) {
        Home home = user.getData().getHome(name);
        if (home != null) {
            user.getData().removeHome(home);
            plugin.getStorage().deleteHome(home).whenComplete((v, t) ->
                    Lang.HOME_DELETED.sendTo(user, name))
                    .exceptionally(handleException(user, Lang.ERROR_DELETING_HOME));
        } else {
            Lang.HOME_NAME_NOT_FOUND.sendTo(user);
        }
    }

    private Function<Throwable, Void> handleException(User user, Lang lang) {
        return t -> {
            lang.sendTo(user);
            plugin.getLogger().log(Level.SEVERE, ChatColor.stripColor(lang.get()), t);
            return null;
        };
    }

    private void teleportToHome(String name, User user) {
        Home home = user.getData().getHome(name);
        if (home != null) {
            user.getPlayer().teleport(home.location());
            Lang.HOME_TELEPORTED_TO.sendTo(user, name);
        } else {
            Lang.HOME_NAME_NOT_FOUND.sendTo(user);
        }
    }

    private void sendAllHomes(User user) {
        Set<Home> homes = user.getData().getHomes();
        if (homes.isEmpty()) {
            Lang.HOME_NAME_NOT_FOUND.sendTo(user);
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("&e------------ &7[&eHomes&7] &e------------\n")
                    .append("&7Below is a list of all your homes:");
            homes.forEach(home -> {
                if (builder.length() > 0)
                    builder.append("\n");
                builder.append(formatHome(home));
            });
            user.getPlayer().sendMessage(Utils.colorize(builder.toString()));
        }
    }

    private String formatHome(Home home) {
        return String.format("&6  %s: [x: %s, y: %s, z: %s]", home.name(),
                (int)home.location().getX(),
                (int)home.location().getY(),
                (int)home.location().getZ());
    }

    @Override
    protected @Nullable List<String> onTabComplete(CommandSource source, String[] args) {
        if (source.getPlayer() == null) return super.onTabComplete(source, args);
        return switch (args.length) {
            case 1 -> tabComplete(args[0], "create", "remove", "list", "teleport");
            case 2 -> tabComplete(args[1], args[0], new String[]{"remove", "delete", "teleport", "tp"}, source.getUser().getData().getHomeNames());
            default -> super.onTabComplete(source, args);
        };
    }
}
