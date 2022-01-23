package me.fourteendoggo.MagmaBuildNetworkReloaded.user;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.MembershipProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IUser {

    UUID getId();

    Player getPlayer();

    StatisticsProfile getStatisticsProfile();

    MembershipProfile getMembershipProfile();

    void sendMessage(String message);

    void login(MBNPlugin plugin);

    void logoutSafely();
}
