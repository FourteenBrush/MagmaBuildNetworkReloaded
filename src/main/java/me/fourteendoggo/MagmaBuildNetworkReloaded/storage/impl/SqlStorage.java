package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomRank;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomType;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public StorageType getStorageType() {
        return connectionFactory.getStorageType();
    }

    @Nullable
    @Override
    public UserSnapshot loadUser(UUID id) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.LOAD_USER)) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int playtime = rs.getInt("playtime");
                int level = rs.getInt("player_level");
                long firstJoin = rs.getLong("first_join");
                StatisticsProfile statisticsProfile = new StatisticsProfile(id, playtime, level, firstJoin);
                KingdomRank kingdomRank = KingdomRank.fromString(rs.getString("kingdom_rank"));
                MembershipProfile membershipProfile = new MembershipProfile(new Kingdom("test", KingdomType.YEXORA, null), kingdomRank);
                // TODO don't load kingdom, get it from cache, fix constructor, load homes too
                return new UserSnapshot(new ChatProfile(id), statisticsProfile, membershipProfile, loadHomes(id));
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
            ps.setInt(1, snapshot.statisticsProfile().getMinutesPlayed());
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
            ps.setInt(2, snapshot.statisticsProfile().getMinutesPlayed());
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

    @Nullable
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
                Location loc = new Location(
                        Bukkit.getWorld(rs.getString("location_world")),
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
            ps.setString(1, home.name());
            ps.setString(2, home.owner().toString());
            ps.setDouble(3, home.location().getX());
            ps.setDouble(4, home.location().getY());
            ps.setDouble(5, home.location().getZ());
            ps.setFloat(6, home.location().getPitch());
            ps.setFloat(7, home.location().getYaw());
            ps.setString(8, home.location().getWorld().getName()); // if world is null something is seriously wrong
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteHome(Home home) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(Constants.DELETE_HOME)) {
            ps.setString(1, home.owner().toString());
            ps.setString(2, home.name());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
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
