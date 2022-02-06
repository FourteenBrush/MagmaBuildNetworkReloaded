package me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom;

public enum KingdomRank {
    INHABITANT;

    public static KingdomRank fromString(String str) {
        for (KingdomRank k : values()) {
            if (k.name().equalsIgnoreCase(str)) return k;
        }
        return INHABITANT;
    }
}
