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

    /**
     * Obtiene una instancia unica de DatabaseManager.
     *
     * @return Una instancia de DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance(){
        if (instance==null){
            instance=new DatabaseManager();
        }
        return instance;
    }

    /**
     * Obtiene una conexion a la base de datos.
     *
     * @return Una conexion a la base de datos.
     * @throws SQLException Si se produce un error al establecer la conexion.
     */
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

    /**
     * Ejecuta un script SQL en la base de datos.
     *
     * @param conn       La conexion a la base de datos en la que se ejecutara el script.
     * @param script     El nombre del archivo que contiene el script SQL.
     * @param logWriter  Indica si se debe registrar la salida en la consola.
     * @throws IOException Si se produce un error de lectura del script SQL.
     * @throws SQLException Si se produce un error al ejecutar el script SQL.
     */
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
