package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache;

import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Cache;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UserRepository implements Cache<UUID, User> {
    private final Map<UUID, User> userMap = new HashMap<>();

    @Override
    public User get(UUID key) {
        return userMap.get(key);
    }

    public Optional<User> getOptional(UUID key) {
        return Optional.ofNullable(get(key));
    }

    @Override
    public boolean contains(UUID key) {
        return userMap.containsKey(key);
    }

    @Override
    public void cache(UUID key, User data) {
        userMap.put(key, data);
    }

    @Override
    public void remove(UUID key) {
        userMap.remove(key);
    }

    @Override
    public void removeByValue(User value) {
        userMap.entrySet().removeIf(entry -> entry.getValue().equals(value));
    }

    @Override
    public void clear() {
        userMap.clear();
    }

    @Override
    public int cleanup() {
        int sizeBefore = size();
        userMap.entrySet().removeIf(entry -> {
            Player player = Bukkit.getPlayer(entry.getKey());
            return player == null || !player.isOnline();
        });
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
