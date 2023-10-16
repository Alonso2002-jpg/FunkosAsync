package org.develop.services.files;

import org.develop.model.Funko;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interfaz que define las operaciones para realizar copias de seguridad de objetos de tipo T.
 *
 * @param <T> El tipo de objeto que se va a respaldar.
 */
public interface BackupManager <T>{

    /**
     * Guarda una lista de objetos en un archivo de respaldo.
     *
     * @param nomFile El nombre del archivo en el que se guardaran los objetos.
     * @param funks   La lista de objetos que se va a respaldar.
     * @return Un CompletableFuture<Boolean> que indica si la operacion de escritura del archivo fue exitosa (true) o no (false).
     */
    CompletableFuture<Boolean> writeFileFunko(String nomFile, List<T> funks);

    /**
     * Lee una lista de objetos desde un archivo de respaldo.
     *
     * @param nomFile El nombre del archivo desde el cual se leeran los objetos.
     * @return Un CompletableFuture que contendra una lista de objetos de tipo T leidos desde el archivo de respaldo.
     * @throws InterruptedException Si la operacion de lectura es interrumpida.
     */
    CompletableFuture<List<Funko>> readFileFunko(String nomFile) throws InterruptedException;
}
