package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import com.zaxxer.hikari.HikariDataSource;
import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

public class ConnectionFactory {
    private final HikariDataSource dataSource;
    private final StorageType type;

    private ConnectionFactory(MBNPlugin plugin, StorageType type) {
        this.type = type;
        dataSource = new HikariDataSource();
        String jdbcUrl;
        if (type == StorageType.MYSQL) {
            String address = plugin.getConfig().getString("database.host", "localhost");
            int port = plugin.getConfig().getInt("database.port", 3306);
            String dbName = plugin.getConfig().getString("database.name", "minecraft");
            jdbcUrl = MessageFormat.format("jdbc:mysql://{0}:{1}/{2}", address, port, dbName);
            dataSource.setUsername(plugin.getConfig().getString("database.user", "mc"));
            dataSource.setPassword(plugin.getConfig().getString("database.password", "password"));
        } else { // h2 fallback, file creation is handled by either h2 or hikari itself
            File file = new File(plugin.getDataFolder(), "database.h2");
            jdbcUrl = "jdbc:h2:file:" + file.getAbsolutePath();
            dataSource.setDriverClassName("org.h2.Driver");
        } // TODO add support for other databases
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("mbn - hikari pool");
        dataSource.setConnectionTestQuery("SELECT 1");
        dataSource.addDataSourceProperty("useSSL", false);
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    }

    public static ConnectionFactory create(MBNPlugin plugin, StorageType type) {
        return new ConnectionFactory(plugin, type);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public StorageType getStorageType() {
        return type;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) { // to prevent (further) exceptions
            dataSource.close();
        }
    }
}
