package me.fourteendoggo.MagmaBuildNetworkReloaded;

import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.HomeCommand;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.VanishCommand;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.BaseRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.DelegatingStorage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Storage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.StorageType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.connection.ConnectionFactory;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl.SqlStorage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import org.bukkit.plugin.java.JavaPlugin;

public class MBNPlugin extends JavaPlugin {
    private DelegatingStorage storage;
    private BaseRepository baseRepository;

    @Override
    public void onEnable() {
        getLogger().info("Initializing...");
        saveDefaultConfig();
        Lang.initialize(this);
        StorageType type = StorageType.fromString(getConfig().getString("database.type"), StorageType.H2);
        getLogger().info("Using " + type.getName() + " storage");
        ConnectionFactory connectionFactory = ConnectionFactory.create(this, type);
        storage = new DelegatingStorage(getStorage(type, connectionFactory));
        storage.initialize();
        baseRepository = new BaseRepository(this);
        new HomeCommand(this);
        new VanishCommand(this);
    }

    private Storage getStorage(StorageType type, ConnectionFactory connectionFactory) {
        switch (type) {
            case H2:
            case MYSQL:
                return new SqlStorage(connectionFactory, getLogger());
        }
        return null;
    }

    @Override
    public void onDisable() {
        storage.close();
    }

    public DelegatingStorage getStorage() {
        return storage;
    }

    public BaseRepository getBaseRepository() {
        return baseRepository;
    }
}
