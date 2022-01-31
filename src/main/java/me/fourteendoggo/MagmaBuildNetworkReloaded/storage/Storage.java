package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Home;

import java.util.Collection;
import java.util.UUID;

public interface Storage {

    StorageType getStorageType();

    void initialize();

    void close();

    default void convert(StorageType from, StorageType to) {}

    User loadUser(UUID id);

    void saveUser(User user);

    ChatChannel loadChatChannel(String name);

    void saveChatChannel(ChatChannel channel);

    Collection<Home> loadHomes(UUID user, String name);
}
