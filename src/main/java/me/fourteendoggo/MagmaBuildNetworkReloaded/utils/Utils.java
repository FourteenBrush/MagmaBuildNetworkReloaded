package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-f0-9]{6}");

    private Utils() {}

    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String colorizeHex(String str) {
        Matcher matcher = HEX_PATTERN.matcher(str);
        while (matcher.find()) {
            String color = str.substring(matcher.start(), matcher.end());

        }
        return ";";
    }

    public static boolean equals(Object... args) {
        Validate.isTrue(args.length % 2 == 0);
        for (int i = 0; i < args.length - 1; i++) {
            if (!args[i].equals(args[++i])) return false;
        }
        return true;
    }
}
