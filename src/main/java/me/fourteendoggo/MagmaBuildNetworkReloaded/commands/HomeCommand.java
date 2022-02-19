package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.UserRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

public class HomeCommand extends CommandBase {
    private final UserRepository userRepository;

    public HomeCommand(MBNPlugin plugin) {
        super(plugin, Permission.DEFAULT);
        userRepository = plugin.getBaseRepository().getUserRepository();
    }

    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        if (source.getPlayer().isEmpty()) return CommandResult.PLAYER_ONLY;
        User user = userRepository.get(source.getPlayer().get().getUniqueId());
        assert user != null;
        Validate.notNull(user.getData());
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
        int homesLimit = plugin.getSettings().getHomesLimitFor(user);
        user.getPlayer().sendMessage("user data is: " + user.getData());
        if (user.getData().getHomesAmount() >= homesLimit) {
            user.getPlayer().sendMessage(Lang.HOME_LIMIT_REACHED.get());
        } else if (user.getData().getHome(name) != null) {
            user.getPlayer().sendMessage(Lang.HOME_CANNOT_HAVE_DUPLICATES.get());
        } else {
            Home home = new Home(name, user.getId(), user.getPlayer().getLocation());
            plugin.getStorage().createHome(home).whenComplete((v, t) -> {
                user.getPlayer().sendMessage(Lang.HOME_CREATED_NEW.get(name));
                user.getData().addHome(home);
                user.setDirty(true);
            }).exceptionally(onException(user, Lang.ERROR_CREATING_HOME));
        }
    }

    private void deleteHome(String name, User user) {
        Home home = user.getData().getHome(name);
        if (home != null) {
            plugin.getStorage().deleteHome(home).whenComplete((v, t) -> {
                user.getPlayer().sendMessage(Lang.HOME_DELETED.get(name));
                user.getData().removeHome(home);
                user.setDirty(true);
            }).exceptionally(onException(user, Lang.ERROR_DELETING_HOME));
        } else {
            user.getPlayer().sendMessage(Lang.HOME_NAME_NOT_FOUND.get());
        }
    }

    private Function<Throwable, Void> onException(User user, Lang lang) {
        return t -> {
            String message = lang.get();
            user.getPlayer().sendMessage(message);
            plugin.getLogger().log(Level.SEVERE, message, t);
            return null;
        };
    }

    private void teleportToHome(String name, User user) {
        Home home = user.getData().getHome(name);
        if (home != null) {
            user.getPlayer().teleport(home.location());
            user.getPlayer().sendMessage(Lang.HOME_TELEPORTED_TO.get(name));
        } else {
            user.getPlayer().sendMessage(Lang.HOME_NAME_NOT_FOUND.get());
        }
    }

    private void sendAllHomes(User user) {
        Collection<Home> homes = user.getData().homes();
        if (homes.isEmpty()) {
            user.getPlayer().sendMessage(Lang.HOMES_NO_HOMES_SET.get());
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
        if (args.length == 1) {
            return tabComplete(args[0], Arrays.asList("create", "remove", "list", "teleport"));
        } else if (args.length == 2 && source.getPlayer().isPresent()) {
            switch (args[0]) {
                case "remove", "delete", "teleport", "tp" -> {
                    Player player = source.getPlayer().get();
                    User user = userRepository.get(player.getUniqueId());
                    return tabComplete(args[1], user.getData().getHomeNames());
                }
            }
        }
        return super.onTabComplete(source, args);
    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.HOME_COMMAND_HELP.get();
    }
}
