package org.develop.locale;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Clase que proporciona funcionalidad para formatear fechas y valores monetarios utilizando un objeto
 * de localizacion especifico. El objeto de localizacion se establece en espanol (Espanol) por defecto (es_ES).
 * La clase permite formatear fechas y valores monetarios de acuerdo con la configuracion regional especifica.
 *
 * @author Alonso Cruz, Joselyn Obando
 */
public class MyLocale {
    private static final Locale locale = new Locale("es","ES");

    /**
     * Convierte un objeto LocalDate en una cadena de fecha formateada segun la configuracion regional
     * especifica definida por el objeto de localizacion.
     *
     * @param date El objeto LocalDate que se va a formatear.
     * @return Una cadena de fecha formateada segun la configuración regional especifica.
     */
    //Estoy utilizando el objeto Locale creado para definir el formato de fecha y dinero
    //el problema es que no reconoce algunos simbolos.
    public static String toLocalDate(LocalDate date) {
        return date.format(
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
        );
    }

    /**
     * Convierte un valor numerico en una cadena de dinero formateada segun la configuracion regional
     * especifica definida por el objeto de localizacion.
     *
     * @param money El valor numerico que se va a formatear como dinero.
     * @return Una cadena de dinero formateada segun la configuracion regional especifica.
     */
    public static String toLocalMoney(double money) {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).format(money);
    }

}