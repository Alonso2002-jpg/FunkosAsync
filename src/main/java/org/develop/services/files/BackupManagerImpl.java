package org.develop.services.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.develop.adapters.LocalDateAdapter;
import org.develop.adapters.LocalDateTimeAdapter;
import org.develop.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementacion de la interfaz BackupManager para realizar operaciones de respaldo de objetos de tipo Funko.
 */
public class BackupManagerImpl implements BackupManager<Funko>{

    private static BackupManagerImpl instance;
    private final Logger logger = LoggerFactory.getLogger(BackupManagerImpl.class);

    private BackupManagerImpl() {
    }

    /**
     * Obtiene una instancia unica de BackupManagerImpl.
     *
     * @return Una instancia de BackupManagerImpl.
     */
    public static BackupManagerImpl getInstance() {
        if (instance == null) {
            instance= new BackupManagerImpl();
        }
        return instance;
    }

    /**
     * Escribe una lista de objetos Funko en un archivo JSON como parte de la operacion de respaldo.
     *
     * @param nomFile El nombre del archivo en el que se guardaran los objetos.
     * @param funks   La lista de objetos Funko que se va a respaldar.
     * @return Un CompletableFuture<Boolean> que indica si la operacion de escritura del archivo fue exitosa (true) o no (false).
     */
    @Override
    public CompletableFuture<Boolean> writeFileFunko(String nomFile, List funks) {
        return CompletableFuture.supplyAsync(()->{
                    String path = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + nomFile;
                    logger.debug("Escribiendo JSON de funkos en: " + path);
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                            .setPrettyPrinting()
                            .create();
                    boolean success = false;
                    try (FileWriter writer = new FileWriter(path)) {
                        gson.toJson(funks, writer);
                        success = true;
                    } catch (Exception e) {
                        logger.error("Error: "+e.getMessage(), e);
                }
                return success;
            });
    }


    /**
     * Lee una lista de objetos Funko desde un archivo JSON como parte de la operacion de restauracion.
     *
     * @param nomFile El nombre del archivo desde el cual se leeran los objetos.
     * @return Un CompletableFuture que contendra una lista de objetos de tipo Funko leidos desde el archivo de respaldo.
     * @throws InterruptedException Si la operacion de lectura es interrumpida.
     */
    @Override
    public CompletableFuture<List<Funko>> readFileFunko(String nomFile) throws InterruptedException {
         String path = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + nomFile;
         List<Funko> funkos = new ArrayList<>();
         Thread.sleep(10000);
          return CompletableFuture.supplyAsync(()->{
                        try(BufferedReader reader =new BufferedReader(new FileReader(path))){
                            logger.debug("Leyendo Funko desde : " + path);
                        String line;
                        reader.readLine();
                        while ((line = reader.readLine()) != null){
                                Funko fk = new Funko().setFunko(line);
                                funkos.add(fk);
                        }
                }catch (Exception e){
                        System.out.println(e.getMessage());
                }
                        return funkos;
                });

    }
}

