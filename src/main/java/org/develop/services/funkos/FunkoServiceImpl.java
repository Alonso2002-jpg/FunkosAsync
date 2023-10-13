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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FunkoServiceImpl implements FunkoService{
    private static final int CACHE_SIZE =10;

    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private static FunkoServiceImpl instance;
    private final FunkoCache cache;
    private final FunkoRepository funkoRepository;
    private final BackupManagerImpl backupManager;

    private FunkoServiceImpl(BackupManagerImpl backupManager, FunkoRepository funkoRepository){
        this.funkoRepository =funkoRepository;
        this.backupManager = backupManager;
        this.cache = new FunkoCacheImpl(CACHE_SIZE);
    }
    public synchronized static FunkoServiceImpl getInstance(BackupManagerImpl backupManager, FunkoRepository funkoRepository){
        if (instance == null){
            instance = new FunkoServiceImpl(backupManager,funkoRepository);
        }
        return instance;
    }
    @Override
    public CompletableFuture<List<Funko>> findAll() throws SQLException {
        logger.debug("Obteniendo todos los funkos");
        return funkoRepository.findAll();
    }

    @Override
    public CompletableFuture<List<Funko>> findAllByNombre(String nombre) {
        logger.debug("Obteniendo los funkos que coincidan con: " + nombre);
        return funkoRepository.findByNombre(nombre);
    }

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

    @Override
    public CompletableFuture<Funko> save(Funko funko) throws FunkoNotSaveException, SQLException, ExecutionException, InterruptedException {
        logger.debug("Guardando Funko en la base de datos: " + funko);
        var funk = funkoRepository.save(funko);
        cache.put(funk.get().getId(), funk.get());
        return funk;
    }

    @Override
    public CompletableFuture<Funko> update(Funko funko) throws FunkoNotFoundException, SQLException {
        logger.debug("Actualizando Funko....");
        var funk = funkoRepository.update(funko);
        cache.put(funko.getId(),funko);
        return funk;
    }

    @Override
    public CompletableFuture<Boolean> deleteById(int id) throws FunkoNotFoundException, SQLException, ExecutionException, InterruptedException {
        logger.debug("Eliminando Funko con id: " + id);
        var res = funkoRepository.deleteById(id);
        if (res.get()) {
            cache.remove(id);
        }
        return res;
    }

    @Override
    public CompletableFuture<Void> deleteAll(){
       return CompletableFuture.runAsync(()->{
            try {
                logger.debug("Eliminando todos los elementos de la Base de Datos....");
                funkoRepository.deleteAll().join();
                cache.clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

        @Override
    public CompletableFuture<Boolean> backup(String file) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Iniciando Backup de la Base de Datos......");
        var suc = backupManager.writeFileFunko(file,findAll().get());
        logger.debug("Backup Realizado Correctamente!");
        return suc;
    }

    @Override
    public CompletableFuture<List<Funko>> imported(String file) throws InterruptedException {
        return backupManager.readFileFunko(file);
    }
}
