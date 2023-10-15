package org.develop.exceptions;

/**
 * Excepcion personalizada que representa una situacion excepcional cuando no se puede guardar un objeto Funko.
 * Esta excepcion extiende la clase FunkoException.
 *
 * @author Alonso Cruz, Joselyn Obando
 */
public class FunkoNotSaveException extends FunkoException{

    /**
     * Construye una nueva instancia de FunkoNotSaveException con el mensaje especificado.
     *
     * @param message El mensaje que describe la excepcion.
     */
    public FunkoNotSaveException(String message) {
        super(message);
    }
}
