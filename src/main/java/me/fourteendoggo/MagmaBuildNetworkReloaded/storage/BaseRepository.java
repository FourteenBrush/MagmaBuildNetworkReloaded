package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.HomeRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.UserRepository;

public class BaseRepository {
    private final HomeRepository homeRepository;
    private final UserRepository userRepository;

    public BaseRepository(MBNPlugin plugin) {
        homeRepository = new HomeRepository(plugin);
        userRepository = new UserRepository();
    }

    public HomeRepository getHomeRepository() {
        return homeRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
