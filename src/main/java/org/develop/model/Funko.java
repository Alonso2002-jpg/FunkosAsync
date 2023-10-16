package org.develop.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.develop.locale.MyLocale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Clase que representa un objeto Funko con atributos como identificador, nombre, modelo, precio, fecha de lanzamiento, etc.
 * Esta clase proporciona metodos para formatear y mostrar informacion sobre un Funko.
 *
 * @author Alonso Cruz, Joselyn Obando
 */
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

    /**
     * Genera una representacion en formato de cadena del objeto Funko.
     *
     * @return Una cadena que representa el objeto Funko, incluyendo su identificador, nombre, modelo, precio,
     *         fecha de lanzamiento, etc., formateados según la configuracion regional especifica.
     */
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


    /**
     * Establece los atributos de un objeto Funko a partir de una linea de datos en formato CSV.
     *
     * @param line La linea de datos en formato CSV que contiene informacion sobre el Funko.
     * @return El objeto Funko con sus atributos configurados segun la linea de datos.
     */
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
