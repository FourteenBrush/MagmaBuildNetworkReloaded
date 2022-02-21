package me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles;

import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomRank;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MembershipProfile {
    private final Kingdom kingdom;
    private final KingdomRank kingdomRank;

    public MembershipProfile(Kingdom kingdom, KingdomRank kingdomRank) {
        this.kingdom = kingdom;
        this.kingdomRank = kingdomRank;
    }

    @Nullable
    public Kingdom getKingdom() {
        return kingdom;
    }

    @Nullable
    public KingdomRank getKingdomRank() {
        return kingdomRank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MembershipProfile that = (MembershipProfile) o;
        return kingdom.equals(that.kingdom) && kingdomRank == that.kingdomRank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kingdom, kingdomRank);
    }
}
