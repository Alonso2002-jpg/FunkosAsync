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

public class BackupManager {
        private final Logger logger = LoggerFactory.getLogger(BackupManager.class);
        public CompletableFuture<Boolean> writeFileFunko(String nomFile,List<Funko> funks){
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
         public CompletableFuture<List<Funko>> readFileFunko() throws InterruptedException {

         String path = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + "funkos.csv";
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

