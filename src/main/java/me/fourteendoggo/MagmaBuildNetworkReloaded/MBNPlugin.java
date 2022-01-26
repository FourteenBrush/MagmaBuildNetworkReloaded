package me.fourteendoggo.MagmaBuildNetworkReloaded;

import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.DelegatingStorage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.StorageType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.connection.ConnectionFactory;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl.MySqlStorage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class MBNPlugin extends JavaPlugin {
    private static MBNPlugin instance;
    private DelegatingStorage storage;

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.RED + "Initializing...");
        instance = this;
        saveDefaultConfig();
        Lang.initialize(this);
        StorageType type = StorageType.fromString(getConfig().getString("database.type"), StorageType.H2);
        getLogger().info(ChatColor.RED + "Using " + type.getName() + " database for storage");
        ConnectionFactory connectionFactory = ConnectionFactory.create(this, type);
        storage = new DelegatingStorage(new MySqlStorage(connectionFactory, getLogger()), getLogger());
        storage.initialize();
    }

    @Override
    public void onDisable() {
        storage.close();
    }

    public static MBNPlugin getInstance() {
        return instance;
    }

    public DelegatingStorage getStorage() {
        return storage;
    }
}
