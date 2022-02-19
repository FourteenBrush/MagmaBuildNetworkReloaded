package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

import org.intellij.lang.annotations.Language;

public interface Constants {
    @Language("SQL") String LOAD_USER = "SELECT * FROM users WHERE uuid=?;";
    @Language("SQL") String CREATE_USER = "INSERT INTO users(uuid, playtime, player_level, last_update, first_join, kingdom, kingdom_rank) VALUES(?,?,?,?,?,?,?);";
    @Language("SQL") String SAVE_USER = "UPDATE users SET playtime=?, player_level=?, last_update=?, kingdom=?, kingdom_rank=? WHERE uuid=?;";
    @Language("SQL") String LOAD_HOMES = "SELECT * FROM homes WHERE user=?;";
    @Language("SQL") String CREATE_HOME = "INSERT INTO homes(name, owner, location_x, location_y, location_z, location_pitch, location_yaw, location_world) VALUES(?,?,?,?,?,?,?,?);";
    @Language("SQL") String DELETE_HOME = "DELETE FROM homes WHERE user=?, name=?;";
    @Language("MYSQL-PSQL") String[] TABLES = {
            "CREATE TABLE IF NOT EXISTS users(uuid VARCHAR(40), playtime INT, player_level INT, last_update BIGINT, first_join BIGINT, kingdom VARCHAR(40), kingdom_rank VARCHAR(40), PRIMARY KEY(uuid));",
            "CREATE TABLE IF NOT EXISTS kingdoms(name VARCHAR(40), PRIMARY KEY(name));",
            "CREATE TABLE IF NOT EXISTS homes(name VARCHAR(40), owner VARCHAR(40), location_x DOUBLE, location_y DOUBLE, location_z DOUBLE, location_pitch FLOAT, location_yaw FLOAT, location_world VARCHAR(40), PRIMARY KEY(name, owner));"
    };
    int DEFAULT_HOMES_LIMIT = 2;
    int MODERATOR_HOMES_LIMIT = 5;
}
