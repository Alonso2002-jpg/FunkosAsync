package org.develop.services.funkos;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;
import org.develop.model.Funko;
import org.develop.repositories.FunkoRepository;
import org.develop.services.files.BackupManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementacion de la interfaz FunkoService que realiza operaciones relacionadas con objetos Funko.
 */
public class FunkoServiceImpl implements FunkoService{
    private static final int CACHE_SIZE =10;

    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private static FunkoServiceImpl instance;
    private final FunkoCache cache;
    private final FunkoRepository funkoRepository;
    private final BackupManagerImpl backupManager;

    /**
     * Constructor privado para asegurar el patron Singleton.
     *
     * @param backupManager   Administrador de copias de seguridad.
     * @param funkoRepository Repositorio de Funko.
     */
    private FunkoServiceImpl(BackupManagerImpl backupManager, FunkoRepository funkoRepository){
        this.funkoRepository =funkoRepository;
        this.backupManager = backupManager;
        this.cache = new FunkoCacheImpl(CACHE_SIZE);
    }

    /**
     * Obtiene una instancia unica de FunkoServiceImpl utilizando el patron Singleton.
     *
     * @param backupManager   Administrador de copias de seguridad.
     * @param funkoRepository Repositorio de Funko.
     * @return Instancia de FunkoServiceImpl.
     */
    public synchronized static FunkoServiceImpl getInstance(BackupManagerImpl backupManager, FunkoRepository funkoRepository){
        if (instance == null){
            instance = new FunkoServiceImpl(backupManager,funkoRepository);
        }
        return instance;
    }


    /**
     * Obtiene todos los objetos Funko almacenados en el repositorio.
     *
     * @return Lista de objetos Funko.
     * @throws SQLException Excepcion de SQL en caso de error en la base de datos.
     */
    @Override
    public CompletableFuture<List<Funko>> findAll() throws SQLException {
        logger.debug("Obteniendo todos los funkos");
        return funkoRepository.findAll();
    }

    /**
     * Obtiene objetos Funko que coinciden con un nombre especifico.
     *
     * @param nombre Nombre para buscar coincidencias.
     * @return Lista de objetos Funko que coinciden con el nombre.
     */
    @Override
    public CompletableFuture<List<Funko>> findAllByNombre(String nombre) {
        logger.debug("Obteniendo los funkos que coincidan con: " + nombre);
        return funkoRepository.findByNombre(nombre);
    }


    /**
     * Busca un objeto Funko por su ID. Primero intenta obtenerlo de la cache y, si no lo encuentra,
     * lo obtiene del repositorio y lo almacena en la cache para futuras consultas.
     *
     * @param id ID del objeto Funko a buscar.
     * @return Objeto Funko si se encuentra, o un objeto Optional vacio si no.
     * @throws FunkoNotFoundException Excepcion si el Funko no se encuentra en la base de datos.
     * @throws ExecutionException   Excepcion de ejecucion en caso de error.
     * @throws InterruptedException Excepcion de interrupcióon en caso de error.
     * @throws SQLException          Excepcion de SQL en caso de error en la base de datos.
     */
    @Override
    public CompletableFuture<Optional<Funko>> findById(int id) throws FunkoNotFoundException, ExecutionException, InterruptedException, SQLException {
        logger.debug("Obteniendo el funko con Id: " + id);
        var fk = cache.get(id);
        if (fk != null && fk.get().isPresent()  ) {
            return fk;
        } else {
            return funkoRepository.findById(id);
        }

    }

    /**
     * Guarda un nuevo objeto Funko en el repositorio y lo almacena en la cache.
     *
     * @param funko Objeto Funko a guardar.
     * @return Objeto Funko guardado.
     * @throws FunkoNotSaveException Excepcion si el Funko no se puede guardar.
     * @throws SQLException          Excepcion de SQL en caso de error en la base de datos.
     * @throws ExecutionException   Excepcion de ejecucion en caso de error.
     * @throws InterruptedException Excepcion de interrupcion en caso de error.
     */
    @Override
    public CompletableFuture<Funko> save(Funko funko) throws FunkoNotSaveException, SQLException, ExecutionException, InterruptedException {
        logger.debug("Guardando Funko en la base de datos: " + funko);
        var funk = funkoRepository.save(funko);
        cache.put(funko.getId(),funko);
        return funk;
    }

    /**
     * Actualiza un objeto Funko en el repositorio y actualiza la cache.
     *
     * @param funko Objeto Funko a actualizar.
     * @return Objeto Funko actualizado.
     * @throws FunkoNotFoundException Excepcion si el Funko no se encuentra en la base de datos.
     * @throws SQLException          Excepcion de SQL en caso de error en la base de datos.
     */
    @Override
    public CompletableFuture<Funko> update(Funko funko) throws FunkoNotFoundException, SQLException {
        logger.debug("Actualizando Funko....");
        var funk = funkoRepository.update(funko);
        cache.put(funko.getId(),funko);
        return funk;
    }

    /**
     * Elimina un objeto Funko por su ID del repositorio y de la cache, si existe.
     *
     * @param id ID del objeto Funko a eliminar.
     * @return Un valor booleano que indica si la eliminacion tuvo exito.
     * @throws FunkoNotFoundException Excepcion si el Funko no se encuentra en la base de datos.
     * @throws SQLException          Excepcion de SQL en caso de error en la base de datos.
     * @throws ExecutionException   Excepcion de ejecucion en caso de error.
     * @throws InterruptedException Excepcion de interrupcion en caso de error.
     */
    @Override
    public CompletableFuture<Boolean> deleteById(int id) throws FunkoNotFoundException, SQLException, ExecutionException, InterruptedException {
        logger.debug("Eliminando Funko con id: " + id);
        var res = funkoRepository.deleteById(id);
        if (res.get()) {
            cache.remove(id);
        }
        return res;
    }

    /**
     * Elimina todos los objetos Funko de la base de datos y limpia la cache.
     *
     * @return Una tarea CompletableFuture sin valor que indica que la operacion se ha completado.
     */
    @Override
    public CompletableFuture<Void> deleteAll(){
       return CompletableFuture.runAsync(()->{
            try {
                logger.debug("Eliminando todos los elementos de la Base de Datos....");
                funkoRepository.deleteAll().get();
                cache.clear();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Realiza una copia de seguridad de la base de datos almacenando los Funkos en un archivo.
     *
     * @param file Nombre del archivo de copia de seguridad.
     * @return Un valor booleano que indica si la copia de seguridad se realizo con exito.
     * @throws SQLException          Excepcion de SQL en caso de error en la base de datos.
     * @throws ExecutionException   Excepcion de ejecucion en caso de error.
     * @throws InterruptedException Excepcion de interrupcion en caso de error.
     */
        @Override
    public CompletableFuture<Boolean> backup(String file) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Iniciando Backup de la Base de Datos......");
        var suc = backupManager.writeFileFunko(file,findAll().get());
        logger.debug("Backup Realizado Correctamente!");
        return suc;
    }

    /**
     * Importa los Funkos desde un archivo de copia de seguridad.
     *
     * @param file Nombre del archivo de copia de seguridad.
     * @return Una lista de objetos Funko importados desde el archivo.
     * @throws InterruptedException Excepcion de interrupcion en caso de error.
     */
    @Override
    public CompletableFuture<List<Funko>> imported(String file) throws InterruptedException {
        return backupManager.readFileFunko(file);
    }
}
