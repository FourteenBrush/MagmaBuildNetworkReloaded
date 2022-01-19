package me.fourteendoggo.MagmaBuildNetworkReloaded.user;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {
    private final MBNPlugin plugin;
    private final Map<UUID, User> userCache;

    public UserManager(MBNPlugin plugin) {
        this.plugin = plugin;
        this.userCache = new HashMap<>();
    }
}
