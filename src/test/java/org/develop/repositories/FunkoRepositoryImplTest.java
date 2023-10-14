package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.model.MyIDGenerator;
import org.develop.services.database.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class FunkoRepositoryImplTest {
    private FunkoRepository funkoRepository;
    private Funko funko1,funko2;

    @BeforeEach
    void setup() throws SQLException {
        funkoRepository = FunkoRepositoryImpl.getInstance(DatabaseManager.getInstance(), MyIDGenerator.getInstance());

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

    @AfterEach
    void teardown() throws SQLException {
        funkoRepository.deleteAll();
    }

    @Test
    void saveTest() throws SQLException, ExecutionException, InterruptedException {
        Funko fknSave = funkoRepository.save(funko1).get();

        assertAll(() -> assertNotNull(fknSave),
                () -> assertTrue(fknSave.getId() > 0),
                () -> assertTrue(fknSave.getMyId() > 0),
                () -> assertEquals(funko1.getName(), fknSave.getName()),
                () -> assertEquals(funko1.getUuid(), fknSave.getUuid()),
                () -> assertEquals(funko1.getPrecio(), fknSave.getPrecio()),
                () -> assertEquals(funko1.getModelo(),fknSave.getModelo()),
                () -> assertNotNull(fknSave.getCreated_at()),
                () -> assertNotNull(fknSave.getUpdated_at())
        );
    }

    @Test
    void updateTest() throws SQLException, ExecutionException, InterruptedException {
     Funko fknSave = funkoRepository.save(funko1).get();

        fknSave.setName(funko2.getName());
        fknSave.setModelo(funko2.getModelo());
        fknSave.setPrecio(funko2.getPrecio());
        fknSave.setFecha_lanzamiento(LocalDate.now());

        Funko funkoUpdt = funkoRepository.update(fknSave).get();

        assertAll(
                ()-> assertNotNull(funkoUpdt),
                ()-> assertEquals(funko1.getUuid(),funkoUpdt.getUuid()),
                ()-> assertEquals(funko2.getName(),funkoUpdt.getName()),
                ()-> assertEquals(funko2.getModelo(),funkoUpdt.getModelo()),
                ()-> assertEquals(funko2.getPrecio(),funkoUpdt.getPrecio()),
                ()-> assertEquals(LocalDate.now(),funkoUpdt.getFecha_lanzamiento())
        );
    }
    @Test
    void updateBadTest2(){

        funko2.setId(500);

        Exception exception = assertThrows(ExecutionException.class,()->funkoRepository.update(funko2).get());

        assertAll(
                ()-> assertNotNull(exception),
                ()-> assertTrue(exception.getMessage().contains("Funko con ID " + 500 +" no encontrado en la BD"))
        );
    }

    @Test
    void findByIdTest() throws SQLException, ExecutionException, InterruptedException {
    Funko fknSave =funkoRepository.save(funko1).get();

    Optional<Funko> fknId = funkoRepository.findById(fknSave.getId()).get();

    assertAll(
            ()-> assertTrue(fknId.isPresent()),
            ()->assertEquals(fknSave.getId(),fknId.get().getId()),
            ()->assertEquals(fknSave.getUuid(),fknId.get().getUuid()),
            ()->assertEquals(fknSave.getMyId(),fknId.get().getMyId())
    );
    }

    @Test
    void notFindByIdTest() throws SQLException, ExecutionException, InterruptedException {
     Optional<Funko> fknId = funkoRepository.findById(100).get();
        assertAll(
                ()-> assertTrue(fknId.isEmpty())
        );
    }
    @Test
    void findByNombreTest() throws SQLException, ExecutionException, InterruptedException {
     funko1 = funkoRepository.save(funko1).get();
     funko2 = funkoRepository.save(funko2).get();

     var funkName = funkoRepository.findByNombre(funko1.getName()).get();

     assertAll(
                ()-> assertFalse(funkName.isEmpty()),
                ()-> assertTrue(funkName.size() > 1),
                ()-> assertEquals(funko1.getId(),funkName.get(0).getId()),
                ()-> assertEquals(funko1.getUuid(),funkName.get(0).getUuid()),
                ()-> assertEquals(funko1.getMyId(),funkName.get(0).getMyId())
     );
    }

    @Test
    void findAllTest() throws SQLException, ExecutionException, InterruptedException {
        funko1 =funkoRepository.save(funko1).get();
        funko2 = funkoRepository.save(funko2).get();
        var listFunkos = funkoRepository.findAll().get();

        assertAll(
                ()-> assertFalse(listFunkos.isEmpty()),
                ()-> assertTrue((listFunkos.size() == 2)),
                ()-> assertEquals(listFunkos.get(0).getId(),funko1.getId()),
                ()-> assertEquals(listFunkos.get(1).getId(),funko2.getId())
        );
    }

    @Test
    void deleteByIdTest() throws SQLException, ExecutionException, InterruptedException {
        var funkSave = funkoRepository.save(funko1).get();
        var funkDel = funkoRepository.deleteById(funkSave.getId()).get();
        var list = funkoRepository.findAll().get();
        assertAll(
                ()-> assertTrue(funkDel),
                ()-> assertEquals(0,list.size())
        );
    }

    @Test
    void deleteByIdTestError() throws SQLException{

        Exception fknFe = assertThrows(ExecutionException.class,()->funkoRepository.deleteById(500).get());

        assertAll((
                ()-> assertTrue(fknFe.getMessage().contains("Funko con ID " + 500 + " no encontrado en la BD"))
                ));
    }
    @Test
    void deleteAllTest() throws SQLException, ExecutionException, InterruptedException {
        funko1 = funkoRepository.save(funko1).get();
        funko2 = funkoRepository.save(funko2).get();

        funkoRepository.deleteAll();
        var allFunk = funkoRepository.findAll().get();

        assertEquals(0, allFunk.size());
    }

}