package org.develop.services.cache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Cache <K,V>{

    CompletableFuture<Void> put(K key, V value);
    CompletableFuture<Optional<V>> get(K key);
    CompletableFuture<Void> remove(K key);
    CompletableFuture<Void> clear();
    CompletableFuture<Void> shutdown();
}
