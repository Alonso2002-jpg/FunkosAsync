package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.model.Funko;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interfaz que extiende la interfaz `CRUDRepository` para definir operaciones especificas de un repositorio de entidades Funko.
 * Ademas de las operaciones CRUD estandar, esta interfaz proporciona un metodo adicional para buscar Funkos por nombre.
 *
 * @author Alonso Cruz, Joselyn Obando
 */
public interface FunkoRepository extends CRUDRepository<Funko,Integer>{

    /**
     * Busca y recupera una lista de Funkos que coincidan con un nombre especifico.
     *
     * @param nombre El nombre de los Funkos a buscar.
     * @return Una lista de Funkos cuyos nombres coinciden con el parametro proporcionado.
     */
    CompletableFuture<List<Funko>> findByNombre(String nombre);
}
