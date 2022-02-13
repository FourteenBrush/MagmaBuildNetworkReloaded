package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache;

import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Cache;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.NewUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class UserRepository implements Cache<UUID, NewUser> {
    private final Map<UUID, NewUser> userMap = new HashMap<>();

    @Override
    public Optional<NewUser> get(UUID key) {
        return Optional.ofNullable(userMap.get(key));
    }

    @Override
    public boolean contains(UUID key) {
        return userMap.containsKey(key);
    }

    @Override
    public void cache(UUID key, NewUser data) {
        userMap.put(key, data);
    }

    @Override
    public void remove(UUID key) {
        userMap.remove(key);
    }

    @Override
    public void removeByValue(NewUser value) {
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
    public Iterator<NewUser> iterator() {
        return userMap.values().iterator();
    }
}
