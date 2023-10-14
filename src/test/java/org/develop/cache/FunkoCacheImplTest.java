package org.develop.cache;

import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.services.funkos.FunkoCacheImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FunkoCacheImplTest {
    private FunkoCacheImpl funkoCache;
    private Funko funko1,funko2;

    @BeforeEach
    void setup(){
        funkoCache = new FunkoCacheImpl(10);

        funko1=new Funko();
        funko1.setId(1);
        funko1.setUuid(UUID.randomUUID());
        funko1.setName("test");
        funko1.setModelo(Modelo.OTROS);
        funko1.setPrecio(1.0);
        funko1.setFecha_lanzamiento(LocalDate.of(2024,1,20));

        funko2=new Funko();
        funko2.setId(2);
        funko2.setUuid(UUID.randomUUID());
        funko2.setName("test2");
        funko2.setModelo(Modelo.MARVEL);
        funko2.setPrecio(1.5);
        funko2.setFecha_lanzamiento(LocalDate.of(2026,4,10));
    }

    @AfterEach
    void tearDown(){
        funkoCache.clear();
    }

    @Test
    void cacheSizeTest(){
        assertEquals(10,funkoCache.getMaxSize());
    }
    @Test
    void putTest() {
        funkoCache.put(funko1.getId(),funko1).join();

        assertAll(
                ()-> assertFalse(funkoCache.getCache().isEmpty()),
                ()-> assertEquals(funkoCache.getCache().size(),1)
        );
    }

    @Test
    void getTest() throws ExecutionException, InterruptedException {
        funkoCache.put(funko1.getId(),funko1).join();
        Optional<Funko> funkoCach = funkoCache.get(funko1.getId()).get();

        assertAll(
                ()-> assertTrue(funkoCach.isPresent()),
                ()-> assertEquals(funko1.getId(),funkoCach.get().getId()),
                ()-> assertEquals(funko1.getUuid(),funkoCach.get().getUuid())
        );
    }

    @Test
    void removeTest() {
        funkoCache.put(funko1.getId(),funko1).join();
        funkoCache.put(funko2.getId(),funko2).join();
        funkoCache.remove(funko1.getId());

        assertAll(
                ()-> assertFalse(funkoCache.getCache().isEmpty()),
                ()-> assertEquals(funkoCache.getCache().size(),1)
        );
    }

    @Test
    void clearTest() {
        funkoCache.put(funko1.getId(),funko1);
        funkoCache.put(funko2.getId(),funko2);

        funkoCache.clear();
        assertAll(
                ()-> assertTrue(funkoCache.getCache().isEmpty())
        );
    }

    @Test
    void clearForTimeTest() throws InterruptedException {
        funkoCache.put(funko1.getId(),funko1);
        funkoCache.getCleaner().scheduleAtFixedRate(funkoCache::clear,1,1, TimeUnit.MINUTES);
        Thread.sleep(61000);

        assertAll(
                ()-> assertTrue(funkoCache.getCache().isEmpty())
        );
    }

    @Test
    void shutdownTest() {
        funkoCache.shutdown().join();
        assertTrue(funkoCache.getCleaner().isShutdown());
    }
}