package org.develop;

import org.develop.model.Funko;
import org.develop.repositories.FunkoRepositoryImpl;
import org.develop.services.database.DatabaseManager;
import org.develop.services.files.ReadCSV;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        ReadCSV readCSV = new ReadCSV();
        FunkoRepositoryImpl fk = FunkoRepositoryImpl.getInstance(DatabaseManager.getInstance());

        var list = readCSV.readFileFunko();

        for (Funko funko : list.get()) {
            fk.save(funko);
        }

    }
}