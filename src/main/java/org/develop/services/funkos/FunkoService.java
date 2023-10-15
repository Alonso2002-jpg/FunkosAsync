package org.develop.services.funkos;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;
import org.develop.model.Funko;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Interfaz que define operaciones relacionadas con objetos Funko, como buscar, guardar, actualizar, borrar y realizar copias de seguridad.
 */
public interface FunkoService {

    /**
     * Busca y devuelve todos los objetos Funko en la fuente de datos.
     *
     * @return Un CompletableFuture que contendra una lista de objetos Funko.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws ExecutionException Si ocurre un error durante la ejecucion de la operacion asincronica.
     * @throws InterruptedException Si la operacion asincronica es interrumpida.
     */
    CompletableFuture<List<Funko>> findAll() throws SQLException, ExecutionException, InterruptedException;

    /**
     * Busca y devuelve todos los objetos Funko con un nombre que contiene la cadena especificada.
     *
     * @param nombre El nombre o parte del nombre a buscar.
     * @return Un CompletableFuture que contendra una lista de objetos Funko que coincidan con el nombre.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws ExecutionException Si ocurre un error durante la ejecucion de la operacion asincronica.
     * @throws InterruptedException Si la operacion asincronica es interrumpida.
     */
    CompletableFuture<List<Funko>> findAllByNombre(String nombre) throws SQLException, ExecutionException, InterruptedException;

    /**
     * Busca un objeto Funko por su identificador unico.
     *
     * @param id El identificador unico del objeto Funko a buscar.
     * @return Un CompletableFuture que contendra un Optional con el objeto Funko si se encuentra, o un Optional vacio si no se encuentra.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws ExecutionException Si ocurre un error durante la ejecucion de la operacion asincronica.
     * @throws InterruptedException Si la operacion asincronica es interrumpida.
     * @throws FunkoNotFoundException Si el objeto Funko no se encuentra en la fuente de datos.
     */
    CompletableFuture<Optional<Funko>> findById(int id) throws SQLException, ExecutionException, InterruptedException, FunkoNotFoundException;

    /**
     * Guarda un objeto Funko en la fuente de datos.
     *
     * @param funko El objeto Funko a guardar.
     * @return Un CompletableFuture que contendra el objeto Funko guardado.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws FunkoNotSaveException Si no se puede guardar el objeto Funko.
     * @throws ExecutionException Si ocurre un error durante la ejecucion de la operacion asincronica.
     * @throws InterruptedException Si la operacion asincrnoica es interrumpida.
     */
    CompletableFuture<Funko> save(Funko funko) throws SQLException, FunkoNotSaveException, ExecutionException, InterruptedException;

    /**
     * Actualiza un objeto Funko en la fuente de datos.
     *
     * @param funko El objeto Funko actualizado.
     * @return Un CompletableFuture que contendra el objeto Funko actualizado.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws FunkoNotFoundException Si el objeto Funko no se encuentra en la fuente de datos.
     * @throws ExecutionException Si ocurre un error durante la ejecucion de la operacion asincronica.
     * @throws InterruptedException Si la operacion asincronica es interrumpida.
     */
    CompletableFuture<Funko> update(Funko funko) throws SQLException, FunkoNotFoundException, ExecutionException, InterruptedException;

    /**
     * Borra un objeto Funko de la fuente de datos por su identificador inico.
     *
     * @param id El identificador unico del objeto Funko a borrar.
     * @return Un CompletableFuture que contendra `true` si se elimino correctamente, o `false` si no se pudo encontrar el objeto.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws FunkoNotFoundException Si el objeto Funko no se encuentra en la fuente de datos.
     * @throws ExecutionException Si ocurre un error durante la ejecucion de la operacion asincronica.
     * @throws InterruptedException Si la operacion asincronica es interrumpida.
     */
    CompletableFuture<Boolean> deleteById(int id) throws SQLException,FunkoNotFoundException ,ExecutionException, InterruptedException;

/**
 * Borra todos los objetos Funko de la fuente de datos.
 *
 * @return Un CompletableFuture que se completara una vez que se hayan eliminado todos los objetos Funko.
 * ocurrira un error al interactuar con la base de datos.
 * @throws ExecutionException Si ocurre un error durante la ejecucion de la operacion asincronica.
 * @throws InterruptedException Si la operacion asincronica es interrumpida.
 */
     CompletableFuture<Void> deleteAll() throws SQLException, ExecutionException, InterruptedException;

    /**
     * Realiza una copia de seguridad de los objetos Funko en un archivo.
     *
     * @param file El nombre del archivo donde se realizara la copia de seguridad.
     * @return Un CompletableFuture que contendra `true` si la copia de seguridad se completo con exito, o `false` si ocurrio algun error.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws ExecutionException Si ocurre un error durante la ejecucion de la operacion asincronica.
     * @throws InterruptedException Si la operacion asincronica es interrumpida.
     */
    CompletableFuture<Boolean> backup(String file) throws SQLException, ExecutionException, InterruptedException;

    /**
     * Importa objetos Funko desde un archivo de copia de seguridad.
     *
     * @param file El nombre del archivo desde donde se importaran los objetos Funko.
     * @return Un CompletableFuture que contendra una lista de objetos Funko importados.
     * @throws InterruptedException Si la operacion asincronica es interrumpida.
     */
    CompletableFuture<List<Funko>> imported(String file) throws InterruptedException;
}
