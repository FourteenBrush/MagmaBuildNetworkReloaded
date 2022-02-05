package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public enum Lang {

    NO_PERMISSION("no-permission", "&cI'm sorry but you don't have permission to do that!"),
    NO_CONSOLE("no-console", "&cI'm sorry but the console cannot execute this!"),
    CHANNEL_ALREADY_JOINED("channel.already-joined", "&cYou are already in that channel!"),
    CHANNEL_NOT_WHITELISTED("channel.not-whitelisted", "&cYou are not on the whitelist of that channel!"),
    CHANNEL_JOINED("channel.joined", messageColor() + "You joined {0}", false),
    CHANNEL_NOT_JOINED("channel.not-joined", "&cYou are not in that channel!"),
    CHANNEL_LEFT("channel.left", messageColor() + "You left the channel {0}", false),
    CHANNEL_SET_CURRENT("channel.set-as-current", messageColor() + "You set {0} as your current channel", false),
    VANISH_ANNOUNCE("vanish.announce", "&e{0} has vanished. Poof"),
    VANISHED_BACK_VISIBLE_ANNOUNCE("vanish.back-visible-announce", "&e{0} has become visible"),
    VANISHED_NO_PLAYERS_VANISHED("vanish.no-players-vanished", "&cThere are currently no vanished players online!"),

    /* command help messages */

    HOME_COMMAND_HELP("command-help.home", "&e------------ &7[&eHome Command&7] &e------------" +
            "&7Below is a list of all home subcommands:" +
            "  &6/home create <name> &7- &6Creates a new home" +
            "  /home remove <name> &7- &6Removes the home with that name" +
            "  /home list &7- &6Shows a list of all your homes" +
            "  /home teleport <name> &7- &6Teleports you to the home with that name" +
            "  /home help &7- &6Shows this message"),

    VANISH_COMMAND_HELP("command.help.vanish", "&e------------ &7[&eVanish Command&7] &e------------" +
            "&7Below is a list of all vanish subcommands:" +
            "  &6/vanish <player> &7- &6Vanishes the mentioned player" +
            "  /vanish list &7- &6Shows a list of all the vanished players on the server" +
            "  /vanish fakequit &7- &6Sends the server a fake leave message and vanishes you");

    private static final ChatColor messageColor = ChatColor.of("#83c916");
    private final String path;
    private final String fallback;
    private final boolean needsColoring;
    private String value;

    Lang(String path, String fallback) {
        this(path, fallback, true);
    }

    Lang(String path, String fallback, boolean needsColoring) {
        this.path = path;
        this.fallback = fallback;
        this.needsColoring = needsColoring;
    }

    public String get(String... args) {
        if (args.length > 0) {
            String result = null;
            for (int i = 0; i < args.length; i++) {
                result = value.replace("{" + i + "}", args[i]);
            }
            return result;
        }
        return value;
    }

    public static void initialize(MBNPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "lang.yml");
        if (!file.exists()) {
            plugin.saveResource("lang.yml", false);
        }
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        boolean fileNeedsToBeSaved = false;
        for (Lang l : values()) {
            if (l.name().endsWith("COMMAND_HELP")) continue; // command help messages cannot be overridden and aren't placed in the lang file either
            String real = yaml.getString(l.path);
            if (real == null || real.isEmpty()) {
                plugin.getLogger().warning("Missing or empty lang data found on path " + l.path + ", using fallback (" + l.fallback + ")");
                yaml.set(l.path, l.fallback);
                real = l.fallback;
                fileNeedsToBeSaved = true;
            }
            l.value = l.needsColoring ? Utils.colorize(real) : real;
        }
        if (fileNeedsToBeSaved) {
            try {
                yaml.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to add new entries to the lang.yml file");
                e.printStackTrace();
            }
        }
    }

    private static ChatColor messageColor() {
        return messageColor;
    }
}
