package me.fourteendoggo.MagmaBuildNetworkReloaded.user;

import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomRank;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.ChatProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.MembershipProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

public class UserSnapshot {
    private final UUID id;
    private final ChatProfile chatProfile;
    private final StatisticsProfile statisticsProfile;
    private final MembershipProfile membershipProfile;
    private final Map<CooldownType, Cooldown> cooldowns = new HashMap<>(); // TODO load them
    private final Set<Home> homes;

    public UserSnapshot(UUID id, ChatProfile chatProfile, StatisticsProfile statisticsProfile, MembershipProfile membershipProfile, Set<Home> homes) {
        this.id = id;
        this.chatProfile = chatProfile;
        this.statisticsProfile = statisticsProfile;
        this.membershipProfile = membershipProfile;
        this.homes = homes;
    }

    public static UserSnapshot createNew(UUID id) {
        return new UserSnapshot(
                id,
                new ChatProfile(),
                new StatisticsProfile(), // TODO change kingdom to null
                new MembershipProfile(new Kingdom("test", KingdomType.GRAUDOR, null), KingdomRank.INHABITANT),
                new HashSet<>());
    }

    public UUID getId() {
        return id;
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

    public Cooldown getCooldown(CooldownType type) {
        return cooldowns.get(type);
    }

    public void addCooldown(CooldownType type, Cooldown cooldown) {
        cooldowns.put(type, cooldown);
    }

    public void removeCooldown(CooldownType type) {
        cooldowns.remove(type);
    }

    @UnmodifiableView
    public Set<Home> getHomes() {
        return new HashSet<>(homes);
    }

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
