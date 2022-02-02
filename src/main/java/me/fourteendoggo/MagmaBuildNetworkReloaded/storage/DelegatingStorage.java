package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.User;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.Home;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

public class DelegatingStorage {
    private final Logger logger;
    private final Storage storage;
    private final Executor executor = new ForkJoinPool(
            Runtime.getRuntime().availableProcessors() * 2,
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            (t, e) -> e.printStackTrace(),
            false);

    public DelegatingStorage(Storage storage, Logger logger) {
        this.storage = storage;
        this.logger = logger;
    }

    public StorageType getStorageType() {
        return storage.getStorageType();
    }

    public void initialize() {
        try {
            storage.initialize();
        } catch (Exception e) {
            logger.severe("Failed to initialize storage");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            storage.close();
        } catch (Exception e) {
            logger.severe("Failed to close storage");
            e.printStackTrace();
        }
    }

    public CompletableFuture<@Nullable User> loadUser(UUID id) {
        return CompletableFuture.supplyAsync(() -> storage.loadUser(id), executor);
    }

    public CompletableFuture<Void> saveUser(User user) {
        return CompletableFuture.runAsync(() -> storage.saveUser(user), executor);
    }

    public CompletableFuture<@Nullable ChatChannel> loadChatChannel(String name) {
        return CompletableFuture.supplyAsync(() -> storage.loadChatChannel(name), executor);
    }

    public CompletableFuture<Void> saveChatChannel(ChatChannel channel) {
        return CompletableFuture.runAsync(() -> storage.saveChatChannel(channel), executor);
    }

    public CompletableFuture<Collection<Home>> loadHomes(UUID user, String name) {
        return CompletableFuture.supplyAsync(() -> storage.loadHomes(user, name), executor);
    }
}
