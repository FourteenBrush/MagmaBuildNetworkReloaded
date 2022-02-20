package me.fourteendoggo.MagmaBuildNetworkReloaded.user;

import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomRank;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.ChatProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.MembershipProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserSnapshot {

    private final ChatProfile chatProfile;
    private final StatisticsProfile statisticsProfile;
    private final MembershipProfile membershipProfile;
    private final Collection<Home> homes;

    public UserSnapshot(ChatProfile chatProfile, StatisticsProfile statisticsProfile, MembershipProfile membershipProfile, Collection<Home> homes) {
        this.chatProfile = chatProfile;
        this.statisticsProfile = statisticsProfile;
        this.membershipProfile = membershipProfile;
        this.homes = homes;
    }

    public static UserSnapshot createNewFor(UUID id) {
        return new UserSnapshot(new ChatProfile(id),
                new StatisticsProfile(id),
                new MembershipProfile(new Kingdom("test", KingdomType.GRAUDOR, null), KingdomRank.INHABITANT),
                new HashSet<>());
    }

    public ChatProfile getChatProfile() {
        return chatProfile;
    }

    public StatisticsProfile getStatisticsProfile() {
        return statisticsProfile;
    }

    public MembershipProfile getMembershipProfile() {
        return membershipProfile;
    }

    @Nullable
    public Home getHome(String name) {
        return homes.stream().filter(home -> home.name().equals(name)).findFirst().orElse(null);
    }

    public int getHomesAmount() {
        return homes.size();
    }

    public void addHome(Home home) {
        homes.add(home);
    }

    public void removeHome(Home home) {
        homes.remove(home);
    }

    public Set<String> getHomeNames() {
        return homes.stream().map(Home::name).collect(Collectors.toSet());
    }
}
