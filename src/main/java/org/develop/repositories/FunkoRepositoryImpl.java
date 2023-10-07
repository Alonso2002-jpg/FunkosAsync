package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;
import org.develop.model.Funko;
import org.develop.services.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class FunkoRepositoryImpl implements FunkoRepository<Funko,Integer>{
    private static FunkoRepositoryImpl instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);

    private final DatabaseManager db;
    private FunkoRepositoryImpl(DatabaseManager db){
        this.db= db;
    }

    public synchronized static FunkoRepositoryImpl getInstance(DatabaseManager db){
        if (instance == null){
            instance= new FunkoRepositoryImpl(db);
        }

        return instance;
    }
    @Override
    public CompletableFuture<Funko> save(Funko funko) throws FunkoNotSaveException{
        logger.debug("Saving Funko On Database: " + funko.getName());
        String sqlQuery = "INSERT INTO Funko (uuid,myid, name, modelo, precio, fecha_lanzamiento) VALUES (?, ?, ?, ?, ?, ?)";
        return  CompletableFuture.supplyAsync(()->{
            try (var conn = db.getConnection();
                 var stmt = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);) {
        stmt.setObject(1, funko.getUuid());
        stmt.setLong(2,funko.getMyId());
        stmt.setString(3, funko.getName());
        stmt.setString(4, funko.getModelo().toString());
        stmt.setDouble(5, funko.getPrecio());
        stmt.setDate(6, Date.valueOf(funko.getFecha_lanzamiento()));
        int res = stmt.executeUpdate();
        conn.commit();

        if (res > 0) {
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                funko.setMyId(rs.getInt(1)); // Obtiene el ID generado automáticamente
            }
            rs.close();
        }else{
            logger.error("Objeto no guardado en la base de datos");
            throw new FunkoNotSaveException("Funko con nombre "+ funko.getName() + " no almacenado en la BD");
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
        return CompletableFuture.supplyAsync(()->{
            try (var conn = db.getConnection();
            var stmt = conn.prepareStatement(sqlQuery);){
            stmt.setString(1,funko.getName());
            stmt.setString(2,funko.getModelo().toString());
            stmt.setDouble(3,funko.getPrecio());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(5,funko.getId());
            var rs = stmt.executeUpdate();
            if (rs <= 0 ){
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
    public CompletableFuture<Optional<Funko>> findById(Integer integer) throws FunkoNotFoundException {
        return null;
    }

    @Override
    public CompletableFuture<List<Funko>> findAll() throws SQLException {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> deleteById(Integer integer) throws SQLException {
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteAll() throws SQLException {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> backup(String file) throws SQLException {
        return null;
    }

    @Override
    public CompletableFuture<Funko> findByNombre(String nombre) {
        return null;
    }
}
