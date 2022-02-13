package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomRank;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Storage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.StorageType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.connection.ConnectionFactory;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.UserSnapshot;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.ChatProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.MembershipProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Constants;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SqlStorage implements Storage {
    private final ConnectionFactory connectionFactory;

    public SqlStorage(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void initialize() {
        try (Connection conn = connectionFactory.getConnection()) {
            for (String query : Constants.TABLES) {
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.execute();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        connectionFactory.close();
    }

    @Override
    public StorageType getType() {
        return StorageType.MYSQL; // or h2 but the implementation handles all sql languages the same
    }

    @Override
    @Nullable
    public UserSnapshot loadUser(UUID id) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.LOAD_USER)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int playtime = rs.getInt("playtime");
                int level = rs.getInt("player_level");
                long firstJoin = rs.getLong("first_join");
                StatisticsProfile statisticsProfile = new StatisticsProfile(id, playtime, level, firstJoin);
                String kingdom = rs.getString("kingdom");
                KingdomRank kingdomRank = KingdomRank.fromString(rs.getString("kingdom_rank"));
                MembershipProfile membershipProfile = new MembershipProfile(loadKingdom(kingdom), kingdomRank);
                // TODO don't load kingdom, get it from cache
                return new UserSnapshot(new ChatProfile(id), statisticsProfile, membershipProfile);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void saveUser(UserSnapshot snapshot) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.SAVE_USER)) {
            ps.setInt(1, snapshot.statisticsProfile().getPlaytime());
            ps.setInt(2, snapshot.statisticsProfile().getLevel());
            ps.setLong(3, System.currentTimeMillis());
            ps.setString(4, snapshot.membershipProfile().getKingdom().getName());
            ps.setString(5, snapshot.membershipProfile().getKingdomRank().name());
            ps.setString(6, snapshot.statisticsProfile().getId().toString());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(UserSnapshot snapshot) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.CREATE_USER)) {
            ps.setString(1, snapshot.statisticsProfile().getId().toString());
            ps.setInt(2, snapshot.statisticsProfile().getPlaytime());
            ps.setInt(3, snapshot.statisticsProfile().getLevel());
            long now = System.currentTimeMillis();
            ps.setLong(4, now);
            ps.setLong(5, now);
            ps.setString(6, snapshot.membershipProfile().getKingdom().getName());
            ps.setString(7, snapshot.membershipProfile().getKingdomRank().name());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChatChannel loadChannel(String name) {
        return null;
    }

    @Override
    public void saveChannel(ChatChannel channel) {

    }

    @Override
    public void createChannel(ChatChannel channel) {

    }

    @Override
    public void deleteChannel(String name) {

    }

    @Override
    public Collection<Home> loadHomes(UUID owner) {
        Set<Home> homes = new HashSet<>();
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.LOAD_HOMES)) {
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
            throw new RuntimeException(e);
        }
        return homes;
    }

    @Override
    public void createHome(Home home) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.CREATE_HOME)) {
            ps.setString(1, home.getOwner().toString());
            ps.setDouble(2, home.getLocation().getX());
            ps.setDouble(3, home.getLocation().getY());
            ps.setDouble(4, home.getLocation().getZ());
            ps.setFloat(5, home.getLocation().getPitch());
            ps.setFloat(6, home.getLocation().getYaw());
            if (home.getLocation().getWorld() != null) {
                ps.setString(7, home.getLocation().getWorld().getName());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteHome(Home home) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.DELETE_HOME)) {
            ps.setString(1, home.getOwner().toString());
            ps.setString(2, home.getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Kingdom loadKingdom(String name) {
        return null;
    }

    @Override
    public void saveKingdom(Kingdom kingdom) {

    }

    @Override
    public void createKingdom(Kingdom kingdom) {

    }
}
