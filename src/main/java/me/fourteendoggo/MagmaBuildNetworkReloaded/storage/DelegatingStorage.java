package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class DelegatingStorage<K, V> {
    private final Logger logger;
    private final Storage<K, V> storage;
    private final Executor executor = new ForkJoinPool(
            Runtime.getRuntime().availableProcessors() * 2,
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            (t, e) -> e.printStackTrace(),
            false);

    public DelegatingStorage(Storage<K, V> storage, Logger logger) {
        this.storage = storage;
        this.logger = logger;
    }

    public StorageType getStorageType() {
        return storage.getStorageType();
    }

    public void init() {
        try {
            storage.init();
        } catch (Exception e) {
            logger.severe("Failed to initialize storage");
            e.printStackTrace();
        }
    }

    public V load(K id) {
        return null;
    }

    public void save(V data) {

    }

    public void close() {
        storage.close();
    }


    private <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    private <T> CompletableFuture<Void> makeFuture(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
}
