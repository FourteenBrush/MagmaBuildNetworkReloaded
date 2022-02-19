package me.fourteendoggo.MagmaBuildNetworkReloaded.storage;

public interface Cache<K, V> extends Iterable<V> {

    V get(K key);

    boolean contains(K key);

    void cache(K key, V data);

    void remove(K key);

    void removeByValue(V value);

    void clear();

    int cleanup();

    int size();
}
