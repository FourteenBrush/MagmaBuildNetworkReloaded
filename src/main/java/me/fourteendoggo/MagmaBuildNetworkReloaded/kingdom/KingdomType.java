package me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom;

import net.md_5.bungee.api.ChatColor;

public enum KingdomType {
    GRAUDOR("graudor", ChatColor.RED + "[Graudor]"),
    YEXORA("yexora", ChatColor.AQUA + "[Yexora]"),
    XEVIA("xevia", ChatColor.GOLD + "[Xevia]"),
    ESTOTHA("estotha", ChatColor.GREEN + "[Estotha]");

    private final String name;
    private final String prefix;

    KingdomType(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public static KingdomType fromString(String str) {
        for (KingdomType k : values()) {
            if (k.name().equalsIgnoreCase(str)) {
                return k;
            }
        }
        return null;
    }
}
