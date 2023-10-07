package org.develop;

import org.develop.model.Funko;
import org.develop.repositories.FunkoRepositoryImpl;
import org.develop.services.database.DatabaseManager;
import org.develop.services.files.BackupManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        BackupManager readCSV = new BackupManager();
        FunkoRepositoryImpl fk = FunkoRepositoryImpl.getInstance(DatabaseManager.getInstance());

        var list = readCSV.readFileFunko();

        List<Funko> funkBD = new ArrayList<>();

        for (Funko funko : list.get()) {
            funkBD.add(fk.save(funko).get());
        }


        var fkup = fk.update(funkBD.get(5));

        System.out.println(fkup.get());
    }
}