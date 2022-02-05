package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomRank;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Storage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.StorageType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.connection.ConnectionFactory;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.ChatProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.MembershipProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class MySqlStorage implements Storage {
    private final ConnectionFactory connectionFactory;
    private final Logger logger;

    public MySqlStorage(ConnectionFactory connectionFactory, Logger logger) {
        this.connectionFactory = connectionFactory;
        this.logger = logger;
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MYSQL;
    }

    @Override
    public void initialize() {
        String[] queries = new String[3];
        queries[0] = "CREATE TABLE IF NOT EXISTS users (" +
                "uuid varchar(40)," +
                "playtime int," +
                "level int," +
                "last_update bigint," +
                "first_join bigint," +
                "kingdom varchar(40)," +
                "kingdom_rank varchar(40)," +
                "primary key(uuid));";
        queries[1] = "CREATE TABLE IF NOT EXISTS kingdoms (" +
                "name varchar(40)," +
                "kingdom_type varchar(40)";
        queries[2] = "CREATE TABLE IF NOT EXISTS homes (" +
                "name varchar(40), " +
                "owner varchar(40), " +
                "location_x DOUBLE, " +
                "location_y DOUBLE, " +
                "location_z DOUBLE, " +
                "location_pitch FLOAT, " +
                "location_yaw FLOAT, " +
                "location_world VARCHAR(40), " +
                "PRIMARY KEY(name, owner);";
        try (Connection conn = connectionFactory.getConnection()) {
            for (String query : queries) {
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.execute();
                }
            }
        } catch (SQLException e) {
            logger.severe("Failed to initialize database, type: " + getStorageType());
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        connectionFactory.close();
    }

    @Override
    public User createNewUser(UUID id) {
        ChatProfile chatProfile = new ChatProfile(id);
        StatisticsProfile statisticsProfile = new StatisticsProfile(id);
        return new User(chatProfile, statisticsProfile);
    }

    @Nullable
    @Override
    public User loadUser(UUID id) {
        String sql = "SELECT * FROM users WHERE uuid=?;";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ChatProfile chatProfile = new ChatProfile(id);
                int playTime = rs.getInt("playtime");
                int level = rs.getInt("level");
                long firstJoin = rs.getLong("first_join");
                StatisticsProfile statisticsProfile = new StatisticsProfile(id, playTime, level, firstJoin);
                String kingdom = rs.getString("kingdom");
                KingdomRank kingdomRank = KingdomRank.fromString(rs.getString("kingdom_rank"));
                MembershipProfile membershipProfile = new MembershipProfile(loadKingdom(kingdom), kingdomRank);
                return new User(chatProfile, statisticsProfile, membershipProfile);

            }
        } catch (SQLException e) {
            logger.severe("Failed to load user from the database, type: " + getStorageType());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveUser(User user) {
        String sql = "UPDATE users SET playtime=?, level=?, last_update=?, kingdom=?, kingdom_rank=? WHERE uuid=?;";
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getStatisticsProfile().getPlaytime());
            ps.setInt(2, user.getStatisticsProfile().getLevel());
            ps.setLong(3, System.currentTimeMillis());
            ps.setString(4, user.getMembershipProfile().getKingdom().getName());
            ps.setString(5, user.getMembershipProfile().getKingdomRank().name());
            ps.setString(6, user.getId().toString());
            ps.execute();
        } catch (SQLException e) {
            logger.severe("Failed to save an user to the database");
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public ChatChannel loadChatChannel(String name) {
        return null;
    }

    @Override
    public void saveChatChannel(ChatChannel channel) {

    }

    @Override
    public Collection<Home> loadHomes(UUID user) {
        String sql = "SELECT * FROM homes WHERE user=?, name=?;";
        List<Home> homes = new ArrayList<>();
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Location loc = new Location(Bukkit.getWorld(rs.getString("location_world")),
                            rs.getDouble("location_x"),
                            rs.getDouble("location_y"),
                            rs.getDouble("location_z"),
                            rs.getFloat("location_yaw"),
                            rs.getFloat("location_pitch"));
                    homes.add(new Home(rs.getString("name"),
                            UUID.fromString(rs.getString("owner")),
                            loc));
                }
        } catch (SQLException e) {
            logger.severe("Failed to load homes for user " + user);
            e.printStackTrace();
        }
        return homes;
    }

    @Nullable
    @Override
    public Kingdom loadKingdom(String name) {
        return null;
    }
}
