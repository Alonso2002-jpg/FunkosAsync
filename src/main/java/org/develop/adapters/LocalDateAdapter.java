package org.develop.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Esta clase proporciona una adaptacion personalizada para la serializacion y deserializacion
 * de objetos LocalDate a y desde formato JSON utilizando la libreria Gson.
 *
 * Utiliza un patron de fecha predefinido "yyyy-MM-dd" para la serializacion.
 *
 * @author Alonso Cruz, Joselyn Obando
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * Serializa un objeto LocalDate a formato JSON.
     *
     * @param out   El escritor JSON de destino.
     * @param value El objeto LocalDate a serializar.
     * @throws IOException Si hay un problema al escribir en el flujo de salida JSON.
     */
    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(formatter.format(value));
        }
    }

    /**
     * Deserializa un objeto LocalDate desde formato JSON (no implementado en este adaptador).
     *
     * @param in El lector JSON de origen.
     * @return Siempre devuelve null ya que la deserializacion no esta implementada.
     * @throws IOException Si hay un problema al leer desde el flujo de entrada JSON.
     */
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        return null;
    }

}
