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
    private HikariDataSource dataSource;
    private PreparedStatement preparedStatement;
    private Connection connection;
    private String serverUrl;
    private String dataBaseName;
    private boolean chargeInit;
    private String conURL;
    private String initScript;


    private DatabaseManager(){
        try {
            configFromProperties();
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(conURL);
            this.dataSource = new HikariDataSource(config);
            openConnection();
            if (chargeInit){
                executeScript(initScript,true);
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
        if (connection == null || connection.isClosed()){
            try{
                openConnection();
            }catch (SQLException e){
                logger.error("Error: " + e.getMessage(),e);
            }
        }
        return connection;
    }

    private synchronized void openConnection() throws SQLException{
        connection = dataSource.getConnection();
    }

    public synchronized void closeConnection() throws SQLException{
        if (preparedStatement != null){ preparedStatement.close();}
        connection.close();
    }

    private synchronized void configFromProperties(){
        try{
        Properties properties = new Properties();
        properties.load(DatabaseManager.class.getClassLoader().getResourceAsStream("config.properties"));

        serverUrl= properties.getProperty("database.url","jdbc:h2");
        dataBaseName = properties.getProperty("database.name","Funkos");
        chargeInit =Boolean.parseBoolean(properties.getProperty("database.initDatabase","false"));
        conURL =properties.getProperty("database.connectionUrl", serverUrl + ":"+dataBaseName + ".db");
        initScript=properties.getProperty("database.initScript","init.sql");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public synchronized void executeScript(String script, boolean logWriter) throws IOException, SQLException {
        ScriptRunner runner = new ScriptRunner(connection);
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
