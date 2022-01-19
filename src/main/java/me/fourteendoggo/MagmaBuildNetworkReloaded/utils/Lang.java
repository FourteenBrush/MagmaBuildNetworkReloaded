package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

public enum Lang {

    NO_PERMISSION("no-permission", "&cI'm sorry but you don't have permission!"),
    NO_CONSOLE("no-console", "I'm sorry but the console cannot execute this!");

    private final String value;

    Lang(String path, String fallback) {

    }

    public String get(String... args) {
        if (args.length > 0) {
            String result = null;
            for (int i = 0; i < args.length; i++) {
                result = value.replace("{" + i + "}", args[i])
            }
            return result;
        }
        return value;
    }
}
