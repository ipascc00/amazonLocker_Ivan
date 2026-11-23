package amazonlocker.composite;

import amazonlocker.iterator.Iterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Iterator usado como cursor sobre el Composite.
 */
class CasilleroIteratorTest {

    private RedLockers red;
    private Taquilla campus;
    private Columna colA;
    private Casillero a1;

    @BeforeEach
    void setUp() {
        a1 = new Casillero("A1", Tamano.PEQUENO);
        colA = new Columna("A");
        colA.add(a1);
        colA.add(new Casillero("A2", Tamano.MEDIANO));
        Columna colB = new Columna("B");
        colB.add(new Casillero("B1", Tamano.GRANDE));
        campus = new Taquilla("Campus", "Campus");
        campus.add(colA);
        campus.add(colB);

        Columna vacia = new Columna("Vacía");
        Columna colC = new Columna("C");
        colC.add(new Casillero("C1", Tamano.GRANDE));
        Taquilla estacion = new Taquilla("Estación", "Estación");
        estacion.add(vacia);
        estacion.add(colC);

        red = new RedLockers("León");
        red.add(campus);
        red.add(new Taquilla("Sin columnas", "Obra"));
        red.add(estacion);
    }

    private static List<String> ids(Iterator<? extends ElementoLocker> it) {
        List<String> resultado = new ArrayList<>();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            resultado.add(it.currentElement().getId());
        }
        return resultado;
    }

    @Test
    void recorreTodosLosCasillerosEnProfundidad() {
        assertEquals(List.of("A1", "A2", "B1", "C1"), ids(red.createCasilleroIterator()));
    }

    @Test
    void funcionaDesdeCualquierNivel() {
        assertEquals(List.of("A1", "A2", "B1"), ids(campus.createCasilleroIterator()));
        assertEquals(List.of("A1", "A2"), ids(colA.createCasilleroIterator()));
        assertEquals(List.of("A1"), ids(a1.createCasilleroIterator()));
    }

    @Test
    void saltaGruposVacios() {
        assertEquals(List.of(), ids(new Columna("X").createCasilleroIterator()));
    }

    @Test
    void sePuedeReiniciar() {
        Iterator<Casillero> it = red.createCasilleroIterator();
        it.nextElement();
        it.nextElement();
        assertEquals("B1", it.currentElement().getId());
        it.firstElement();
        assertEquals("A1", it.currentElement().getId());
    }

    @Test
    void iteradorDeHijosDirectos() {
        assertEquals(List.of("Campus", "Sin columnas", "Estación"), ids(red.createIterator()));
        assertEquals(List.of("A2", "A1"), ids(colA.createIterator("reverse")));
    }

    @Test
    void unaHojaDevuelveUnNullIterator() {
        assertFalse(a1.createIterator().hasMoreElements());
    }
}
