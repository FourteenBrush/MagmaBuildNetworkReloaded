package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.cache;

import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Cache;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Home;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

public class HomeRepository implements Cache<Pair<UUID, String>, Home> {

    @Override
    public Optional<Home> get(Pair<UUID, String> key) {
        return Optional.empty();
    }

    @Override
    public boolean has(Pair<UUID, String> key) {
        return false;
    }

    @Override
    public void cache(Pair<UUID, String> key, Home data) {

    }

    @Override
    public void cache(Pair<UUID, String> key, Home data, boolean overrideOlder) {

    }

    @Override
    public void remove(Pair<UUID, String> key) {

    }

    @Override
    public void removeByValue(Home value) {

    }

    @Override
    public void clear() {

    }

    @Override
    public int cleanup() {
        return 0;
    }

    @Override
    public int size() {
        return 0;
    }

    @NotNull
    @Override
    public Iterator<Home> iterator() {
        return null;
    }
}
