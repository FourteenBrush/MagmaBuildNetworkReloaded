package me.fourteendoggo.MagmaBuildNetworkReloaded;

import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.HomeCommand;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.PlaytimeCommand;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.UserinfoCommand;
import me.fourteendoggo.MagmaBuildNetworkReloaded.commands.VanishCommand;
import me.fourteendoggo.MagmaBuildNetworkReloaded.listeners.PlayerListener;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.*;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl.SqlStorage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Lang;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MBNPlugin extends JavaPlugin {
    private DelegatingStorage storage;
    private final Settings settings = new Settings();
    private final RemoteDataCache remoteDataCache = new RemoteDataCache();

    @Override
    public void onEnable() {
        getLogger().info("Initializing...");
        saveDefaultConfig();
        Lang.initialize(this);
        StorageType type = StorageType.fromString(getConfig().getString("database.type"), StorageType.H2);
        Storage impl = switch (type) {
            case H2, MYSQL -> new SqlStorage(ConnectionFactory.create(this, type));
        };
        storage = new DelegatingStorage(impl, getLogger());
        storage.initialize();
        remoteDataCache.startSaveTask(this);
        getLogger().info("Using " + type.getDescription() + " for storage");
        new HomeCommand(this).register("home", true);
        new VanishCommand(this).register("vanish", true);
        new PlaytimeCommand(this).register("playtime", false);
        new UserinfoCommand(this).register("userinfo", true);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        getLogger().info("Finished initialisation");
    }

    @Override
    public void onDisable() {
        storage.close();
        Bukkit.getScheduler().cancelTasks(this); // explicit call
    }

    public DelegatingStorage getStorage() {
        return storage;
    }

    public Settings getSettings() {
        return settings;
    }

    public RemoteDataCache getData() {
        return remoteDataCache;
    }
}
