package amazonlocker.composite;

import amazonlocker.iterator.Iterator;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Rol CONCRETE ITERATOR: iterador usado como cursor sobre el Composite.
 *
 * Recorre en profundidad (de izquierda a derecha) todos los casilleros que
 * cuelgan de un elemento del locker, sea cual sea la profundidad del árbol
 * (red → taquilla → columna → casillero). Mantiene una pila con los
 * iteradores de hijos de cada nivel que está visitando.
 */
public class CasilleroIterator implements Iterator<Casillero> {

    private final ElementoLocker raiz;
    private final Deque<Iterator<ElementoLocker>> pila = new ArrayDeque<>();
    private Casillero actual;

    public CasilleroIterator(ElementoLocker raiz) {
        this.raiz = raiz;
        firstElement();
    }

    @Override
    public void firstElement() {
        pila.clear();
        actual = null;
        if (raiz instanceof Casillero) {
            actual = (Casillero) raiz;          // la raíz ya es una hoja
        } else {
            Iterator<ElementoLocker> it = raiz.createIterator();
            it.firstElement();
            pila.push(it);
            avanzar();
        }
    }

    @Override
    public void nextElement() {
        avanzar();
    }

    @Override
    public boolean hasMoreElements() {
        return actual != null;
    }

    @Override
    public Casillero currentElement() {
        if (actual == null) {
            throw new IllegalStateException("No hay más casilleros");
        }
        return actual;
    }

    /** Busca el siguiente casillero bajando por el árbol. */
    private void avanzar() {
        actual = null;
        while (!pila.isEmpty()) {
            Iterator<ElementoLocker> it = pila.peek();
            if (!it.hasMoreElements()) {
                pila.pop();                      // nivel terminado: subimos
                continue;
            }
            ElementoLocker elemento = it.currentElement();
            it.nextElement();
            if (elemento instanceof Casillero) {
                actual = (Casillero) elemento;
                return;
            }
            Iterator<ElementoLocker> hijos = elemento.createIterator();
            hijos.firstElement();
            pila.push(hijos);                    // bajamos un nivel
        }
    }
}
