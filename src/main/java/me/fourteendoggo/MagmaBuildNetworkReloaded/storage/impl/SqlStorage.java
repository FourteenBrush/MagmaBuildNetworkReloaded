package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomRank;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.KingdomType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.ConnectionFactory;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Storage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.StorageType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.UserSnapshot;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.ChatProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.MembershipProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.profiles.StatisticsProfile;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SqlStorage implements Storage {
    private static final @Language("SQL") String LOAD_USER = "SELECT * FROM users WHERE uuid=?;";
    private static final @Language("SQL") String CREATE_USER = "INSERT INTO users(uuid, playtime, player_level, last_update, first_join, kingdom, kingdom_rank) VALUES(?,?,?,?,?,?,?);";
    private static final @Language("SQL") String SAVE_USER = "UPDATE users SET playtime=?, player_level=?, last_update=?, kingdom=?, kingdom_rank=? WHERE uuid=?;";
    private static final @Language("SQL") String LOAD_HOMES = "SELECT * FROM homes WHERE owner=?;";
    private static final @Language("SQL") String CREATE_HOME = "INSERT INTO homes(name, owner, location_x, location_y, location_z, location_pitch, location_yaw, location_world) VALUES(?,?,?,?,?,?,?,?);";
    private static final @Language("SQL") String DELETE_HOME = "DELETE FROM homes WHERE owner=? AND name=?;";
    private static final @Language("SQL") String[] TABLE_SETUP = {
            "CREATE TABLE IF NOT EXISTS users(uuid VARCHAR(40), playtime INT, player_level INT, last_update BIGINT, first_join BIGINT, kingdom VARCHAR(40), kingdom_rank VARCHAR(40), PRIMARY KEY(uuid));",
            "CREATE TABLE IF NOT EXISTS kingdoms(name VARCHAR(40), PRIMARY KEY(name));",
            "CREATE TABLE IF NOT EXISTS homes(name VARCHAR(40), owner VARCHAR(40), location_x DOUBLE, location_y DOUBLE, location_z DOUBLE, location_pitch FLOAT, location_yaw FLOAT, location_world VARCHAR(40), PRIMARY KEY(name, owner));"
    };
    private final ConnectionFactory connectionFactory;

    public SqlStorage(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void initialize() {
        try (Connection conn = connectionFactory.getConnection()) {
            for (String query : TABLE_SETUP) {
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
    public StorageType getStorageType() {
        return connectionFactory.getStorageType();
    }

    @Nullable
    @Override
    public UserSnapshot loadUser(UUID id) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(LOAD_USER)) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int playtime = rs.getInt("playtime");
                int level = rs.getInt("player_level");
                long firstJoin = rs.getLong("first_join");
                StatisticsProfile statisticsProfile = new StatisticsProfile(id, playtime, level, firstJoin);
                KingdomRank kingdomRank = KingdomRank.fromString(rs.getString("kingdom_rank"));
                MembershipProfile membershipProfile = new MembershipProfile(new Kingdom("test", KingdomType.YEXORA, null), kingdomRank);
                // TODO don't create random kingdom, get it from cache, fix constructor
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
             PreparedStatement ps = conn.prepareStatement(SAVE_USER)) {
            ps.setInt(1, snapshot.getStatisticsProfile().getMinutesPlayed());
            ps.setInt(2, snapshot.getStatisticsProfile().getLevel());
            ps.setLong(3, System.currentTimeMillis()); // TODO check nullability
            ps.setString(4, snapshot.getMembershipProfile().getKingdom().getName());
            ps.setString(5, snapshot.getMembershipProfile().getKingdomRank().name());
            ps.setString(6, snapshot.getStatisticsProfile().getId().toString());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(UserSnapshot snapshot) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(CREATE_USER)) {
            ps.setString(1, snapshot.getStatisticsProfile().getId().toString());
            ps.setInt(2, snapshot.getStatisticsProfile().getMinutesPlayed());
            ps.setInt(3, snapshot.getStatisticsProfile().getLevel());
            long now = System.currentTimeMillis();
            ps.setLong(4, now);
            ps.setLong(5, now);
            ps.setString(6, snapshot.getMembershipProfile().getKingdom().getName());
            ps.setString(7, snapshot.getMembershipProfile().getKingdomRank().name());
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
    public Set<Home> loadHomes(UUID owner) {
        Set<Home> homes = new HashSet<>();
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(LOAD_HOMES)) {
            ps.setString(1, owner.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                homes.add(constructHome(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return homes;
    }

    private Home constructHome(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        UUID owner = UUID.fromString(rs.getString("owner"));
        World world = Bukkit.getWorld(rs.getString("location_world"));
        double x = rs.getDouble("location_x");
        double y = rs.getDouble("location_y");
        double z = rs.getDouble("location_z");
        float yaw = rs.getFloat("location_yaw");
        float pitch = rs.getFloat("location_pitch");
        Location location = new Location(world, x, y, z, yaw, pitch);
        return new Home(name, owner, location);
    }

    @Override
    public void createHome(Home home) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(CREATE_HOME)) {
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
             PreparedStatement ps = conn.prepareStatement(DELETE_HOME)) {
            ps.setString(1, home.owner().toString());
            ps.setString(2, home.name());
            ps.execute();
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
