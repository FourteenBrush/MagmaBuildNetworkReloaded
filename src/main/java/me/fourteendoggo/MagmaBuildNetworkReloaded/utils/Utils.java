package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import org.bukkit.ChatColor;

public class Utils {

    private Utils() {}

    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
