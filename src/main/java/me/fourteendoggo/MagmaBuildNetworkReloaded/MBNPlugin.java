package me.fourteendoggo.MagmaBuildNetworkReloaded;

import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.DelegatingStorage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Storage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.StorageType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.connection.ConnectionFactory;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl.MySqlUserStorage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class MBNPlugin extends JavaPlugin {
    private static MBNPlugin instance;
    private ConnectionFactory connectionFactory;
    private DelegatingStorage<UUID, User> userStorage;

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.RED + "Initializing...");
        instance = this;
        saveDefaultConfig();
        connectionFactory = ConnectionFactory.fromConfiguration(getConfig());
        userStorage = createUserStorage();
    }

    private DelegatingStorage<UUID, User> createUserStorage() {
        StorageType type = StorageType.fromString(getConfig().getString("storage-type", "MySQL"), StorageType.MYSQL);
        if (type == StorageType.MYSQL) {
            return new DelegatingStorage<>(new MySqlUserStorage(connectionFactory), getLogger());
        } else if (type == StorageType.YAML) {

        }
    }

    public static MBNPlugin getInstance() {
        return instance;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
