package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;
import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.services.database.DatabaseManager;
import org.develop.services.files.BackupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FunkoRepositoryImpl implements FunkoRepository<Funko,Integer> {
    private static FunkoRepositoryImpl instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);

    private final DatabaseManager db;

    private FunkoRepositoryImpl(DatabaseManager db) {
        this.db = db;
    }

    public synchronized static FunkoRepositoryImpl getInstance(DatabaseManager db) {
        if (instance == null) {
            instance = new FunkoRepositoryImpl(db);
        }

        return instance;
    }

    @Override
    public CompletableFuture<Funko> save(Funko funko) throws FunkoNotSaveException {
        logger.debug("Saving Funko On Database: " + funko.getName());
        String sqlQuery = "INSERT INTO Funko (uuid,myid, name, modelo, precio, fecha_lanzamiento) VALUES (?, ?, ?, ?, ?, ?)";
        return CompletableFuture.supplyAsync(() -> {
            try (var conn = db.getConnection();
                 var stmt = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setObject(1, funko.getUuid());
                stmt.setLong(2, funko.getMyId());
                stmt.setString(3, funko.getName());
                stmt.setString(4, funko.getModelo().toString());
                stmt.setDouble(5, funko.getPrecio());
                stmt.setDate(6, Date.valueOf(funko.getFecha_lanzamiento()));
                int res = stmt.executeUpdate();
                conn.commit();

                if (res > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        funko.setId(rs.getInt(1)); // Obtiene el ID generado automáticamente
                    }
                    rs.close();
                } else {
                    logger.error("Objeto no guardado en la base de datos");
                    throw new FunkoNotSaveException("Funko con nombre " + funko.getName() + " no almacenado en la BD");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return funko;
        });
    }

    @Override
    public CompletableFuture<Funko> update(Funko funko) throws FunkoNotFoundException {
        logger.info("Actualizando Objeto ..... ");
        String sqlQuery = "UPDATE Funko SET name = ? , modelo = ?, precio = ? , updated_at = ? WHERE id = ?";
        return CompletableFuture.supplyAsync(() -> {
            try (var conn = db.getConnection();
                 var stmt = conn.prepareStatement(sqlQuery)) {
                stmt.setString(1, funko.getName());
                stmt.setString(2, funko.getModelo().toString());
                stmt.setDouble(3, funko.getPrecio());
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(5, funko.getId());
                var rs = stmt.executeUpdate();
                if (rs <= 0) {
                    logger.error("Funko no encontrado en la BD");
                    throw new FunkoNotFoundException("Funko con ID " + funko.getId() + " no encontrado en la BD");
                }

                logger.debug("Objeto Actualizado Correctamente!");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return funko;
        });
    }

    @Override
    public CompletableFuture<Optional<Funko>> findById(Integer id) throws FunkoNotFoundException {
        logger.info("Buscando Objeto con ID " + id + "......");
        String sqlQuery = "SELECT * FROM Funko WHERE id = ?";
        return CompletableFuture.supplyAsync(() -> {
            Optional<Funko> funk = Optional.empty();
            try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)) {
                stmt.setInt(1, id);
                var rs = stmt.executeQuery();
                while (rs.next()) {
                    Funko fk = new Funko();
                    fk.setId(rs.getInt("id"));
                    fk.setMyId(rs.getLong("Myid"));
                    fk.setUuid((UUID) rs.getObject("uuid"));
                    fk.setName(rs.getString("name"));
                    fk.setModelo(Modelo.valueOf(rs.getString("modelo")));
                    fk.setPrecio(rs.getDouble("precio"));
                    fk.setFecha_lanzamiento(rs.getDate("fecha_lanzamiento").toLocalDate());
                    funk = Optional.of(fk);
                }
            } catch (SQLException e) {
                logger.error("ERROR: " + e.getMessage(), e);
            }

            return funk;
        });
    }

    @Override
    public CompletableFuture<List<Funko>> findByNombre(String nombre) {
        logger.info("Obtener Funko con Nombre "+nombre+".......");
        List<Funko> funks = new ArrayList<>();
        return CompletableFuture.supplyAsync(()->{
        String sqlQuery = "SELECT * FROM Funko WHERE name LIKE ?";
        try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)){
            stmt.setString(1,"%" + nombre + "%");
            var rs = stmt.executeQuery();
            while (rs.next()){
                Funko fk = new Funko();
                fk.setId(rs.getInt("id"));
                fk.setMyId(rs.getLong("Myid"));
                fk.setUuid((UUID) rs.getObject("uuid"));
                fk.setName(rs.getString("name"));
                fk.setModelo(Modelo.valueOf(rs.getString("modelo")));
                fk.setPrecio(rs.getDouble("precio"));
                fk.setFecha_lanzamiento(rs.getDate("fecha_lanzamiento").toLocalDate());
                funks.add(fk);
            }
            logger.debug("Objeto obtenido con nombre: " + nombre);
        }catch (SQLException e) {
            logger.error("ERROR: " + e.getMessage(),e);
        }
        return funks;
        });
    }

    @Override
    public CompletableFuture<List<Funko>> findAll() {
        logger.info("Obteniendo todos los Objetos");
        String sqlQuery = "SELECT * FROM Funko";

        return CompletableFuture.supplyAsync(() -> {
            List<Funko> funks = new ArrayList<>();
            try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)) {
                var rs = stmt.executeQuery();
                while (rs.next()) {
                    Funko fk = new Funko();
                    fk.setId(rs.getInt("id"));
                    fk.setMyId(rs.getLong("Myid"));
                    fk.setUuid((UUID) rs.getObject("uuid"));
                    fk.setName(rs.getString("name"));
                    fk.setModelo(Modelo.valueOf(rs.getString("modelo")));
                    fk.setPrecio(rs.getDouble("precio"));
                    fk.setFecha_lanzamiento(rs.getDate("fecha_lanzamiento").toLocalDate());
                    funks.add(fk);
                }
                logger.debug("Objetos Obtenidos Correctamente");
            } catch (SQLException e) {
                logger.error("ERROR: " + e.getMessage(), e);
            }
            return funks;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(Integer id) throws FunkoNotFoundException {
        logger.info("Eliminando Objeto con ID " + id + "..........");
        String sqlQuery = "DELETE FROM Funko WHERE id = ? ";
        return CompletableFuture.supplyAsync(() -> {
            try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)) {
                stmt.setInt(1, id);
                var rs = stmt.executeUpdate();
                if (rs > 0) {
                    logger.info("Eliminado correctamente");
                    return true;
                } else {
                    throw new FunkoNotFoundException("Funko con ID " + id + " no encontrado en la BD");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteAll() {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("Eliminando Objetos de la BD......");
            String sqlQuery = "DELETE FROM Funko";
            try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)) {
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.error("ERROR: " + e.getMessage(), e);
            }
            logger.info("Objetos eliminados Correctamente");
            return null;
        });
    }


    @Override
    public CompletableFuture<Boolean> backup(String file){
        return CompletableFuture.supplyAsync(() -> {
            boolean suc = false;
            try{
            BackupManager bkcM = new BackupManager();
            logger.debug("Iniciando Backup de la Base de Datos......");
            suc = bkcM.writeFileFunko(file,findAll().get()).get();
            }catch (InterruptedException | ExecutionException e){
                logger.error("ERROR: " + e.getMessage(), e);
            }
            logger.debug("Backup Realizado Correctamente!");
            return suc;
        });
    }
}


