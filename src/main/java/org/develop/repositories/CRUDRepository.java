package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CRUDRepository <T,ID>{
        // Guardar
    CompletableFuture<T> save(T t) throws SQLException, FunkoNotSaveException;

    // Actualizar
    CompletableFuture<T> update(T t) throws SQLException, FunkoNotFoundException;

    // Buscar por ID
    CompletableFuture<Optional<T>> findById(ID id) throws SQLException, FunkoNotFoundException;

    // Buscar todos
    CompletableFuture<List<T>> findAll() throws SQLException;

    // Borrar por ID
    CompletableFuture<Boolean> deleteById(ID id) throws SQLException, FunkoNotFoundException;

    // Borrar todos
    CompletableFuture<Void> deleteAll() throws SQLException;

    // Hacer Backup
}
