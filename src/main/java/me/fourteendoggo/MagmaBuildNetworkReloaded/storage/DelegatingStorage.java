package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import me.fourteendoggo.MagmaBuildNetworkReloaded.chat.ChatChannel;
import me.fourteendoggo.MagmaBuildNetworkReloaded.kingdom.Kingdom;
import me.fourteendoggo.MagmaBuildNetworkReloaded.user.UserSnapshot;
import me.fourteendoggo.MagmaBuildNetworkReloaded.utils.records.Home;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class DelegatingStorage {
    private final Logger logger;
    private final Storage storage;
    private final Executor executor = new ForkJoinPool(
            Runtime.getRuntime().availableProcessors() * 2,
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            (t, e) -> e.printStackTrace(),
            false
    );

    public DelegatingStorage(Storage storage, Logger logger) {
        this.storage = storage;
        this.logger = logger;
    }

    public void initialize() {
        try {
            storage.initialize();
        } catch (Exception e) {
            exceptionHandler("Failed to initialize storage").accept(e);
        }
    }

    public void close() {
        try {
            storage.close();
        } catch (Exception e) {
            exceptionHandler("Failed to close storage").accept(e);
        }
    }

    public StorageType getType() {
        return storage.getType();
    }

    public CompletableFuture<UserSnapshot> loadUser(UUID id) {
        return makeFuture(() -> storage.loadUser(id),
                exceptionHandler("Failed to load user " + id));
    }

    public CompletableFuture<Void> saveUser(UserSnapshot snapshot) {
        return makeFuture(() -> storage.saveUser(snapshot),
                exceptionHandler("Failed to save user " + snapshot.statisticsProfile().getId()));
    }

    public CompletableFuture<Void> createUser(UserSnapshot snapshot) {
        return makeFuture(() -> storage.createUser(snapshot),
                exceptionHandler("Failed to create user " + snapshot.statisticsProfile().getId()));
    }

    public CompletableFuture<ChatChannel> loadChannel(String name) {
        return makeFuture(() -> storage.loadChannel(name),
                exceptionHandler("Failed to load chatchannel " + name));
    }

    public CompletableFuture<Void> saveChannel(ChatChannel channel) {
        return makeFuture(() -> storage.saveChannel(channel),
                exceptionHandler("Failed to save chatchannel " + channel.getName()));
    }

    public CompletableFuture<Void> createChannel(ChatChannel channel) {
        return makeFuture(() -> storage.createChannel(channel),
                exceptionHandler("Failed to create channel " + channel.getName()));
    }

    public CompletableFuture<Void> deleteChannel(String name) {
        return makeFuture(() -> storage.deleteChannel(name),
                exceptionHandler("Failed to delete chatchannel " + name));
    }

    public CompletableFuture<Collection<Home>> loadHomes(UUID owner) {
        return makeFuture(() -> storage.loadHomes(owner),
                exceptionHandler("Failed to load homes for user " + owner));
    }

    public CompletableFuture<Void> createHome(Home home) {
        return makeFuture(() -> storage.createHome(home),
                exceptionHandler("Failed to create home " + home.name() + " for owner " + home.owner()));
    }

    public CompletableFuture<Void> deleteHome(Home home) {
        return makeFuture(() -> storage.deleteHome(home),
                exceptionHandler("Failed to delete home " + home.name() + " for owner " + home.owner()));
    }

    public CompletableFuture<Kingdom> loadKingdom(String name) {
        return makeFuture(() -> storage.loadKingdom(name),
                exceptionHandler("Failed to load kingdom " + name));
    }

    public CompletableFuture<Void> saveKingdom(Kingdom kingdom) {
        return makeFuture(() -> storage.saveKingdom(kingdom),
                exceptionHandler("Failed to save kingdom " + kingdom.getName()));
    }

    public CompletableFuture<Void> createKingdom(Kingdom kingdom) {
        return makeFuture(() -> storage.createKingdom(kingdom),
                exceptionHandler("Failed to create kingdom " + kingdom.getName()));
    }

    private Consumer<Exception> exceptionHandler(String logMessage) {
        return e -> {
            logger.severe(logMessage);
            logger.severe("Storage implementation is " + getType().getDescription());
            e.printStackTrace();
        };
    }

    private CompletableFuture<Void> makeFuture(Runnable runnable, Consumer<Exception> exceptionConsumer) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                exceptionConsumer.accept(e);
            }
        }, executor);
    }

    private <T> CompletableFuture<T> makeFuture(Supplier<T> supplier, Consumer<Exception> exceptionConsumer) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                exceptionConsumer.accept(e);
                return null;
            }
        }, executor);
    }
}
