package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Interfaz que define operaciones basicas de un repositorio CRUD (Crear, Leer, Actualizar, Borrar) para entidades.
 * Esta interfaz proporciona metodos para guardar, actualizar, buscar, listar y borrar entidades, asi como realizar
 * operaciones de respaldo.
 *
 * @param <T> El tipo de entidad que se gestionará en el repositorio.
 * @param <ID> El tipo de identificador utilizado para las entidades.
 *
 * @author Alonso Cruz, Joselyn Obando
 */
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
