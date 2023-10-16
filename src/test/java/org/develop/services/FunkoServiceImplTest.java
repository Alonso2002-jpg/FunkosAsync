package org.develop.services;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.repositories.FunkoRepository;
import org.develop.services.files.BackupManager;
import org.develop.services.files.BackupManagerImpl;
import org.develop.services.funkos.FunkoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {

    private Funko funko1, funko2;

    @Mock
    FunkoRepository repository;
    @Mock
    BackupManagerImpl backupManager;

    @InjectMocks
    FunkoServiceImpl service;

    @BeforeEach
    void setup(){
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
    void findAll() throws SQLException, ExecutionException, InterruptedException {
        var listFunk = List.of(funko1,funko2);

        when(repository.findAll()).thenReturn(CompletableFuture.completedFuture(listFunk));

        var res = service.findAll().get();

        assertAll(
                ()-> assertFalse(res.isEmpty()),
                ()-> assertEquals(res.size(),2),
                ()-> assertEquals(res.get(0).getUuid(),funko1.getUuid()),
                ()-> assertEquals(res.get(1).getUuid(),funko2.getUuid())
        );
        verify(repository,times(1)).findAll();
    }

    @Test
    void findAllByNombre() throws ExecutionException, InterruptedException {
        var listFunk = List.of(funko1,funko2);

        when(repository.findByNombre("test")).thenReturn(CompletableFuture.completedFuture(listFunk));

        var res = service.findAllByNombre("test").get();

        assertAll(
                ()-> assertFalse(res.isEmpty()),
                ()-> assertEquals(res.size(),2),
                ()-> assertEquals(res.get(0).getUuid(),funko1.getUuid()),
                ()-> assertEquals(res.get(1).getUuid(),funko2.getUuid())
        );

        verify(repository,times(1)).findByNombre("test");
    }

    @Test
    void findById() throws SQLException, ExecutionException, InterruptedException {
        when(repository.findById(1)).thenReturn(CompletableFuture.completedFuture(Optional.of(funko1)));

        var res = service.findById(1).get();

        assertAll(
                ()-> assertTrue(res.isPresent()),
                ()-> assertEquals(res.get().getName(),funko1.getName()),
                ()-> assertEquals(res.get().getUuid(),funko1.getUuid())
        );

        verify(repository,times(1)).findById(1);
    }

    @Test
    void findByIdError() throws SQLException, ExecutionException, InterruptedException {
        when(repository.findById(1)).thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        var res = service.findById(1).get();

        assertTrue(res.isEmpty());
    }

    @Test
    void save() throws SQLException, ExecutionException, InterruptedException  {
        when(repository.save(funko1)).thenReturn(CompletableFuture.completedFuture(funko1));

        var res = service.save(funko1).get();

        assertAll(
                ()-> assertNotNull(res),
                ()-> assertEquals(res.getName(),funko1.getName()),
                ()-> assertEquals(res.getUuid(),funko1.getUuid())
        );

        verify(repository,times(1)).save(funko1);
    }

    @Test
    void update() throws SQLException, ExecutionException, InterruptedException {
        when(repository.update(funko1)).thenReturn(CompletableFuture.completedFuture(funko1));

        var res = service.update(funko1).get();

        assertAll(
                ()-> assertEquals(res.getName(),funko1.getName()),
                ()-> assertEquals(res.getUuid(),funko1.getUuid())
        );
    }

    @Test
    void updateError() throws SQLException, ExecutionException, InterruptedException {
        when(repository.update(funko1)).thenThrow(new FunkoNotFoundException("Funko con ID " + 1 + " no encontrado en la BD"));

        try {
            var res = service.update(funko1).get();
        }catch (FunkoNotFoundException e){
            assertEquals(e.getMessage(),"Funko con ID " + 1 + " no encontrado en la BD");
        }

        verify(repository,times(1)).update(funko1);
    }
    @Test
    void deleteById() throws SQLException, ExecutionException, InterruptedException {
            when(repository.deleteById(1)).thenReturn(CompletableFuture.completedFuture(true));

            var res = service.deleteById(1).get();

            assertTrue(res);

            verify(repository,times(1)).deleteById(1);
    }

    @Test
    void deletedByIdError() throws SQLException, ExecutionException, InterruptedException {
        when(repository.deleteById(1)).thenReturn(CompletableFuture.completedFuture(false));

        var res = service.deleteById(1).get();

        assertFalse(res);
    }
    @Test
    void deleteAll() throws SQLException, ExecutionException, InterruptedException {
        when(repository.deleteAll()).thenReturn(CompletableFuture.completedFuture(null));

        service.deleteAll().get();

        verify(repository,times(1)).deleteAll();
    }

    @Test
    void backup() throws SQLException, ExecutionException, InterruptedException {
        var listFunk = List.of(funko1,funko2);

        when(backupManager.writeFileFunko("funkosTest.json",listFunk)).thenReturn(CompletableFuture.completedFuture(true));
        when(repository.findAll()).thenReturn(CompletableFuture.completedFuture(listFunk));

        var res = service.backup("funkosTest.json").get();

        assertTrue(res);
    }

    @Test
    void imported() throws InterruptedException, ExecutionException {
        var listFunk = List.of(funko1,funko2);

        when(backupManager.readFileFunko("funkosTest.json")).thenReturn(CompletableFuture.completedFuture(listFunk));

        var res = service.imported("funkosTest.json").get();

        assertAll(
                ()-> assertNotNull(res),
                ()-> assertEquals(res.size(),2)
        );
    }
}