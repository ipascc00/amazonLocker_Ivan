package amazonlocker.iterator;

/**
 * Iterador vacío. Lo devuelven las hojas del Composite (los casilleros)
 * cuando se les pide un iterador de hijos: así el cliente puede iterar
 * sobre cualquier ElementoLocker sin preguntar si es hoja o compuesto.
 */
public class NullIterator<T> implements Iterator<T> {

    @Override
    public void firstElement() {
        // nada que recorrer
    }

    @Override
    public void nextElement() {
        // nada que recorrer
    }

    @Override
    public boolean hasMoreElements() {
        return false;
    }

    @Override
    public T currentElement() {
        throw new IllegalStateException("Un NullIterator no tiene elementos");
    }
}
