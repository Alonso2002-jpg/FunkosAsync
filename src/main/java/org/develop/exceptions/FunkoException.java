package org.develop.exceptions;
/**
 * Excepcion personalizada que representa una situacion excepcional relacionada con
 * operaciones o problemas relacionados con objetos Funko.
 * Esta excepcion extiende la clase RuntimeException, lo que la hace una excepcion no comprobada.
 *
 * @author Alonso Cruz, Joselyn Obando
 */
public class FunkoException extends RuntimeException{

    /**
     * Construye una nueva instancia de FunkoException con el mensaje especificado.
     *
     * @param message El mensaje que describe la excepcion.
     */
    public FunkoException(String message){
        super(message);
    }
}
