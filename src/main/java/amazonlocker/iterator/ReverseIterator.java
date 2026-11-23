package amazonlocker.iterator;

/**
 * Rol CONCRETE ITERATOR: recorre un agregado del último al primero.
 * Muestra que un mismo agregado admite distintas formas de recorrido
 * (p. ej. revisar una columna de abajo a arriba).
 */
public class ReverseIterator<T> implements Iterator<T> {

    private final Aggregate<T> aggregate;
    private int current;

    public ReverseIterator(Aggregate<T> aggregate) {
        this.aggregate = aggregate;
        firstElement();
    }

    @Override
    public void firstElement() {
        current = aggregate.getSize() - 1;
    }

    @Override
    public void nextElement() {
        current = current - 1;
    }

    @Override
    public boolean hasMoreElements() {
        return current >= 0 && current < aggregate.getSize();
    }

    @Override
    public T currentElement() {
        if (!hasMoreElements()) {
            throw new IllegalStateException("No hay más elementos");
        }
        return aggregate.getElement(current);
    }
}
