package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;
import org.develop.model.Funko;
import org.develop.model.Modelo;
import org.develop.model.MyIDGenerator;
import org.develop.services.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Clase que implementa la interfaz `FunkoRepository` y proporciona metodos para realizar operaciones CRUD en objetos Funko
 * utilizando una base de datos y un generador de ID.
 *
 * Esta clase se encarga de guardar, actualizar, buscar y eliminar Funkos en la base de datos, ademas de proporcionar
 * una operacion para buscar Funkos por nombre.
 *
 @author Alonso Cruz, Joselyn Obando
 */
public class FunkoRepositoryImpl implements FunkoRepository {
    private static FunkoRepositoryImpl instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);

    private final DatabaseManager db;
    private final MyIDGenerator idGenerator;

    private FunkoRepositoryImpl(DatabaseManager db,MyIDGenerator idGenerator) {
        this.db = db;
        this.idGenerator = idGenerator;
    }

    /**
     * Obtiene la única instancia de la clase FunkoRepositoryImpl, utilizando el patron Singleton.
     *
     * @param db          El gestor de la base de datos.
     * @param idGenerator El generador de IDs.
     * @return La instancia de FunkoRepositoryImpl.
     */
    public synchronized static FunkoRepositoryImpl getInstance(DatabaseManager db,MyIDGenerator idGenerator) {
        if (instance == null) {
            instance = new FunkoRepositoryImpl(db,idGenerator);
        }

        return instance;
    }

    /**
     * Guarda un objeto Funko en la base de datos.
     *
     * @param funko El objeto Funko que se va a guardar en la base de datos.
     * @return Un CompletableFuture que contendra el objeto Funko guardado si la operacion es exitosa.
     * @throws FunkoNotSaveException Si ocurre un error durante el proceso de guardado y el Funko no se almacena en la BD.
     */
    @Override
    public CompletableFuture<Funko> save(Funko funko) throws FunkoNotSaveException {
        String sqlQuery = "INSERT INTO Funko (uuid,myid, name, modelo, precio, fecha_lanzamiento) VALUES (?, ?, ?, ?, ?, ?)";
        return CompletableFuture.supplyAsync(() -> {
            try (var conn = db.getConnection();
                 var stmt = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                logger.debug("Saving Funko On Database: " + funko.getName());
                funko.setMyId(idGenerator.getIDandIncrement());
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

    /**
     * Actualiza un objeto Funko en la base de datos.
     *
     * @param funko El objeto Funko que se va a actualizar en la base de datos.
     * @return Un CompletableFuture que contendra el objeto Funko actualizado si la operacion es exitosa.
     * @throws FunkoNotFoundException Si el Funko con el ID especificado no se encuentra en la BD.
     */
    @Override
    public CompletableFuture<Funko> update(Funko funko){
        String sqlQuery = "UPDATE Funko SET name = ? , modelo = ?, precio = ? , updated_at = ? WHERE id = ?";
        return CompletableFuture.supplyAsync(() -> {
            try (var conn = db.getConnection();
                 var stmt = conn.prepareStatement(sqlQuery)) {
                logger.debug("Actualizando Objeto ..... ");
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
            } catch (SQLException| FunkoNotFoundException e) {
                throw new CompletionException(e);
            }

            return funko;
        });
    }

    /**
     * Busca un objeto Funko en la base de datos por su ID.
     *
     * @param id El ID del Funko que se desea buscar.
     * @return Un CompletableFuture que contendra un Optional que puede contener el Funko encontrado si existe.
     * @throws FunkoNotFoundException Si el Funko con el ID especificado no se encuentra en la BD.
     */
    @Override
    public CompletableFuture<Optional<Funko>> findById(Integer id) throws FunkoNotFoundException {

        String sqlQuery = "SELECT * FROM Funko WHERE id = ?";
        return CompletableFuture.supplyAsync(() -> {
            Optional<Funko> funk = Optional.empty();
            try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)) {
            logger.info("Buscando Objeto con ID " + id + "......");
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

    /**
     * Busca y recupera una lista de objetos Funko cuyos nombres contienen una cadena especifica.
     *
     * @param nombre La cadena que se utilizara para buscar Funkos por nombre.
     * @return Un CompletableFuture que contendra una lista de Funkos cuyos nombres coinciden con el parametro proporcionado.
     */
    @Override
    public CompletableFuture<List<Funko>> findByNombre(String nombre){

        List<Funko> funks = new ArrayList<>();
        return CompletableFuture.supplyAsync(()->{
        String sqlQuery = "SELECT * FROM Funko WHERE name LIKE ?";
        try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)){
            stmt.setString(1,"%" + nombre + "%");
            logger.info("Obtener Funko con Nombre "+nombre+".......");
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

    /**
     * Recupera una lista de todos los objetos Funko almacenados en la base de datos.
     *
     * @return Un CompletableFuture que contendra una lista de todos los Funkos almacenados en la base de datos.
     */
    @Override
    public CompletableFuture<List<Funko>> findAll() {
        String sqlQuery = "SELECT * FROM Funko";
        return CompletableFuture.supplyAsync(() -> {
            List<Funko> funks = new ArrayList<>();
            try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)) {
                logger.info("Obteniendo todos los Objetos");
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

    /**
     * Elimina un objeto Funko de la base de datos por su ID.
     *
     * @param id El ID del Funko que se desea eliminar de la base de datos.
     * @return Un CompletableFuture que contendra un valor booleano que indica si la eliminacion fue exitosa (true) o si el Funko no se encontro en la BD (false).
     * @throws FunkoNotFoundException Si el Funko con el ID especificado no se encuentra en la BD.
     */
    @Override
    public CompletableFuture<Boolean> deleteById(Integer id) {
        String sqlQuery = "DELETE FROM Funko WHERE id = ? ";
        return CompletableFuture.supplyAsync(() -> {
            try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)) {
                   logger.info("Eliminando Objeto con ID " + id + "..........");
                stmt.setInt(1, id);
                var rs = stmt.executeUpdate();
                if (rs > 0) {
                    logger.info("Eliminado correctamente");
                    return true;
                } else {
                    throw new FunkoNotFoundException("Funko con ID " + id + " no encontrado en la BD");
                }
            } catch (SQLException|FunkoNotFoundException e) {
                throw new CompletionException(e);
            }
        });
    }

    /**
     * Elimina todos los objetos Funko de la base de datos.
     *
     * @return Un CompletableFuture<Void> que se completa cuando se han eliminado todos los Funkos de la BD.
     */
    @Override
    public CompletableFuture<Void> deleteAll() {
        return CompletableFuture.runAsync(() -> {
            String sqlQuery = "DELETE FROM Funko";
            try (var conn = db.getConnection(); var stmt = conn.prepareStatement(sqlQuery)) {
                logger.info("Eliminando Objetos de la BD......");
                stmt.executeUpdate();
            } catch (SQLException e) {
                logger.error("ERROR: " + e.getMessage(), e);
            }
            logger.info("Objetos eliminados Correctamente");
        });
    }

}


