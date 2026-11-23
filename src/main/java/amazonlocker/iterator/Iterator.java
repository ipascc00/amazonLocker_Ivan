package amazonlocker.iterator;

/**
 * Rol ITERATOR del patrón Iterator.
 *
 * Define la interfaz para acceder y recorrer secuencialmente los elementos
 * de un agregado sin conocer su estructura interna.
 */
public interface Iterator<T> {

    /** Sitúa el cursor en el primer elemento. */
    void firstElement();

    /** Avanza el cursor al siguiente elemento. */
    void nextElement();

    /** Indica si el cursor apunta a un elemento válido. */
    boolean hasMoreElements();

    /** Devuelve el elemento al que apunta el cursor. */
    T currentElement();
}
