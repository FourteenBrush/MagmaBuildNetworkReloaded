package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.UserSnapshot;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DelegatingStorage {
    private final Logger logger;
    private final Storage storage;
    private final Executor executor;

    public DelegatingStorage(Storage storage, Logger logger) {
        this.storage = storage;
        this.logger = logger;
        executor = new ForkJoinPool(
                Runtime.getRuntime().availableProcessors() * 2,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                (t, e) -> e.printStackTrace(),
                false
        );
    }

    public void initialize() {
        try {
            storage.initialize();
        } catch (Exception e) {
            handleException(e, "Failed to initialize storage");
        }
    }

    public void close() {
        try {
            storage.close();
            logger.info("Closed storage");
        } catch (Exception e) {
            handleException(e, "Failed to close storage");
        }
    }

    public StorageType getStorageType() {
        return storage.getStorageType();
    }

    public CompletableFuture<UserSnapshot> loadUser(UUID id) {
        return makeFuture(() -> storage.loadUser(id), "Failed to load user " + id);
    }

    public CompletableFuture<Void> saveUser(UserSnapshot snapshot) {
        return makeFuture(() -> storage.saveUser(snapshot), "Failed to save user " + snapshot.getId());
    }

    public CompletableFuture<Void> createUser(UserSnapshot snapshot) {
        return makeFuture(() -> storage.createUser(snapshot), "Failed to create user " + snapshot.getId());
    }

    public CompletableFuture<ChatChannel> loadChannel(String name) {
        return makeFuture(() -> storage.loadChannel(name), "Failed to load chatchannel " + name);
    }

    public CompletableFuture<Void> saveChannel(ChatChannel channel) {
        return makeFuture(() -> storage.saveChannel(channel), "Failed to save chatchannel " + channel.getName());
    }

    public CompletableFuture<Void> createChannel(ChatChannel channel) {
        return makeFuture(() -> storage.createChannel(channel), "Failed to create channel " + channel.getName());
    }

    public CompletableFuture<Void> deleteChannel(String name) {
        return makeFuture(() -> storage.deleteChannel(name), "Failed to delete chatchannel " + name);
    }

    public CompletableFuture<Void> createHome(Home home) {
        return makeFuture(() -> storage.createHome(home), "Failed to create home " + home.name() + " for user " + home.owner());
    }

    public CompletableFuture<Void> deleteHome(Home home) {
        return makeFuture(() -> storage.deleteHome(home), "Failed to delete home " + home.name() + " for user " + home.owner());
    }

    public CompletableFuture<Kingdom> loadKingdom(String name) {
        return makeFuture(() -> storage.loadKingdom(name), "Failed to load kingdom " + name);
    }

    public CompletableFuture<Void> saveKingdom(Kingdom kingdom) {
        return makeFuture(() -> storage.saveKingdom(kingdom), "Failed to save kingdom " + kingdom.getName());
    }

    public CompletableFuture<Void> createKingdom(Kingdom kingdom) {
        return makeFuture(() -> storage.createKingdom(kingdom), "Failed to create kingdom " + kingdom.getName());
    }

    private CompletableFuture<Void> makeFuture(ThrowingRunnable runnable, String errorMessage) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                handleException(e, errorMessage);
                throw new CompletionException(e);
            }
        }, executor);
    }

    private <T> CompletableFuture<T> makeFuture(ThrowingSupplier<T> supplier, String errorMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                handleException(e, errorMessage);
                throw new CompletionException(e); // exception held internally in the future, handled with #whenComplete
            }
        }, executor);
    }

    private void handleException(Exception e, String errorMessage) {
        logger.log(Level.SEVERE, errorMessage + " Storage implementation is " + getStorageType().getDescription(), e);
    }

    @FunctionalInterface
    public interface ThrowingRunnable {

        void run() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {

        T get() throws Exception;
    }
}
