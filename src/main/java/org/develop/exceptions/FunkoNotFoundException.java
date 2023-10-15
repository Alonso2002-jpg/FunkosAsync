package org.develop.exceptions;

/**
 * Excepcion personalizada que representa una situacion excepcional cuando un objeto Funko no se encuentra.
 * Esta excepcion extiende la clase FunkoException.
 *
 * @author Alonso Cruz, Joselyn Obando
 */
public class FunkoNotFoundException extends FunkoException{


    /**
     * Construye una nueva instancia de FunkoNotFoundException con el mensaje especificado.
     *
     * @param message El mensaje que describe la excepcion.
     */
    public FunkoNotFoundException(String message) {
        super(message);
    }
}
