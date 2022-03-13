package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.UserSnapshot;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;

import java.sql.SQLException;
import java.util.UUID;

public interface Storage {

    void initialize() throws SQLException;

    void close();

    StorageType getStorageType();

    UserSnapshot loadUser(UUID id) throws SQLException;

    void saveUser(UserSnapshot snapshot) throws SQLException;

    void createUser(UserSnapshot snapshot) throws SQLException;

    ChatChannel loadChannel(String name) throws SQLException;

    void saveChannel(ChatChannel channel) throws SQLException;

    void createChannel(ChatChannel channel) throws SQLException;

    void deleteChannel(String name) throws SQLException;

    void createHome(Home home) throws SQLException;

    void deleteHome(Home home) throws SQLException;

    Kingdom loadKingdom(String name) throws SQLException;

    void saveKingdom(Kingdom kingdom) throws SQLException;

    void createKingdom(Kingdom kingdom) throws SQLException;
}
