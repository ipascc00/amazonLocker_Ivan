package amazonlocker.iterator;

import java.util.Vector;

/**
 * Rol CONCRETE AGGREGATE del patrón Iterator.
 *
 * Adapta {@link java.util.Vector} a la interfaz {@link Aggregate}. Es la
 * colección en la que cada compuesto del locker guarda a sus hijos.
 */
public class VectorAggregate<T> extends Vector<T> implements Aggregate<T> {

    @Override
    public Iterator<T> createIterator() {
        return new ListIterator<>(this);
    }

    @Override
    public Iterator<T> createIterator(String type) {
        if ("reverse".equals(type)) {
            return new ReverseIterator<>(this);
        }
        return new ListIterator<>(this);
    }

    @Override
    public int getSize() {
        return size();
    }

    @Override
    public T getElement(int index) {
        return elementAt(index);
    }

    // addElement(T) lo hereda directamente de Vector

    @Override
    public void removeElement(int index) {
        removeElementAt(index);
    }
}
