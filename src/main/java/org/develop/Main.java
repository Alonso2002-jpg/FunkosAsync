package org.develop;

import org.develop.model.Funko;
import org.develop.model.MyIDGenerator;
import org.develop.repositories.FunkoRepositoryImpl;
import org.develop.services.database.DatabaseManager;
import org.develop.services.files.BackupManagerImpl;
import org.develop.services.funkos.FunkoService;
import org.develop.services.funkos.FunkoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {

    private final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        FunkoServiceImpl fknServ = FunkoServiceImpl.getInstance(BackupManagerImpl.getInstance(),FunkoRepositoryImpl.getInstance(DatabaseManager.getInstance(), MyIDGenerator.getInstance()));
        Main main = new Main();
        //Importando archivo de Funkos
        var importe = fknServ.imported("funkos.csv");
//        //Importando mal archivo de Funkos
//        var BadImporte = fknServ.imported("funkosbad.csv");
//
        //Guardando Funkos en la BD
        for (Funko funko : importe.get()) {
            var funkoSave = fknServ.save(funko).get();
        }
//
//        //Obteniendo todos los Funkos de la BD
//        fknServ.findAll().get().forEach(System.out::println);
//
        //Obteniendo Funko con ID: 10
        var funkoId = fknServ.findById(10);
//        System.out.println(funkoId.get());
//        //Obteninedo Funko con ID erroneo
//        var funkoBadId = fknServ.findById(200);
//        System.out.println(funkoBadId.get());
//
//        //Actualizando Funko
//        Funko fkn = funkoId.get().get();
//        fkn.setName("Funko Actualizado");
//        var updateFun = fknServ.update(funk);
//        System.out.println(updateFun.get());
//
//        //Obteniendo Funkos con nombre : "Super"
//        var funkoName = fknServ.findAllByNombre("Super");
//        funkoName.get().forEach(System.out::println);
//        //Obteniendo Ningun Funko con nombre : "sakfaskf"
//        var badFunkoName = fknServ.findAllByNombre("sakfaskf");
//        badFunkoName.get().forEach(System.out::println);
//
//        //Borrando Funko con ID: 10
//        var funkoDel = fknServ.deleteById(10);
//        System.out.println(funkoDel.get());
//        //Borrando Funko con Id erroneo
//        var funkoBadDel = fknServ.deleteById(200);
//        System.out.println(funkoBadDel.get());
//
//        //Borrando Todos los Datos de la BD
//        fknServ.deleteAll();
//
//        //Exportando Datos a Fichero JSON
//        var exp = fknServ.backup("funkos.json");
//        System.out.println(exp.get());
//        //Exportando Mal Datos a Fichero JSON
//        var badexp = fknServ.backup("funkos");
//        System.out.println(badexp.get());
//
//        var allFunks = fknServ.findAll();
//
//        var moreExpFun = allFunks.get().stream()
//                        .max(Comparator.comparingDouble(Funko::getPrecio));
//        main.logger.debug("Funko mas Caro");
//        System.out.println(moreExpFun.orElse(new Funko()));
//
//        main.logger.debug("Media de precio de Funkos");
//        var funkPricAverage = allFunks.get().stream()
//                .mapToDouble(Funko::getPrecio)
//                .average();
//        System.out.println("Media de precios : " + funkPricAverage.orElse(0.0));
//
//        main.logger.debug("Funkos Agrupados por Modelo");
//        var funkType= allFunks.get().stream()
//                .map(Funko::getModelo)
//                .distinct()
//                .collect(Collectors.toMap(fk->fk,
//                        fk-> {
//                            try {
//                                return allFunks.get().stream()
//                                .filter(fkT -> fkT.getModelo().equals(fk))
//                                .toList();
//                            } catch (InterruptedException | ExecutionException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }));
//        funkType.forEach((a,b) -> System.out.println(a + " : " + b));
//
//        main.logger.debug("Numero de Funkos por Modelo");
//        var funkCountType = allFunks.get().stream()
//                .map(Funko::getModelo)
//                .collect(Collectors.groupingBy(fk->fk,Collectors.counting()));
//        funkCountType.forEach((a,b) -> System.out.println(a + " : " + b));
//
//        main.logger.debug("Funkos Lanzados en el 2023");
//        var funkLaunchDate = allFunks.get().stream()
//                .filter(fk -> fk.getFecha_lanzamiento().toString().contains("2023"))
//                .toList();
//        funkLaunchDate.forEach(System.out::println);
//
//        main.logger.debug("Funkos de Stitch");
//        var count = allFunks.get().stream()
//        .filter(fk -> fk.getName().contains("Stitch"))
//        .count();
//
//        var stitchFunkos = allFunks.get().stream()
//                .filter(fk -> fk.getName().contains("Stitch"))
//                .collect(Collectors.groupingBy(
//                        fk -> count,  // Utiliza la cantidad como clave
//                        Collectors.toList()
//                ));
//
//        System.out.println("Funkos de Stitch : ");
//        stitchFunkos.forEach((a,b) -> System.out.println(a + " : " + b));
    }

}