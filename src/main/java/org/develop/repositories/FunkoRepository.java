package org.develop.repositories;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FunkoRepository<T,ID> extends CRUDRepository<T,ID>{
    CompletableFuture<List<T>> findByNombre(String nombre);
}
