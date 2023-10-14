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