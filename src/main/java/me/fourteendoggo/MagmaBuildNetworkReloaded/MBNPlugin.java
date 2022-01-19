package me.fourteendoggo.MagmaBuildNetworkReloaded;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MBNPlugin extends JavaPlugin {
    private static MBNPlugin instance;
    private final Logger logger = getLogger();

    @Override
    public void onEnable() {
        instance = this;
        logger.info("Initializing...");
    }

    public static MBNPlugin getInstance() {
        return instance;
    }
}
