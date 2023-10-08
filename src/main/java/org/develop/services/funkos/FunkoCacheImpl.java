package org.develop.services.funkos;

import org.develop.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FunkoCacheImpl implements FunkoCache{
    private final Logger logger = LoggerFactory.getLogger(FunkoCacheImpl.class);
    private final int maxSize;
    private final Map<Integer,Funko> cache;
    private final ScheduledExecutorService cleaner;

    public FunkoCacheImpl(int maxSize){
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize,0.75f,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Funko> eldest) {
                return size() > maxSize;
            }
        };
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear,2,2, TimeUnit.MINUTES);
    }
    @Override
    public CompletableFuture<Void> put(Integer key, Funko value) {
       return CompletableFuture.runAsync(()->{
           logger.debug("Añadiendo Funko al Cache");
           cache.put(key,value);
       });
    }

    @Override
    public CompletableFuture<Funko> get(Integer key) {
        return CompletableFuture.supplyAsync(()->{
           logger.debug("Obteniendo Funko de la Cache");
            return cache.get(key);
       });
    }

    @Override
    public CompletableFuture<Void> remove(Integer key) {
        return CompletableFuture.runAsync(()->{
           logger.debug("Borrando Funko de la Cache");
           cache.remove(key);
       });
    }

    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.runAsync(()->{
           logger.debug("Limpiando la cache");
           cache.entrySet().removeIf(entry -> {
            boolean shouldRemove = entry.getValue().getUpdated_at().plusMinutes(1).isBefore(LocalDateTime.now());
            if (shouldRemove) {
                logger.debug("Autoeliminando por caducidad alumno de cache con id: " + entry.getKey());
            }
            return shouldRemove;
        });
       });
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.supplyAsync(()->{
           cleaner.shutdown();
            return null;
        });
    }
}