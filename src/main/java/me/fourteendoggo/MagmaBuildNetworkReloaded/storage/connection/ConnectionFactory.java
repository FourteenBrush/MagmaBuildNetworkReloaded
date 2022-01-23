package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.connection;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

public class ConnectionFactory {
    private final HikariDataSource dataSource;

    private ConnectionFactory(FileConfiguration config) {
        this.dataSource = new HikariDataSource();
        String address = config.getString("mysql.host", "localhost");
        int port = config.getInt("mysql.port", 3306);
        String dbName = config.getString("mysql.name", "minecraft");
        dataSource.setJdbcUrl(MessageFormat.format("jdbc:mysql://{0}:{1}/{2}", address, port, dbName));
        dataSource.setMaximumPoolSize(10);
        dataSource.setUsername(config.getString("mysql.user", "mc"));
        dataSource.setPassword(config.getString("mysql.password", "password"));
        dataSource.setPoolName("mbn - hikari");
        dataSource.addDataSourceProperty("useSSL", false);
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    }

    public static ConnectionFactory fromConfiguration(FileConfiguration config) {
        return new ConnectionFactory(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() {
        dataSource.close();
    }
}
