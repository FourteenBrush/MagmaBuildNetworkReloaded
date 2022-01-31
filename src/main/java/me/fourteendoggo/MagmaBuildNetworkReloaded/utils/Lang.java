package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public enum Lang {

    NO_PERMISSION("no-permission", "&cI'm sorry but you don't have permission to do that!"),
    NO_CONSOLE("no-console", "&cI'm sorry but the console cannot execute this!"),
    CHANNEL_ALREADY_JOINED("channel.already-joined", "&cYou are already in that channel!"),
    CHANNEL_NOT_WHITELISTED("channel.not-whitelisted", "&cYou are not on the whitelist of that channel!"),
    CHANNEL_JOINED("channel.joined", messageColor() + "You joined {0}", false),
    CHANNEL_NOT_JOINED("channel.not-joined", "&cYou are not in that channel!"),
    CHANNEL_LEFT("channel.left", messageColor() + "You left the channel {0}");

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
        for (Lang l : values()) {
            String real = yaml.getString(l.path);
            if (real == null || real.isEmpty()) {
                plugin.getLogger().warning("Missing or empty lang data found on path " + l.path + ", using fallback");
                real = l.fallback;
            }
            if (l.needsColoring) {
                l.value = Utils.colorize(real);
            } else {
                l.value = real;
            }
        }
    }

    private static ChatColor messageColor() {
        return messageColor;
    }
}
