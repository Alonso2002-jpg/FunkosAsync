package org.develop.services.cache;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Interfaz que define las operaciones basicas de una cache generica.
 *
 * @param <K> Tipo de clave.
 * @param <V> Tipo de valor.
 */
public interface Cache <K,V>{

    /**
     * Almacena un valor en la cache asociado a una clave.
     *
     * @param key   La clave que se utilizara para almacenar el valor en la cache.
     * @param value El valor que se va a almacenar en la cache.
     * @return Un CompletableFuture<Void> que se completa cuando la operacion de almacenamiento ha tenido exito.
     */
    CompletableFuture<Void> put(K key, V value);

    /**
     * Recupera un valor de la cache asociado a una clave.
     *
     * @param key La clave que se utilizara para buscar el valor en la cache.
     * @return Un CompletableFuture que contendra un Optional que puede contener el valor asociado a la clave, si se encuentra en la cache.
     */
    CompletableFuture<Optional<V>> get(K key);
    /**
     * Elimina un valor de la cache asociado a una clave.
     *
     * @param key La clave que se utilizara para eliminar el valor de la cache.
     * @return Un CompletableFuture<Void> que se completa cuando la operacion de eliminacion ha tenido exito.
     */
    CompletableFuture<Void> remove(K key);
    /**
     * Elimina todos los valores almacenados en la cache, dejandola vacia.
     *
     * @return Un CompletableFuture<Void> que se completa cuando la operacion de eliminacion de todos los valores ha tenido exito.
     */
    CompletableFuture<Void> clear();


    /**
     * Detiene y cierra la cache, liberando recursos y finalizando cualquier operacion pendiente.
     *
     * @return Un CompletableFuture<Void> que se completa cuando la operacion de cierre de la cache ha tenido exito.
     */
    CompletableFuture<Void> shutdown();
}
