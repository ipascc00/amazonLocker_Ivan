package amazonlocker.iterator;

/**
 * Rol CONCRETE ITERATOR: recorre un agregado del primero al último.
 * Guarda la posición actual del recorrido.
 */
public class ListIterator<T> implements Iterator<T> {

    private final Aggregate<T> aggregate;
    private int current;

    public ListIterator(Aggregate<T> aggregate) {
        this.aggregate = aggregate;
        this.current = 0;
    }

    @Override
    public void firstElement() {
        current = 0;
    }

    @Override
    public void nextElement() {
        current = current + 1;
    }

    @Override
    public boolean hasMoreElements() {
        return current < aggregate.getSize();
    }

    @Override
    public T currentElement() {
        if (!hasMoreElements()) {
            throw new IllegalStateException("No hay más elementos");
        }
        return aggregate.getElement(current);
    }
}
