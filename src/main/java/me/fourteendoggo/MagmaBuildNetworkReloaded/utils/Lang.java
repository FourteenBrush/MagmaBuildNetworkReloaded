package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public enum Lang {
    NO_PERMISSION("no-permission", "&cI'm sorry but you don't have permission to do that!"),
    NO_CONSOLE("no-console", "&cI'm sorry but the console cannot execute this!"),
    PICK_KINGDOM_FIRST("pick-kingdom-first", "&cPlease select a kingdom to join before you can do anything!"),
    JOIN_MESSAGE("join-message.normal", "&7[&a&l+&7] &b{0} &7joined the server"),
    JOINED_VANISHED("join-message.vanished", "&7[&a&l+&7] &3{0} joined vanished and silently"),
    LEAVE_MESSAGE("leave-message.normal", "&7[&c&l-&7] &b{0} &7left the server"),
    LEFT_VANISHED("leave-message.vanished", "&7[&c&l-&7] &3{0} left vanished and silently"),
    PLAYER_NOT_FOUND("player-not-found", "&cThat player cannot be found!"),
    PLAYTIME("playtime", messageColor() + "Your playtime is {0} and the first time you joined the server was on {1}"),
    PLAYTIME_OTHER("playtime-other", messageColor() + "{0}'s playtime is {1} and they joined the server for the first time on {2}"),
    CHANNEL_ALREADY_JOINED("channel.already-joined", "&cYou are already in that channel!"),
    CHANNEL_NOT_WHITELISTED("channel.not-whitelisted", "&cYou are not on the whitelist of that channel!"),
    CHANNEL_JOINED("channel.joined", messageColor() + "You joined {0}"),
    CHANNEL_NOT_JOINED("channel.not-joined", "&cYou are not in that channel!"),
    CHANNEL_LEFT("channel.left", messageColor() + "You left the channel {0}"),
    CHANNEL_SET_CURRENT("channel.set-as-current", messageColor() + "You set {0} as your current channel"),
    CHANNEL_NOT_FOUND("channel.not-found", "&cThat channel does not seem to exist!"),
    VANISH_ENABLED("vanish.enabled", "&6You have been vanished"),
    VANISH_ENABLED_BY_OTHER("vanish.enabled-by-other", "&6You have been vanished by {0}"),
    VANISH_ENABLED_FOR_OTHER("vanish.disabled-for-other", messageColor() + "You vanished {0}"),
    VANISH_DISABLED("vanish.disabled", "&6You are visible again"),
    VANISH_DISABLED_BY_OTHER("vanish.disabled-by-other", "&6You have been un-vanished by {0}"),
    VANISH_DISABLED_FOR_OTHER("vanish.disabled-for-other", messageColor() + "You un-vanished {0}"),
    VANISH_ALREADY_VANISHED("vanish.already-vanished", "&cYou are already vanished!"),
    VANISH_OTHER_ALREADY_VANISHED("vanish.other-already-vanished", "&cThat player is already vanished!"),
    VANISH_ALREADY_VISIBLE("vanish.already-visible", "&cYou are already visible!"),
    VANISH_OTHER_ALREADY_VISIBLE("vanish.other-already-visible", "&cThat player is already visible!"),
    VANISH_ANNOUNCE("vanish.announce", "&e{0} has vanished. Poof"),
    VANISH_BACK_VISIBLE_ANNOUNCE("vanish.back-visible-announce", "&e{0} has become visible"),
    VANISH_NO_PLAYERS_VANISHED("vanish.no-players-vanished", "&cThere are currently no vanished players online!"),
    HOME_LIMIT_REACHED("home.limit-reached", "&cYou have reached the maximum amount of homes you can have, you can try to delete one first!"),
    HOME_CREATED_NEW("home.created-new", messageColor() + "You have created a new home, {0}"),
    HOME_DELETED("home.deleted", messageColor() + "You've deleted your home, {0}"),
    HOME_TELEPORTED_TO("home.teleported-to", messageColor() + "You teleported to your home, {0}"),
    HOMES_NO_HOMES_SET("home.no-homes-created", "&cYou haven't created any homes!"),
    HOME_NAME_NOT_FOUND("home.name-not-found", "&cYou have no home with that name!"),
    HOME_CANNOT_HAVE_DUPLICATES("home.cannot-have-duplicates", "&cYou already have a home with that name!"),
    ERROR_CREATING_HOME("error.creating-home", "&cSomething went wrong creating a home!"),
    ERROR_DELETING_HOME("error.deleting-home", "&cSomething went wrong deleting a home!"),
    ERROR_FAILED_TO_LOAD_DATA("error.failed-to-load-data", "&cWe failed to load your player data, please try again in a while!"),
    CHAT_COOLDOWN("cooldown.chat", "&cYou are sending messages too fast, please slow down!"),
    COMMAND_COOLDOWN("cooldown.command", "&cPlease wait &e{0}&c before reusing this command!"),

    /* command help messages - cannot be overridden */

    HOME_COMMAND_HELP("command-help.home", """
            &e------------ &7[&eHome Command&7] &e------------&7
              Below is a list of all home subcommands:
              &6/home create <name> &7- &6Creates a new home
              /home remove <name> &7- &6Removes the home with that name
              /home list &7- &6Shows a list of all your homes
              /home teleport <name> &7- &6Teleports you to the home with that name
              /home help &7- &6Shows this message"""),
    VANISH_COMMAND_HELP("command.help.vanish", """
            &e------------ &7[&eVanish Command&7] &e------------&7
              Below is a list of all vanish subcommands:
              &6/vanish <player> &7- &6Vanishes the mentioned player
              /vanish enable [player] &7- &6Vanishes the mentioned player, or yourself
              /vanish disable [player] &7- &6Un-vanishes the mentioned player, or yourself
              /vanish list &7- &6Shows a list of all the vanished players on the server
              /vanish fakequit &7- &6Sends the server a fake leave message and vanishes you
              /vanish fakejoin &7- &6Sends the server a fake join message and un-vanishes you"""),
    PLAYTIME_COMMAND_HELP("command.help.playtime", """
            &e------------ &7[&ePlaytime Command&7] &e------------&7
              Below is a list of all playtime subcommands:
              &6/playtime <player> &7- &6Shows the playtime for the mentioned player, or yourself""");

    private final String path;
    private final String fallback;
    private String value;

    Lang(String path, String fallback) {
        this.path = path;
        this.fallback = fallback;
    }

    public String get(String... args) {
        if (args.length > 0) {
            String result = value;
            for (int i = 0; i < args.length; i++) {
                result = value.replace("{" + i + "}", args[i]);
            }
            return result;
        }
        return value;
    }

    public void sendTo(CommandSender target, String... args) {
        target.sendMessage(get(args));
    }

    public void sendTo(User target, String... args) {
        sendTo(target.getPlayer(), args);
    }

    public static void initialize(MBNPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "lang.yml");
        if (!file.exists()) {
            plugin.saveResource("lang.yml", false);
        }
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        boolean fileNeedsToBeSaved = false;
        for (Lang entry : values()) {
            String real;
            if (entry.name().endsWith("COMMAND_HELP")) {
                real = entry.fallback; // command help messages cannot be overridden and aren't placed in the lang file either
            } else {
                fileNeedsToBeSaved = true;
                real = yaml.getString(entry.path);
                if (real == null || real.isEmpty() || real.isBlank()) {
                    plugin.getLogger().warning("Missing or empty lang data found on path '" + entry.path + "' replacing it with fallback");
                    yaml.set(entry.path, entry.fallback);
                    real = entry.fallback;
                }
            }
            entry.value = Utils.colorizeSupportHex(real);
        }
        if (fileNeedsToBeSaved) {
            try {
                yaml.save(file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to add new entries to the lang.yml file", e);
            }
        }
    }

    private static String messageColor() {
        return "#83c916";
    }

    // TODO components interface/ enum
}
