package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

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

    public static String minutesToReadable(int minutes) {
        long days = TimeUnit.MINUTES.toDays(minutes);
        minutes -= TimeUnit.DAYS.toMinutes(days);
        long hours = TimeUnit.DAYS.toHours(minutes);
        minutes -= TimeUnit.HOURS.toDays(hours);
        String output = "";
        if (days > 0) {
            output += days + " days";
        }
        if (hours > 0) {
            output += output.isEmpty() ? hours + " hours" : ", " + hours + " hours";
        }
        if (minutes > 0) {
            output += output.isEmpty() ? minutes + " minutes" : ", " + minutes + " minutes";
        }
        return output;
    }
}
