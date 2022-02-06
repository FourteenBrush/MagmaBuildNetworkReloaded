package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-f0-9]{6}");

    private Utils() {}

    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String colorizeSupportHex(String str) {
        Matcher matcher = HEX_PATTERN.matcher(str);
        while (matcher.find()) {
            String color = str.substring(matcher.start(), matcher.end());
            str = str.replace(color, ChatColor.of(color).toString());
        }
        return colorize(str);
    }

    public static boolean equals(Object... args) {
        Validate.isTrue(args.length % 2 == 0);
        for (int i = 0; i < args.length - 1; i++) {
            if (!args[i].equals(args[++i])) return false;
        }
        return true;
    }
}
