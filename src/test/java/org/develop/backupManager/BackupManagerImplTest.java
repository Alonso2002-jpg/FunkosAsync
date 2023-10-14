package org.develop.backupManager;

import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.services.files.BackupManager;
import org.develop.services.files.BackupManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class BackupManagerImplTest {

    private BackupManagerImpl backupManager;
    private Funko funko1,funko2;

    @BeforeEach
    void setup() throws Exception{

        backupManager = BackupManagerImpl.getInstance();

        funko1=new Funko();
        funko1.setUuid(UUID.randomUUID());
        funko1.setName("test");
        funko1.setModelo(Modelo.OTROS);
        funko1.setPrecio(1.0);
        funko1.setFecha_lanzamiento(LocalDate.of(2024,1,20));

        funko2=new Funko();
        funko2.setUuid(UUID.randomUUID());
        funko2.setName("test2");
        funko2.setModelo(Modelo.MARVEL);
        funko2.setPrecio(1.5);
        funko2.setFecha_lanzamiento(LocalDate.of(2026,4,10));
    }
    @Test
    void writeFileFunko() throws ExecutionException, InterruptedException {
        var listFunks = List.of(funko1,funko2);
        File file = new File(Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + "testFunkos.json");

        boolean res = backupManager.writeFileFunko(file.getName(),listFunks).get();

        assertAll(
                ()-> assertTrue(res),
                ()-> assertTrue(file.exists())
        );

    }

    @Test
    void readFileFunko() throws InterruptedException, ExecutionException {

        var listFunks = backupManager.readFileFunko("funkos.csv").get();

        assertAll(
                ()-> assertNotNull(listFunks),
                ()-> assertFalse(listFunks.isEmpty()),
                ()-> assertEquals(listFunks.size(),90)
        );
    }
}