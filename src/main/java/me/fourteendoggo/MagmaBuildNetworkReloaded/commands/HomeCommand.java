package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.HomeRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Permission;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Utils;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class HomeCommand extends CommandBase {
    private final HomeRepository homeRepository;

    public HomeCommand(MBNPlugin plugin) {
        super(plugin, Permission.DEFAULT);
        homeRepository = plugin.getBaseRepository().getHomeRepository();
    }

    @Override
    protected CommandResult execute(CommandSource source, String[] args) {
        if (source.getPlayer().isEmpty()) return CommandResult.PLAYER_ONLY;
        if (args.length == 2) {
            switch (args[0]) {
                case "create":
                    int homesLimit = plugin.getSettings().getHomesLimit(source.getPlayer().get());
                    createHome(args[1], source.getPlayer().get(), homesLimit);
                    break;
                case "remove":
                case "delete":
                    deleteHome(args[1], source.getPlayer().get());
                    break;
                case "teleport":
                case "tp":
                    teleportToHome(args[1], source.getPlayer().get());
                    break;
                default:
                    return CommandResult.SHOW_USAGE;
            }
        } else if (args.length == 1 && args[0].equals("list")) {
            sendAllHomes(source.getPlayer().get());
        } else {
            return CommandResult.SHOW_USAGE;
        }
        return CommandResult.SUCCESS;
    }

    private void createHome(String name, Player from, int homesLimit) {
        if (homeRepository.getAllFor(from.getUniqueId()).size() >= homesLimit) {
            from.sendMessage(Lang.HOME_LIMIT_REACHED.get());
        } else {
            Home home = new Home(name, from.getUniqueId(), from.getLocation());
            plugin.getStorage().createHome(home).whenComplete((v, t) -> {
                from.sendMessage(Lang.HOME_CREATED_NEW.get(name));
                homeRepository.cache(new Pair<>(from.getUniqueId(), name), home);
            }).exceptionally(onException(from, Lang.ERROR_CREATING_HOME));
        }
    }

    private void deleteHome(String name, Player from) {
        Optional<Home> home = homeRepository.get(new Pair<>(from.getUniqueId(), name));
        if (home.isPresent()) {
            plugin.getStorage().deleteHome(home.get()).whenComplete((v, t) -> {
                from.sendMessage(Lang.HOME_DELETED.get(name));
                homeRepository.removeByValue(home.get());
            }).exceptionally(onException(from, Lang.ERROR_DELETING_HOME));
        } else {
            from.sendMessage(Lang.HOME_NAME_NOT_FOUND.get());
        }
    }

    private Function<Throwable, Void> onException(Player player, Lang lang) {
        return t -> {
            String message = lang.get();
            player.sendMessage(message);
            plugin.getLogger().severe(message);
            t.printStackTrace();
            return null;
        };
    }

    private void teleportToHome(String name, Player from) {
        Optional<Home> home = homeRepository.get(new Pair<>(from.getUniqueId(), name));
        if (home.isPresent()) {
            from.teleport(home.get().location());
            from.sendMessage(Lang.HOME_TELEPORTED_TO.get(name));
        } else {
            from.sendMessage(Lang.HOME_NAME_NOT_FOUND.get());
        }
    }

    private void sendAllHomes(Player from) {
        Set<Home> homes = homeRepository.getAllFor(from.getUniqueId());
        if (homes.isEmpty()) {
            from.sendMessage(Lang.HOMES_NO_HOMES_SET.get());
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("&e------------ &7[&eHomes&7] &e------------\n")
                    .append("&7Below is a list of all your homes:");
            homes.forEach(home -> {
                if (builder.length() > 0)
                    builder.append("\n");
                builder.append(String.format("&6  %s: [x: %s, y: %s, z: %s]",
                        home.name(), (int)home.location().getX(), (int)home.location().getY(),
                        (int)home.location().getZ()));
            });
            from.sendMessage(Utils.colorize(builder.toString()));
        }
    }

    @Override
    protected @Nullable List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return tabcomplete(args[0], Arrays.asList("create", "remove", "list", "teleport"));
        } else if (args.length == 2) {
            switch (args[0]) {
                case "remove", "delete", "teleport", "tp" -> {
                    Iterable<String> iterable = homeRepository.getAllNamesFor(((Player) sender).getUniqueId());
                    return tabcomplete(args[1], iterable);
                }
            }
        }
        return super.onTabComplete(sender, args);
    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.HOME_COMMAND_HELP.get();
    }
}
