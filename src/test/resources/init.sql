DROP TABLE IF EXISTS FUNKO;
CREATE TABLE IF NOT EXISTS FUNKO (
    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
    uuid UUID DEFAULT RANDOM_UUID(),
    Myid LONG NOT NULL,
    name VARCHAR(255) NOT NULL,
    modelo ENUM('MARVEL','DISNEY','ANIME','OTROS') NOT NULL,
    precio DOUBLE NOT NULL,
    fecha_lanzamiento DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
