package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.UserSnapshot;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;

import java.util.Collection;
import java.util.UUID;

public interface Storage {

    void initialize();

    void close();

    StorageType getType();

    UserSnapshot loadUser(UUID id);

    void saveUser(UserSnapshot snapshot);

    void createUser(UserSnapshot snapshot);

    ChatChannel loadChannel(String name);

    void saveChannel(ChatChannel channel);

    void createChannel(ChatChannel channel);

    void deleteChannel(String name);

    Collection<Home> loadHomes(UUID owner);

    void createHome(Home home);

    void deleteHome(Home home);

    Kingdom loadKingdom(String name);

    void saveKingdom(Kingdom kingdom);

    void createKingdom(Kingdom kingdom);
}
