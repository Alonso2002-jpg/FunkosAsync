package org.develop.repositories;

import java.util.concurrent.CompletableFuture;

public interface FunkoRepository<T,ID> extends CRUDRepository<T,ID>{
    CompletableFuture<T> findByNombre(String nombre);
}
