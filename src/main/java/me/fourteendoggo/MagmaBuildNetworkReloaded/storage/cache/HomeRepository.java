package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache;

import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Cache;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HomeRepository implements Cache<Pair<UUID, String>, Home> {
    private final Map<Pair<UUID, String>, Home> homesMap = new HashMap<>();

    @Override
    public Home get(Pair<UUID, String> key) {
        return homesMap.get(key);
    }

    public Set<Home> getAllFor(UUID id) {
        Set<Home> output = new HashSet<>();
        homesMap.values().forEach(home -> {
            if (home.owner().equals(id))
                output.add(home);
        });
        return output;
    }

    public Set<String> getAllNamesFor(UUID id) {
        Set<String> output = new HashSet<>();
        homesMap.values().forEach(home -> {
            if (home.owner().equals(id))
                output.add(home.name());
        });
        return output;
    }

    @Override
    public boolean contains(Pair<UUID, String> key) {
        return homesMap.containsKey(key);
    }

    @Override
    public void cache(Pair<UUID, String> key, Home data) {
        homesMap.put(key, data);
    }

    public void cacheAll(Collection<? extends Home> data) {
        data.forEach(home -> homesMap.put(new Pair<>(home.owner(), home.name()), home));
    }

    @Override
    public void remove(Pair<UUID, String> key) {
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
            Player player = Bukkit.getPlayer(entry.getKey().key());
            return player == null || !player.isOnline();
        });
        return size() - sizeBefore;
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
