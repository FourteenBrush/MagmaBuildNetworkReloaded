package me.fourteendoggo.MagmaBuildNetworkReloaded.utils;

@FunctionalInterface
public interface ThrowingSupplier<T> {

    T get() throws Exception;
}
