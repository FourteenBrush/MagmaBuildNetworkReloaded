package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

public enum StorageType {

    MYSQL("MySQL"),
    H2("H2");

    private final String name;

    StorageType(String name) {
        this.name = name;
    }

    public static StorageType fromString(String str, StorageType fallback) {
        for (StorageType s : values()) {
            if (s.getName().equalsIgnoreCase(str)) {
                return s;
            }
        }
        return fallback;
    }

    public String getName() {
        return name;
    }
}
