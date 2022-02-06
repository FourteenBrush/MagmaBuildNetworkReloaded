package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Home;

import java.util.Collection;
import java.util.UUID;
// new - load - save - delete
public interface Storage {

    StorageType getStorageType();

    void initialize();

    void close();

    default void convert(StorageType from, StorageType to) {}

    void createNewUser(User user);

    User loadUser(UUID id);

    void saveUser(User user);

    void createNewChatChannel(ChatChannel channel);

    ChatChannel loadChatChannel(String name);

    void saveChatChannel(ChatChannel channel);

    void deleteChatChannel(ChatChannel channel);

    void createNewHome(Home home);

    Collection<Home> loadHomes(UUID user);

    void deleteHome(Home home);

    Kingdom loadKingdom(String name);

    void saveKingdom(String name);
}
