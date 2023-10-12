package org.develop;

import org.develop.model.Funko;
import org.develop.model.MyIDGenerator;
import org.develop.repositories.FunkoRepositoryImpl;
import org.develop.services.database.DatabaseManager;
import org.develop.services.files.BackupManager;
import org.develop.services.funkos.FunkoServiceImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        BackupManager backupManager = new BackupManager();
        FunkoServiceImpl fknServ = FunkoServiceImpl.getInstance(backupManager,FunkoRepositoryImpl.getInstance(DatabaseManager.getInstance(), MyIDGenerator.getInstance()));

        var list = backupManager.readFileFunko();

        list.get().forEach(fkn -> {
            try {
                fknServ.save(fkn).get();
            } catch (InterruptedException | ExecutionException | SQLException e) {
                throw new RuntimeException(e);
            }
        });

        var fkn = fknServ.findById(5);
        var creat= fknServ.backup("funkos.json");
        System.out.println(creat.get());

        System.out.println(fkn.get());


    }
}