package org.develop.services.funkos;

import org.develop.model.Funko;
import org.develop.services.cache.Cache;

/**
 * Interfaz que extiende la interfaz Cache para proporcionar operaciones especificas de cache para objetos Funko.
 * Define el tipo de clave como Integer y el tipo de valor como Funko.
 */
interface FunkoCache extends Cache<Integer, Funko> {
}
