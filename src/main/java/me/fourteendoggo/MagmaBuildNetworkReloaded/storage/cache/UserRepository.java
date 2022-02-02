package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache;

import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Cache;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UserRepository implements Cache<UUID, User> {
    private final Map<UUID, User> userMap;

    public UserRepository() {
        userMap = new HashMap<>();
    }

    @Override
    public Optional<User> get(UUID key) {
        return Optional.ofNullable(userMap.get(key));
    }

    @Override
    public boolean has(UUID key) {
        return userMap.containsKey(key);
    }

    @Override
    public void cache(UUID key, User data) {
        cache(key, data, false);
    }

    @Override
    public void cache(UUID key, User data, boolean overrideOlder) {
        if (overrideOlder) {
            userMap.put(key, data);
        } else {
            userMap.putIfAbsent(key, data);
        }
    }

    @Override
    public void remove(UUID key) {
        userMap.remove(key);
    }

    @Override
    public void removeByValue(User value) {
        userMap.entrySet().removeIf(entry -> entry.equals(value));
    }

    @Override
    public void clear() {
        userMap.clear();
    }

    @Override
    public int cleanup() {
        int sizeBefore = size();
        userMap.entrySet().removeIf(entry -> !entry.getValue().isOnline());
        return size() - sizeBefore;
    }

    @Override
    public int size() {
        return userMap.size();
    }

    @NotNull
    @Override
    public Iterator<User> iterator() {
        return userMap.values().iterator();
    }
}
