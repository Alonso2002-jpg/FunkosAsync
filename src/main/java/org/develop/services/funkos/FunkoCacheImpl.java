package org.develop.services.funkos;

import lombok.Getter;
import org.develop.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementacion de una cache para objetos Funko con capacidad limitada y limpieza automatica de elementos antiguos.
 * Utiliza un mapa interno para almacenar los objetos Funko.
 */
public class FunkoCacheImpl implements FunkoCache{
    private final Logger logger = LoggerFactory.getLogger(FunkoCacheImpl.class);
    @Getter
    private final int maxSize;
    @Getter
    private final Map<Integer,Funko> cache;
    @Getter
    private final ScheduledExecutorService cleaner;

    /**
     * Crea una nueva instancia de FunkoCacheImpl con un tamano maximo especificado.
     *
     * @param maxSize El tamano maximo de la cache.
     */
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

    /**
     * Anade un objeto Funko a la cache.
     *
     * @param key   La clave asociada al objeto Funko.
     * @param value El objeto Funko a ser almacenado en la cache.
     * @return Un CompletableFuture que se completa cuando la operacion de anadir ha terminado.
     */
    @Override
    public CompletableFuture<Void> put(Integer key, Funko value) {
       return CompletableFuture.runAsync(()->{
           logger.debug("Añadiendo Funko al Cache");
           cache.put(key,value);
       });
    }


    /**
     * Obtiene un objeto Funko de la cache utilizando su clave.
     *
     * @param key La clave del objeto Funko a obtener.
     * @return Un CompletableFuture que contendra un Optional con el objeto Funko si se encuentra en la cache, o un Optional vacío si no se encuentra.
     */
    @Override
    public CompletableFuture<Optional<Funko>> get(Integer key) {
        return CompletableFuture.supplyAsync(()->{
           logger.debug("Obteniendo Funko de la Cache");
           if (cache.get(key) != null){
            return Optional.of(cache.get(key));
           }
           return Optional.empty();
       });
    }

    /**
     * Borra un objeto Funko de la cache utilizando su clave.
     *
     * @param key La clave del objeto Funko a borrar.
     * @return Un CompletableFuture que se completa cuando la operacion de borrado ha terminado.
     */
    @Override
    public CompletableFuture<Void> remove(Integer key) {
        return CompletableFuture.runAsync(()->{
           logger.debug("Borrando Funko de la Cache");
           cache.remove(key);
       });
    }

    /**
     * Limpia la cache de objetos Funko eliminando los elementos caducados automaticamente.
     *
     * @return Un CompletableFuture que se completa cuando la operacion de limpieza ha terminado.
     */
    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.runAsync(()->{
           logger.debug("Limpiando la cache");
           cache.entrySet().removeIf(entry -> {
            boolean shouldRemove = entry.getValue().getUpdated_at().plusMinutes(1).isBefore(LocalDateTime.now());
            if (shouldRemove) {
                logger.debug("Autoeliminando por caducidad Funko de cache con id: " + entry.getKey());
            }
            return shouldRemove;
        });
       });
    }

    /**
     * Detiene la tarea programada de limpieza de la cache.
     *
     * @return Un CompletableFuture que se completa cuando se ha detenido la tarea de limpieza.
     */
    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(cleaner::shutdown);
    }

}
