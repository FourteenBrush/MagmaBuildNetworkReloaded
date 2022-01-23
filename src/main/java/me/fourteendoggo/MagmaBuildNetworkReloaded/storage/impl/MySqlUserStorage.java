package me.fourteendoggo.MagmaBuildNetworkReloaded.storage.impl;

import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Cache;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.Storage;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.StorageType;
import me.fourteendoggo.MagmaBuildNetworkReloaded.storage.connection.ConnectionFactory;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MySqlUserStorage implements Storage<UUID, User>, Cache<UUID, User> {
    private final ConnectionFactory connectionFactory;
    private final Map<UUID, User> userMap;

    public MySqlUserStorage(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.userMap = new HashMap<>();
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MYSQL;
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public User load(UUID id) {
        return null;
    }

    @Override
    public void save(User data) {

    }

    @Override
    public void close() {

    }

    @Override
    public Optional<User> get(UUID key) {
        return Optional.empty();
    }

    @Override
    public boolean has(UUID key) {
        return false;
    }

    @Override
    public void cache(UUID key, User data) {

    }

    @Override
    public void remove(UUID key) {

    }

    @Override
    public void removeByValue(User value) {

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
}
