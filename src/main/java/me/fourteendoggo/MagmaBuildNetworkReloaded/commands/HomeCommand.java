package me.fourteendoggo.MagmaBuildNetworkReloaded.commands;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandBase;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandResult;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.handlers.CommandSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.HomeRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class HomeCommand extends CommandBase {
    private final HomeRepository homeRepository;

    public HomeCommand(MBNPlugin plugin, HomeRepository homeRepository) {
        super(plugin, "home", Permission.DEFAULT, true);
        this.homeRepository = homeRepository;
    }

    @Override
    protected CommandResult execute(CommandSource source, @NotNull String[] args) {
        if (!source.getPlayer().isPresent()) return CommandResult.PLAYER_ONLY;
        if (args.length == 2) {
            switch (args[0]) {
                case "create":
                    int homesLimit = Permission.MODERATOR.has(source.getPlayer().get()) ? 5 : 2;
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
            plugin.getStorage().createNewHome(home).whenComplete((v, t) -> {
                from.sendMessage(Lang.HOME_CREATED_NEW.get(name));
                homeRepository.cache(new Entry<>(from.getUniqueId(), name), home);
            }).exceptionally(onException(from));
        }
    }

    private void deleteHome(String name, Player from) {
        Optional<Home> home = homeRepository.get(new Entry<>(from.getUniqueId(), name));
        if (home.isPresent()) {
            plugin.getStorage().deleteHome(home.get()).whenComplete((v, t) -> {
                from.sendMessage(Lang.HOME_DELETED.get(name));
                homeRepository.removeByValue(home.get());
            }).exceptionally(onException(from));
        } else {
            from.sendMessage(Lang.HOME_NAME_NOT_FOUND.get());
        }
    }

    private void teleportToHome(String name, Player from) {
        Optional<Home> home = homeRepository.get(new Entry<>(from.getUniqueId(), name));
        if (home.isPresent()) {
            from.teleport(home.get().getLocation());
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
                        home.getName(), (int)home.getLocation().getX(), (int)home.getLocation().getY(),
                        (int)home.getLocation().getZ()));
            });
            from.sendMessage(Utils.colorize(builder.toString()));
        }
    }

    private Function<Throwable, Void> onException(Player player) {
        return t -> {
            player.sendMessage(Lang.ERROR_CREATING_HOME.get());
            t.printStackTrace();
            return null;
        };
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("create", "remove", "list", "teleport"), new ArrayList<>());
        } else if (args.length == 2) {
            switch (args[0]) {
                case "remove":
                case "delete":
                case "teleport":
                case "tp":
                    Iterable<String> iterable = homeRepository.getAllNamesFor(((Player)sender).getUniqueId());
                    return StringUtil.copyPartialMatches(args[1], iterable, new ArrayList<>());
            }
        }
        return super.onTabComplete(sender, cmd, label, args);
    }

    @Override
    protected @NotNull String getUsage() {
        return Lang.HOME_COMMAND_HELP.get();
    }
}
