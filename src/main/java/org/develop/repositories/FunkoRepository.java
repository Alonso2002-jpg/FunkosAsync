package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.model.Funko;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FunkoRepository extends CRUDRepository<Funko,Integer>{
    CompletableFuture<List<Funko>> findByNombre(String nombre);
}
