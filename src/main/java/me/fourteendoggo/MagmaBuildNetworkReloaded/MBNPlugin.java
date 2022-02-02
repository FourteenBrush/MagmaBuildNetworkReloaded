package me.fourteendoggo.MagmaBuildNetworkReloaded;

import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.DemoCommand;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.DelegatingStorage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.StorageType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.connection.ConnectionFactory;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl.MySqlStorage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import org.bukkit.plugin.java.JavaPlugin;

public class MBNPlugin extends JavaPlugin {
    private DelegatingStorage storage;

    @Override
    public void onEnable() {
        getLogger().info("Initializing...");
        saveDefaultConfig();
        Lang.initialize(this);
        StorageType type = StorageType.fromString(getConfig().getString("database.type"), StorageType.H2);
        getLogger().info("Using " + type.getName() + " storage");
        ConnectionFactory connectionFactory = ConnectionFactory.create(this, type);
        // todo type implementation
        storage = new DelegatingStorage(new MySqlStorage(connectionFactory, getLogger()), getLogger());
        storage.initialize();
        new DemoCommand(this);
    }

    @Override
    public void onDisable() {
        storage.close();
    }

    public DelegatingStorage getStorage() {
        return storage;
    }
}
