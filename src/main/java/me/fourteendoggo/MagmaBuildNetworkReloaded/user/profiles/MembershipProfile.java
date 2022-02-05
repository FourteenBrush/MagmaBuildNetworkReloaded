package me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles;

import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomRank;

public class MembershipProfile {
    private final Kingdom kingdom;
    private final KingdomRank kingdomRank;

    public MembershipProfile(Kingdom kingdom, KingdomRank kingdomRank) {
        this.kingdom = kingdom;
        this.kingdomRank = kingdomRank;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public KingdomRank getKingdomRank() {
        return kingdomRank;
    }
}
