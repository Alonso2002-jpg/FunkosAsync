# Proyecto FUNKOS ASYNCRONO - Java con H2
***
Este proyecto es una aplicación simple en Java que utiliza H2 como base de datos. A continuación, se describen los pasos para configurar y ejecutar el proyecto.
## Requisitos
***
* Java 8 o superior
* Gradle
## Configuración
***

### Paso 1: Dependencias de Gradle
Agrega las siguientes dependencias a tu archivo `build.gradle`:

```kotlin 
plugins {
    id("java")
}

group = "org.develop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.projectlombok:lombok:1.18.28")
    testImplementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("org.mybatis:mybatis:3.5.13")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.test {
    useJUnitPlatform()
}
```
## Model
***
### Paso 2: Crear un enum Modelo
Crea un enum `Modelo` con sus cuatro valores enumerados `MARVEL`, `DISNEY`,`ANIME` y `OTROS`.

```java 
package org.develop.model;

public enum Modelo {
    MARVEL,DISNEY,ANIME,OTROS;
}
```
### Paso 3: Crear la clase Funko
Crea una clase `Funko` con los atributos `myId`, `id`, UUID`uuid`, `name`, Modelo `modelo`, `precio`, LocalDate `fecha_lanzamiento`, LocalDateTime `created_at`, LocalDateTime `updated_at`, tambien creamos metodo `toString` es la representacion en forma de cadena del objeto Funko, muestra los valores de algunos campos importantes.
Utilizamos `setFunko(String line)` para configurar un objeto Funko a partir de una cadena de datos `line`.

```java
package org.develop.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.develop.locale.MyLocale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Funko {
    private long myId;
    private int id;
    private UUID uuid;
    private String name;
    private Modelo modelo;
    private double precio;
    private LocalDate fecha_lanzamiento;
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();
    @Override
    public String toString() {
        return "Funko{" +
                "id=" + id +
                ", myid=" + myId +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", modelo=" + modelo +
                ", precio=" + MyLocale.toLocalMoney(precio) +
                ", fecha_lanzamiento=" + MyLocale.toLocalDate(fecha_lanzamiento) +
                '}';
    }

    public Funko setFunko(String line){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String[] lineas = line.split(",");
        setUuid(UUID.fromString(lineas[0].length()>36?lineas[0].substring(0,35):lineas[0]));
        setName(lineas[1]);
        setModelo(Modelo.valueOf(lineas[2]));
        setPrecio(Double.parseDouble(lineas[3]));
        setFecha_lanzamiento(LocalDate.parse(lineas[4],formatter));

        return this;
    }
}
```
### Paso 4: Crear la clase MyIDGenerator
Crea una clase `MyIDGenerator`, creamos una instance static tipo `MyIDGenerator` con los atributos `id`, un locker tipo `Lock` que se utiliza para sincronizar el acceso a la variable `id`. Se utiliza un `ReentrantLock` que garantiza que solo un hilo a la vez pueda incrementar el ID.
Tenemos dos metodos ´getInstance´ este método estático se utiliza para obtener la única instancia de MyIDGenerator. Si no existe una instancia previa, se crea una y se devuelve. 
Esto garantiza que siempre se utilice la misma instancia de generador de IDs en toda la aplicación. Ademas `getIDandIncrement` este método incrementa el valor del ID y lo devuelve. Antes de incrementar el ID, adquiere un bloqueo a través del objeto locker para garantizar que la operación sea atómica y que no se produzcan condiciones de carrera si varios hilos intentan obtener un ID al mismo tiempo.
El proposito de esta clase es proporcionar un mecanismo seguro y unico para generar IDs incrementales que pueden utilizarse en otras partes de la aplicacion para identificar objetos, transacciones o cualquier otra entidad que requiera un ID unico y creciente. El uso de un Singleton y un bloqueo garantiza que no se produzcan colisiones de IDs y que se mantenga la integridad de los valores de ID.

```java
package org.develop.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyIDGenerator {

    private static MyIDGenerator instance;
    private static long id = 0;

    private static final Lock locker = new ReentrantLock(true);

    private MyIDGenerator(){}

    public static MyIDGenerator getInstance(){
        if (instance == null){
            instance = new MyIDGenerator();
        }
        return instance;
    }

    public Long getIDandIncrement(){
        locker.lock();
        id++;
        locker.unlock();
        return id;
    }
}

```
## Adapters
***
### Paso 5: Crear la clase LocalDateAdapter
Crea una clase `LocalDateAdapter` es una clase personalizada que extiende `TypeAdapter` de la biblioteca Gson, que se utiliza para convertir objetos LocalDate en JSON y viceversa.
la clase LocalDateAdapter se utiliza para personalizar cómo se representan las fechas LocalDate al escribir objetos JSON utilizando la biblioteca Gson. El formato de fecha en JSON será "año-mes-día" cuando se utilice esta clase para serializar objetos `LocalDate`. Ten en cuenta que si necesitas también la capacidad de deserializar fechas desde JSON a objetos `LocalDate`.

```java 
package org.develop.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return null;
    }

}

```
### Paso 6: Crear la clase LocalDateTimeAdapter
La clase `LocalDateTimeAdapter` es una clase personalizada que extiende TypeAdapter de la biblioteca Gson y se utiliza para convertir objetos LocalDateTime a JSON y viceversa.
El método `write` toma un objeto `LocalDateTime` y lo convierte en una representación en formato JSON.
Si el valor de LocalDateTime es nulo, se escribe un valor nulo en el flujo JSON utilizando out.nullValue().
Si el valor de LocalDateTime no es nulo, se formatea la fecha y hora utilizando el patrón "yyyy-MM-dd" y se escribe en el flujo JSON con out.value(formatter.format(value)). Esto significa que la fecha y hora se representarán en JSON como una cadena con el formato "año-mes-día".
El método `read` no está implementado en esta clase, y siempre devuelve null. Esto significa que esta clase solo se utiliza para la escritura (serialización) de objetos LocalDateTime a JSON y no se encarga de la lectura (deserialización) desde JSON a objetos LocalDateTime.

```java 
package org.develop.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        return null;
    }
}

```
## Locale
***
### Paso 7: Crear la clase MyLocale
La clase MyLocale proporciona dos métodos estáticos para formatear fechas y dinero en función de un objeto Locale específico. En este caso, estás utilizando un objeto Locale con el idioma español (es) y la región España (ES).
El metodo `toLocalDate(LocalDate date)` este método toma un objeto LocalDate como entrada y formatea la fecha en un formato localizado, utilizando las configuraciones del Locale predeterminado. La fecha se formatea en un estilo medio (FormatStyle.MEDIUM), que se adapta al formato local. El objeto Locale utilizado para la formatear la fecha es el Locale predeterminado del sistema. Esto significa que la fecha se formateará en función de las configuraciones de idioma y región del sistema en el que se ejecute la aplicación.
`toLocalMoney(double money)` este método toma un valor numérico como entrada y lo formatea en una representación de moneda localizada. Utiliza el objeto Locale predeterminado del sistema para determinar el formato de moneda. Esto garantiza que la cantidad de dinero se formatee según las configuraciones de idioma y región del sistema.
```java
package org.develop.locale;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class MyLocale {
    private static final Locale locale = new Locale("es","ES");

    //Estoy utilizando el objeto Locale creado para definir el formato de fecha y dinero
    //el problema es que no reconoce algunos simbolos.
    public static String toLocalDate(LocalDate date) {
        return date.format(
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
        );
    }

    public static String toLocalMoney(double money) {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(money);
    }

}
```
## Exceptions
***
### Paso 8: Crear la clase FunkoException
La clase `FunkoException` una clase de excepción personalizada que extiende la clase `RuntimeException`.
`FunkoException(String message)`: La clase tiene un constructor que toma un mensaje como argumento. Este mensaje debería ser una descripción o información adicional sobre la excepción que se está lanzando. Cuando se crea una instancia de FunkoException con un mensaje, ese mensaje se pasa a la clase base RuntimeException (superclase) usando super(message). Esto significa que el mensaje se almacena en la excepción y se puede recuperar más tarde cuando se capture y maneje la excepción.
```java
package org.develop.exceptions;

public class FunkoException extends RuntimeException{
    public FunkoException(String message){
        super(message);
    }
}
```
### Paso 9: Crear la clase FunkoNotFoundException

La clase `FunkoNotFoundException` es una subclase de la clase `FunkoException`, y su propósito es manejar excepciones específicas relacionadas con la situación en la que no se encuentra un objeto `Funko` dentro de tu aplicación. Esta clase extiende `FunkoException`, que a su vez extiende RuntimeException.
```java
package org.develop.exceptions;

public class FunkoNotFoundException extends FunkoException{

    public FunkoNotFoundException(String message) {
        super(message);
    }
}

```
### Paso 10: Crear la clase FunkoNotSaveException
La clase `FunkoNotSaveException` es otra subclase de la clase `FunkoException` y se utiliza para manejar excepciones específicas relacionadas con la incapacidad de guardar un objeto `Funko` en tu aplicación o sistema. Al igual que la clase `FunkoNotFoundException`, esta clase extiende `FunkoException`, que, a su vez, extiende `RuntimeException`.
```java
package org.develop.exceptions;

public class FunkoNotSaveException extends FunkoException{
    public FunkoNotSaveException(String message) {
        super(message);
    }
}
```
## Servicios de Almacenamiento
***
### Paso 11: Crear la clase DatabaseManager
La clase `DatabaseManager` es una clase que gestiona una base de datos utilizando la biblioteca `HikariCP` y permite a tu aplicación conectarse a la base de datos, ejecutar scripts de inicialización y obtener conexiones.
La clase `DatabaseManager` con los atributos Logger `logger`, HikariDataSource `dataSource`, `serverUrl`, `dataBaseName`, `chargeInit`, `conURL`, `initScript`.

```java
package org.develop.services.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private final HikariDataSource dataSource;
    private String serverUrl;
    private String dataBaseName;
    private boolean chargeInit;
    private String conURL;
    private String initScript;


    private DatabaseManager(){
            configFromProperties();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(conURL);
            dataSource = new HikariDataSource(config);
        try (Connection conn = dataSource.getConnection()){
            if (chargeInit){
                executeScript(conn,initScript,true);
            }
            System.out.println("Successfully");
        }catch (SQLException e) {
            logger.error("Error: " + e.getMessage(),e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized DatabaseManager getInstance(){
        if (instance==null){
            instance=new DatabaseManager();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        return  dataSource.getConnection();
    }


    private synchronized void configFromProperties(){
        try{
        Properties properties = new Properties();
        properties.load(DatabaseManager.class.getClassLoader().getResourceAsStream("config.properties"));

        serverUrl= properties.getProperty("database.url","jdbc:h2");
        dataBaseName = properties.getProperty("database.name","Funkos");
        chargeInit =Boolean.parseBoolean(properties.getProperty("database.initDatabase","false"));
        conURL =properties.getProperty("database.connectionUrl", serverUrl + ":"+dataBaseName + ".db");
            System.out.println(conURL);
        initScript=properties.getProperty("database.initScript","init.sql");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized void executeScript(Connection conn, String script, boolean logWriter) throws IOException, SQLException {
        ScriptRunner runner = new ScriptRunner(conn);
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(script);
        if (inputStream != null) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            runner.setLogWriter(logWriter ? new PrintWriter(System.out) : null);
            runner.runScript(reader);
        } else {
            throw new FileNotFoundException("Script not found: " + script);
        }
    }
}
```
## Repositories
***
### Paso 12: CRUDRepository

La interfaz `CRUDRepository` define un conjunto de métodos que proporcionan operaciones básicas de creación, lectura, actualización y eliminación (CRUD) para trabajar con objetos de tipo T en una base de datos o repositorio. Además, incluye un método adicional para realizar una operación de respaldo.
```java
package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CRUDRepository <T,ID>{
        // Guardar
    CompletableFuture<T> save(T t) throws SQLException, FunkoNotSaveException;

    // Actualizar
    CompletableFuture<T> update(T t) throws SQLException, FunkoNotFoundException;

    // Buscar por ID
    CompletableFuture<Optional<T>> findById(ID id) throws SQLException, FunkoNotFoundException;

    // Buscar todos
    CompletableFuture<List<T>> findAll() throws SQLException;

    // Borrar por ID
    CompletableFuture<Boolean> deleteById(ID id) throws SQLException, FunkoNotFoundException;

    // Borrar todos
    CompletableFuture<Void> deleteAll() throws SQLException;

    // Hacer Backup
}
```
### Paso 13: FunkoRepository
La interfaz `FunkoRepository` extiende la interfaz `CRUDRepository` y agrega un método de búsqueda específico para buscar objetos `Funko` por su nombre. Esto permite realizar operaciones de búsqueda personalizadas en la base de datos o en el repositorio en función del nombre del `Funko`. Las implementaciones concretas de esta interfaz proporcionarán la lógica necesaria para realizar estas operaciones de búsqueda.

```java
package org.develop.repositories;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.model.Funko;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FunkoRepository extends CRUDRepository<Funko,Integer>{
    CompletableFuture<List<Funko>> findByNombre(String nombre);
}
```
### Paso 14: FunkoRepositoryImpl
La clase `FunkoRepositoryImpl` proporciona una implementación de las operaciones CRUD (crear, leer, actualizar, eliminar) para objetos Funko en una base de datos. Estas operaciones se realizan de manera asincrónica utilizando CompletableFuture y pueden lanzar excepciones específicas para manejar errores y excepciones. 
La implementación se basa en SQL para interactuar con la base de datos subyacente.
```java
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

public class FunkoRepositoryImpl implements FunkoRepository {
    private static FunkoRepositoryImpl instance;
    private final Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);

    private final DatabaseManager db;
    private final MyIDGenerator idGenerator;

    private FunkoRepositoryImpl(DatabaseManager db,MyIDGenerator idGenerator) {
        this.db = db;
        this.idGenerator = idGenerator;
    }

    public synchronized static FunkoRepositoryImpl getInstance(DatabaseManager db,MyIDGenerator idGenerator) {
        if (instance == null) {
            instance = new FunkoRepositoryImpl(db,idGenerator);
        }

        return instance;
    }

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

    @Override
    public CompletableFuture<Funko> update(Funko funko) throws FunkoNotFoundException {
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return funko;
        });
    }

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

    @Override
    public CompletableFuture<Boolean> deleteById(Integer id) throws FunkoNotFoundException {
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
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

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
```
### Paso 15: BackupManager
La interfaz ´BackupManager<T>´ proporciona métodos para realizar operaciones de respaldo de objetos de tipo T (en este caso, objetos Funko). Permite escribir objetos en un archivo de respaldo y leer objetos desde un archivo de respaldo. Estas operaciones pueden realizarse de manera asincrónica utilizando CompletableFuture. La implementación concreta de esta interfaz determinará cómo se realizan estas operaciones de respaldo y lectura para objetos Funko.
```java
package org.develop.services.files;

import org.develop.model.Funko;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BackupManager <T>{
    CompletableFuture<Boolean> writeFileFunko(String nomFile, List<T> funks);
    CompletableFuture<List<Funko>> readFileFunko(String nomFile) throws InterruptedException;
}
```
### Paso 16: BackupManagerImpl
La clase `BackupManagerImpl` proporciona métodos para realizar operaciones de respaldo de objetos `Funko` en archivos. Puede escribir objetos `Funko` en un archivo en formato `JSON` y leer objetos Funko desde un archivo en formato JSON. Estas operaciones se realizan de manera asincrónica utilizando CompletableFuture. La implementación utiliza la biblioteca Gson para serializar y deserializar objetos en formato JSON. La implementación sigue un patrón Singleton, lo que significa que solo puede haber una instancia de esta clase en la aplicación.
```java
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

public class BackupManagerImpl implements BackupManager<Funko>{

    private static BackupManagerImpl instance;
    private final Logger logger = LoggerFactory.getLogger(BackupManagerImpl.class);

    private BackupManagerImpl() {
    }

    public static BackupManagerImpl getInstance() {
        if (instance == null) {
            instance= new BackupManagerImpl();
        }
        return instance;
    }
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
```
## Servicios de Almacenamiento Funkos
***
### Paso 17: FunkoCache
La interfaz `FunkoCache` extiende una interfaz genérica llamada Cache y se especializa para trabajar con objetos de tipo Funko utilizando identificadores de tipo Integer. Las implementaciones concretas de esta interfaz proporcionarán la funcionalidad para almacenar y gestionar objetos `Funko en una estructura de caché.
```java
package org.develop.services.funkos;

import org.develop.model.Funko;
import org.develop.services.cache.Cache;

interface FunkoCache extends Cache<Integer, Funko> {
}
```
### Paso 18: FunkoCacheImpl
La clase `FunkoCacheImpl` proporciona una implementación de una caché para objetos `Funko`. La caché se configura para eliminar automáticamente los elementos más antiguos cuando se alcanza el tamaño máximo. Además, se programa una limpieza periódica para eliminar objetos caducados de la caché. La implementación de esta caché utiliza CompletableFuture para realizar operaciones de forma asincrónica.
```java
package org.develop.services.funkos;

import org.develop.model.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FunkoCacheImpl implements FunkoCache{
    private final Logger logger = LoggerFactory.getLogger(FunkoCacheImpl.class);
    private final int maxSize;
    private final Map<Integer,Funko> cache;
    private final ScheduledExecutorService cleaner;

    public FunkoCacheImpl(int maxSize){
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(maxSize,0.75f,true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Funko> eldest) {
                return size() > maxSize;
            }
        };
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear,2,2, TimeUnit.MINUTES);
    }
    @Override
    public CompletableFuture<Void> put(Integer key, Funko value) {
       return CompletableFuture.runAsync(()->{
           logger.debug("Añadiendo Funko al Cache");
           cache.put(key,value);
       });
    }

    @Override
    public CompletableFuture<Optional<Funko>> get(Integer key) {
        return CompletableFuture.supplyAsync(()->{
           logger.debug("Obteniendo Funko de la Cache");
           if (cache.get(key) != null){
            return Optional.of(cache.get(key));
           }
           return Optional.empty();
       });
    }

    @Override
    public CompletableFuture<Void> remove(Integer key) {
        return CompletableFuture.runAsync(()->{
           logger.debug("Borrando Funko de la Cache");
           cache.remove(key);
       });
    }

    @Override
    public CompletableFuture<Void> clear() {
        return CompletableFuture.runAsync(()->{
           logger.debug("Limpiando la cache");
           cache.entrySet().removeIf(entry -> {
            boolean shouldRemove = entry.getValue().getUpdated_at().plusMinutes(1).isBefore(LocalDateTime.now());
            if (shouldRemove) {
                logger.debug("Autoeliminando por caducidad alumno de cache con id: " + entry.getKey());
            }
            return shouldRemove;
        });
       });
    }

    @Override
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.supplyAsync(()->{
           cleaner.shutdown();
            return null;
        });
    }
}
```
### Paso 19: FunkoService
La interfaz `FunkoService` define métodos que permiten realizar operaciones relacionadas con la gestión de objetos Funko. Los métodos pueden lanzar excepciones relacionadas con SQL y manejo de excepciones asincrónicas. Las implementaciones concretas de esta interfaz proporcionarán la lógica para realizar estas operaciones.
```java
package org.develop.services.funkos;

import org.develop.exceptions.FunkoNotFoundException;
import org.develop.exceptions.FunkoNotSaveException;
import org.develop.model.Funko;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface FunkoService {
CompletableFuture<List<Funko>> findAll() throws SQLException, ExecutionException, InterruptedException;

    CompletableFuture<List<Funko>> findAllByNombre(String nombre) throws SQLException, ExecutionException, InterruptedException;

    CompletableFuture<Optional<Funko>> findById(int id) throws SQLException, ExecutionException, InterruptedException, FunkoNotFoundException;

    CompletableFuture<Funko> save(Funko funko) throws SQLException, FunkoNotSaveException, ExecutionException, InterruptedException;

    CompletableFuture<Funko> update(Funko funko) throws SQLException, FunkoNotFoundException, ExecutionException, InterruptedException;

    CompletableFuture<Boolean> deleteById(int id) throws SQLException,FunkoNotFoundException ,ExecutionException, InterruptedException;

    CompletableFuture<Void> deleteAll() throws SQLException, ExecutionException, InterruptedException;

    CompletableFuture<Boolean> backup(String file) throws SQLException, ExecutionException, InterruptedException;

    CompletableFuture<List<Funko>> imported(String file) throws InterruptedException;
}
```
### Paso 20: FunkoServiceImpl
La clase `FunkoServiceImpl` proporciona la lógica para interactuar con objetos Funko, realizar operaciones de base de datos, gestionar una caché y realizar operaciones de copia de seguridad e importación de objetos Funko. La implementación de esta clase utiliza CompletableFuture para realizar operaciones de forma asincrónica cuando es necesario.
```java

public class FunkoServiceImpl implements FunkoService{
    private static final int CACHE_SIZE =10;

    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private static FunkoServiceImpl instance;
    private final FunkoCache cache;
    private final FunkoRepository funkoRepository;
    private final BackupManagerImpl backupManager;

    private FunkoServiceImpl(BackupManagerImpl backupManager, FunkoRepository funkoRepository){
        this.funkoRepository =funkoRepository;
        this.backupManager = backupManager;
        this.cache = new FunkoCacheImpl(CACHE_SIZE);
    }
    public synchronized static FunkoServiceImpl getInstance(BackupManagerImpl backupManager, FunkoRepository funkoRepository){
        if (instance == null){
            instance = new FunkoServiceImpl(backupManager,funkoRepository);
        }
        return instance;
    }
    @Override
    public CompletableFuture<List<Funko>> findAll() throws SQLException {
        logger.debug("Obteniendo todos los funkos");
        return funkoRepository.findAll();
    }

    @Override
    public CompletableFuture<List<Funko>> findAllByNombre(String nombre) {
        logger.debug("Obteniendo los funkos que coincidan con: " + nombre);
        return funkoRepository.findByNombre(nombre);
    }

    @Override
    public CompletableFuture<Optional<Funko>> findById(int id) throws FunkoNotFoundException, ExecutionException, InterruptedException, SQLException {
        logger.debug("Obteniendo el funko con Id: " + id);
        var fk = cache.get(id);
        if (fk != null && fk.get().isPresent()  ) {
            return fk;
        } else {
            return funkoRepository.findById(id);
        }

    }

    @Override
    public CompletableFuture<Funko> save(Funko funko) throws FunkoNotSaveException, SQLException, ExecutionException, InterruptedException {
        logger.debug("Guardando Funko en la base de datos: " + funko);
        var funk = funkoRepository.save(funko);
        cache.put(funko.getId(),funko);
        return funk;
    }

    @Override
    public CompletableFuture<Funko> update(Funko funko) throws FunkoNotFoundException, SQLException {
        logger.debug("Actualizando Funko....");
        var funk = funkoRepository.update(funko);
        cache.put(funko.getId(),funko);
        return funk;
    }

    @Override
    public CompletableFuture<Boolean> deleteById(int id) throws FunkoNotFoundException, SQLException, ExecutionException, InterruptedException {
        logger.debug("Eliminando Funko con id: " + id);
        var res = funkoRepository.deleteById(id);
        if (res.get()) {
            cache.remove(id);
        }
        return res;
    }

    @Override
    public CompletableFuture<Void> deleteAll(){
       return CompletableFuture.runAsync(()->{
            try {
                logger.debug("Eliminando todos los elementos de la Base de Datos....");
                funkoRepository.deleteAll().get();
                cache.clear();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

        @Override
    public CompletableFuture<Boolean> backup(String file) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Iniciando Backup de la Base de Datos......");
        var suc = backupManager.writeFileFunko(file,findAll().get());
        logger.debug("Backup Realizado Correctamente!");
        return suc;
    }

    @Override
    public CompletableFuture<List<Funko>> imported(String file) throws InterruptedException {
        return backupManager.readFileFunko(file);
    }
}
```
Y luego en tu main

```java
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
//        //Obteniendo Funko con ID: 10
//        var funkoId = fknServ.findById(10);
//        System.out.println(funkoId.get());
//        //Obteninedo Funko con ID erroneo
//        var funkoBadId = fknServ.findById(200);
//        System.out.println(funkoBadId.get());
//
//        //Actualizando Funko
//        Funko fkn = funkoId.get().get();
//        fkn.setName("Funko Actualizado");
//        var updateFun = fknServ.update(fkn);
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

        var allFunks = fknServ.findAll();

        var moreExpFun = allFunks.get().stream()
                        .max(Comparator.comparingDouble(Funko::getPrecio));
        main.logger.debug("Funko mas Caro");
        System.out.println(moreExpFun.orElse(new Funko()));

        main.logger.debug("Media de precio de Funkos");
        var funkPricAverage = allFunks.get().stream()
                .mapToDouble(Funko::getPrecio)
                .average();
        System.out.println("Media de precios : " + funkPricAverage.orElse(0.0));

        main.logger.debug("Funkos Agrupados por Modelo");
        var funkType= allFunks.get().stream()
                .map(Funko::getModelo)
                .distinct()
                .collect(Collectors.toMap(fk->fk,
                        fk-> {
                            try {
                                return allFunks.get().stream()
                                .filter(fkT -> fkT.getModelo().equals(fk))
                                .toList();
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }));
        funkType.forEach((a,b) -> System.out.println(a + " : " + b));

        main.logger.debug("Numero de Funkos por Modelo");
        var funkCountType = allFunks.get().stream()
                .map(Funko::getModelo)
                .collect(Collectors.groupingBy(fk->fk,Collectors.counting()));
        funkCountType.forEach((a,b) -> System.out.println(a + " : " + b));

        main.logger.debug("Funkos Lanzados en el 2023");
        var funkLaunchDate = allFunks.get().stream()
                .filter(fk -> fk.getFecha_lanzamiento().toString().contains("2023"))
                .toList();
        funkLaunchDate.forEach(System.out::println);

        main.logger.debug("Funkos de Stitch");
        var count = allFunks.get().stream()
        .filter(fk -> fk.getName().contains("Stitch"))
        .count();

        var stitchFunkos = allFunks.get().stream()
                .filter(fk -> fk.getName().contains("Stitch"))
                .collect(Collectors.groupingBy(
                        fk -> count,  // Utiliza la cantidad como clave
                        Collectors.toList()
                ));

        System.out.println("Funkos de Stitch : ");
        stitchFunkos.forEach((a,b) -> System.out.println(a + " : " + b));
    }

}
```





 