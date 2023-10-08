package org.develop.services.funkos;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;
import org.develop.model.Funko;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface FunkoService {
    CompletableFuture<List<Funko>> findAll() throws SQLException, ExecutionException, InterruptedException;

    CompletableFuture<List<Funko>> findAllByNombre(String nombre) throws SQLException, ExecutionException, InterruptedException;

    CompletableFuture<Optional<Funko>> findById(int id) throws SQLException, ExecutionException, InterruptedException, FunkoNotFoundException;

    CompletableFuture<Funko> save(Funko funko) throws SQLException, FunkoNotSaveException, ExecutionException, InterruptedException;

    CompletableFuture<Funko> update(Funko funko) throws SQLException, FunkoNotFoundException, ExecutionException, InterruptedException;

    CompletableFuture<Boolean> deleteById(int id) throws SQLException,FunkoNotFoundException ,ExecutionException, InterruptedException;

    CompletableFuture<Void> deleteAll() throws SQLException, ExecutionException, InterruptedException;

    CompletableFuture<Boolean> backup(String file) throws SQLException, ExecutionException, InterruptedException;
}
