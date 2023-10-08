package org.develop.services.funkos;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;
import org.develop.model.Funko;
import org.develop.repositories.FunkoRepository;
import org.develop.repositories.FunkoRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FunkoServiceImpl implements FunkoService{
    private static final int CACHE_SIZE =10;
    private static FunkoServiceImpl instance;
    private final FunkoCache cache;
    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private final FunkoRepository funkoRepository;

    private FunkoServiceImpl(FunkoRepository funkoRepository){
        this.funkoRepository =funkoRepository;

        this.cache = new FunkoCacheImpl(CACHE_SIZE);
    }
    public static FunkoServiceImpl getInstance(FunkoRepository funkoRepository){
        if (instance == null){
            instance = new FunkoServiceImpl(funkoRepository);
        }
        return instance;
    }
    @Override
    public CompletableFuture<List<Funko>> findAll(){
        return CompletableFuture.supplyAsync(()->{
            List<Funko> listF;
            try{
              logger.debug("Obteniendo todos los Funkos");
              listF = funkoRepository.findAll().get();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return listF;
        });
    }

    @Override
    public CompletableFuture<List<Funko>> findAllByNombre(String nombre) {
        return CompletableFuture.supplyAsync(()->{
            List<Funko> listF;
            try {
                logger.debug("Obteniendo los funkos que coincidad con: " + nombre);
                listF=funkoRepository.findByNombre(nombre).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return listF;
        });
    }

    @Override
    public CompletableFuture<Optional<Funko>> findById(int id) throws FunkoNotFoundException{
        return CompletableFuture.supplyAsync(()->{
            Optional<Funko> funko;
            try {
                funko = funkoRepository.findById(id).get();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return funko;
        });
    }

    @Override
    public CompletableFuture<Funko> save(Funko funko) throws FunkoNotSaveException{
        return CompletableFuture.supplyAsync(()->{
            Funko funks;
            try {
                funks = funkoRepository.save(funko).get();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return funks;
        });
    }

    @Override
    public CompletableFuture<Funko> update(Funko funko) throws SQLException, FunkoNotFoundException, ExecutionException, InterruptedException {
       return CompletableFuture.supplyAsync(()->{
            Funko funks;
            try {
                funks = funkoRepository.update(funko).get();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return funks;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(int id) throws SQLException, FunkoNotFoundException, ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteAll() throws SQLException, ExecutionException, InterruptedException {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> backup(String file) throws SQLException, ExecutionException, InterruptedException {
        return null;
    }
}
