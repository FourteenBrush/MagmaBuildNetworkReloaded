package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache;

import me.fourteendoggo.MagmaBuildNetworkReloaded.MBNPlugin;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Cache;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Entry;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Home;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HomeRepository implements Cache<Entry<UUID, String>, Home> {
    private final Map<Entry<UUID, String>, Home> homesMap;

    public HomeRepository(MBNPlugin plugin) {
        homesMap = new HashMap<>();
        Bukkit.getScheduler().runTaskTimer(plugin, this::cleanup, 20, 20 * 60 * 5); // cleanup every 5 minutes
    }

    @Override
    public Optional<Home> get(Entry<UUID, String> key) {
        return Optional.ofNullable(homesMap.get(key));
    }

    public Set<Home> getAllFor(UUID id) {
        Set<Home> homes = new HashSet<>();
        forEach(home -> {
            if (home.getOwner().equals(id))
                homes.add(home);
        });
        return homes;
    }

    public Set<String> getAllNamesFor(UUID id) {
        Set<String> homes = new HashSet<>();
        forEach(home -> {
            if (home.getOwner().equals(id)) {
                homes.add(home.getName());
            }
        });
        return homes;
    }

    @Override
    public boolean has(Entry<UUID, String> key) {
        return homesMap.containsKey(key);
    }

    @Override
    public void cache(Entry<UUID, String> key, Home data) {
        cache(key, data, false);
    }

    @Override
    public void cache(Entry<UUID, String> key, Home data, boolean overrideOlder) {
        if (overrideOlder) {
            homesMap.put(key, data);
        } else {
            homesMap.putIfAbsent(key, data);
        }
    }

    @Override
    public void remove(Entry<UUID, String> key) {
        homesMap.remove(key);
    }

    @Override
    public void removeByValue(Home value) {
        homesMap.entrySet().removeIf(entry -> entry.getValue().equals(value));
    }

    @Override
    public void clear() {
        homesMap.clear();
    }

    @Override
    public int cleanup() {
        int sizeBefore = size();
        homesMap.entrySet().removeIf(entry -> {
            Player player = Bukkit.getPlayer(entry.getKey().getKey());
            return player == null || !player.isOnline();
        });
        return sizeBefore - size();
    }

    @Override
    public int size() {
        return homesMap.size();
    }

    @NotNull
    @Override
    public Iterator<Home> iterator() {
        return homesMap.values().iterator();
    }
}
