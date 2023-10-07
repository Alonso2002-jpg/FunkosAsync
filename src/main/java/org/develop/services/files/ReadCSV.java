package org.develop.services.files;

import org.develop.model.Funko;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class ReadCSV {

         public CompletableFuture<List<Funko>> readFileFunko() throws InterruptedException {

         String path = Paths.get("").toAbsolutePath().toString() + File.separator + "data" + File.separator + "funkos.csv";
         List<Funko> funkos = new ArrayList<>();
         Thread.sleep(10000);
          return CompletableFuture.supplyAsync(()->{
                        try(BufferedReader reader =new BufferedReader(new FileReader(path))){
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

