package me.fourteendoggo.MagmaBuildNetworkReloaded.user;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.MembershipProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;

import java.util.UUID;

public interface IUser {

    UUID getId();

    StatisticsProfile getStatisticsProfile();

    MembershipProfile getMembershipProfile();

    void sendMessage(String message);

    void login(MBNPlugin plugin);

    void logoutSafely();
}
