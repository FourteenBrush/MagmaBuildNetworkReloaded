package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

public enum StorageType {

    MYSQL("MySQL", "MySQL database in online mode"),
    H2("H2", "H2 database in embedded mode");

    private final String name;
    private final String description;

    StorageType(String name, String description) {
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }
}
