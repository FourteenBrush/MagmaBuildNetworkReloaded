package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

public interface Storage<K, V> {

    StorageType getStorageType();

    void init() throws Exception;

    V load(K id);

    void save(V data);

    void close();
}
