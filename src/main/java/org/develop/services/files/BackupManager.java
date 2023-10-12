package org.develop.services.files;

import org.develop.model.Funko;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BackupManager <T>{
    CompletableFuture<Boolean> writeFileFunko(String nomFile, List<T> funks);
    CompletableFuture<List<Funko>> readFileFunko(String nomFile) throws InterruptedException;
}
