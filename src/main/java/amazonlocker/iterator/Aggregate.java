package amazonlocker.iterator;

/**
 * Rol AGGREGATE del patrón Iterator.
 *
 * Define la interfaz para crear iteradores y manejar los elementos.
 */
public interface Aggregate<T> {

    Iterator<T> createIterator();

    /** Permite elegir el tipo de recorrido ("list" o "reverse"). */
    Iterator<T> createIterator(String type);

    int getSize();

    T getElement(int index);

    void addElement(T object);

    void removeElement(int index);
}
