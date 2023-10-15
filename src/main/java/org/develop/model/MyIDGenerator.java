package org.develop.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que proporciona una implementacion de generacion de identificadores (IDs) unicos.
 * Esta clase utiliza un patron Singleton para garantizar una unica instancia y
 * utiliza un mecanismo de bloqueo para garantizar la atomicidad en la generacion de IDs.
 *
 * @author Alonso Cruz, Joselyn Obando
 */

public class MyIDGenerator {

    private static MyIDGenerator instance;
    private static long id = 0;

    private static final Lock locker = new ReentrantLock(true);

    /**
     * Obtiene la unica instancia de la clase MyIDGenerator.
     *
     * @return La instancia de MyIDGenerator.
     */
    private MyIDGenerator(){}


    /**
     * Genera un nuevo ID unico y lo incrementa en uno.
     *
     * @return El ID unico generado.
     */
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
