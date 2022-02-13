package me.fourteendoggo.MagmaBuildNetworkReloaded.user;

import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.ChatProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.MembershipProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;
import org.jetbrains.annotations.Contract;

public record UserSnapshot(ChatProfile chatProfile,
                           StatisticsProfile statisticsProfile,
                           MembershipProfile membershipProfile) {

    @Contract("_ -> new")
    public UserSnapshot setChatProfile(ChatProfile chatProfile) {
        return new UserSnapshot(chatProfile, statisticsProfile, membershipProfile);
    }

    @Contract("_ -> new")
    public UserSnapshot setStatisticsProfile(StatisticsProfile statisticsProfile) {
        return new UserSnapshot(chatProfile, statisticsProfile, membershipProfile);
    }

    @Contract("_ -> new")
    public UserSnapshot setMembershipProfile(MembershipProfile membershipProfile) {
        return new UserSnapshot(chatProfile, statisticsProfile, membershipProfile);
    }
}
