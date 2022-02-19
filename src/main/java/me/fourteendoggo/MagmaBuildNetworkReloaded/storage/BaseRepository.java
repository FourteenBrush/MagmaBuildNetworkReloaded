package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.HomeRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.KingdomRepository;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache.UserRepository;

public class BaseRepository {
    private final HomeRepository homeRepository;
    private final UserRepository userRepository;
    private final KingdomRepository kingdomRepository;

    public BaseRepository() {
        homeRepository = new HomeRepository();
        userRepository = new UserRepository();
        kingdomRepository = new KingdomRepository();
    }

    public HomeRepository getHomeRepository() {
        return homeRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public KingdomRepository getKingdomRepository() {
        return kingdomRepository;
    }
}
