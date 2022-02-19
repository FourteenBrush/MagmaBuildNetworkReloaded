package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache;

import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Cache;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KingdomRepository implements Cache<String, Kingdom> {
    private final Map<String, Kingdom> kingdomMap;

    public KingdomRepository() {
        kingdomMap = new HashMap<>();
    }

    @Override
    public Kingdom get(String key) {
        return kingdomMap.get(key);
    }

    @Override
    public boolean contains(String key) {
        return kingdomMap.containsKey(key);
    }

    @Override
    public void cache(String key, Kingdom data) {

    }

    @Override
    public void remove(String key) {
        kingdomMap.remove(key);
    }

    @Override
    public void removeByValue(Kingdom value) {
        kingdomMap.entrySet().removeIf(entry -> entry.getValue().equals(value));
    }

    @Override
    public void clear() {
        kingdomMap.clear();
    }

    @Override
    public int cleanup() {
        return 0;
    }

    @Override
    public int size() {
        return kingdomMap.size();
    }

    @NotNull
    @Override
    public Iterator<Kingdom> iterator() {
        return kingdomMap.values().iterator();
    }
}
