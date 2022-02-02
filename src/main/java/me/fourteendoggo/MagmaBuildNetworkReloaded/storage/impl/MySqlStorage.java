package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Storage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.StorageType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.connection.ConnectionFactory;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Home;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MySqlStorage implements Storage {
    private final ConnectionFactory connectionFactory;
    private final Logger logger;
    private final Map<UUID, User> userMap;

    public MySqlStorage(ConnectionFactory connectionFactory, Logger logger) {
        this.connectionFactory = connectionFactory;
        this.logger = logger;
        this.userMap = new HashMap<>();
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MYSQL;
    }

    @Override
    public void initialize() {
        String query = "CREATE TABLE IF NOT EXISTS users " +
                "(uuid varchar(40)," +
                "playtime int," +
                "level int," +
                "last_update bigint," +
                "first_join bigint," +
                "kingdom varchar(40)," +
                "kingdom_rank varchar(40)," +
                "primary key(uuid));";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.execute();
        } catch (SQLException e) {
            logger.severe("Failed to initialize database");
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        connectionFactory.close();
    }

    @Override
    public User loadUser(UUID id) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement()) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

            }
        } catch (SQLException e) {
            logger.severe("Failed to load user from the database (" + getStorageType() + ")");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public ChatChannel loadChatChannel(String name) {
        return null;
    }

    @Override
    public void saveChatChannel(ChatChannel channel) {

    }

    @Override
    public Collection<Home> loadHomes(UUID user, String name) {
        return null;
    }
}
