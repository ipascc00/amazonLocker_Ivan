package amazonlocker.iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VectorAggregateTest {

    private VectorAggregate<String> agregado;

    @BeforeEach
    void setUp() {
        agregado = new VectorAggregate<>();
        agregado.addElement("A1");
        agregado.addElement("A2");
        agregado.addElement("A3");
    }

    private static <T> List<T> recorrer(Iterator<T> it) {
        List<T> resultado = new ArrayList<>();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            resultado.add(it.currentElement());
        }
        return resultado;
    }

    @Test
    void operacionesDelAgregado() {
        assertEquals(3, agregado.getSize());
        assertEquals("A2", agregado.getElement(1));
        agregado.removeElement(0);
        assertEquals(2, agregado.getSize());
        assertEquals("A2", agregado.getElement(0));
    }

    @Test
    void listIteratorRecorreEnOrden() {
        assertEquals(List.of("A1", "A2", "A3"), recorrer(agregado.createIterator()));
        assertEquals(List.of("A1", "A2", "A3"), recorrer(agregado.createIterator("list")));
    }

    @Test
    void reverseIteratorRecorreAlReves() {
        assertEquals(List.of("A3", "A2", "A1"), recorrer(agregado.createIterator("reverse")));
    }

    @Test
    void firstElementReiniciaElRecorrido() {
        Iterator<String> it = agregado.createIterator();
        it.nextElement();
        it.nextElement();
        assertEquals("A3", it.currentElement());
        it.firstElement();
        assertEquals("A1", it.currentElement());
    }

    @Test
    void variosIteradoresALaVezSonIndependientes() {
        Iterator<String> it1 = agregado.createIterator();
        Iterator<String> it2 = agregado.createIterator();
        it1.nextElement();
        assertEquals("A2", it1.currentElement());
        assertEquals("A1", it2.currentElement());
    }

    @Test
    void agregadoVacio() {
        VectorAggregate<String> vacio = new VectorAggregate<>();
        assertFalse(vacio.createIterator().hasMoreElements());
        assertFalse(vacio.createIterator("reverse").hasMoreElements());
    }

    @Test
    void currentElementFueraDeRangoLanzaExcepcion() {
        Iterator<String> it = agregado.createIterator();
        it.nextElement();
        it.nextElement();
        it.nextElement();
        assertFalse(it.hasMoreElements());
        assertThrows(IllegalStateException.class, it::currentElement);
    }

    @Test
    void nullIteratorNoTieneElementos() {
        Iterator<String> it = new NullIterator<>();
        it.firstElement();
        assertFalse(it.hasMoreElements());
        assertThrows(IllegalStateException.class, it::currentElement);
    }
}
