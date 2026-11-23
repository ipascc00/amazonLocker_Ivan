package amazonlocker.composite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrupoLockerTest {

    private Casillero a1, a2, a3, b1, c1;
    private Columna colA, colB, colC;
    private Taquilla campus, estacion;
    private RedLockers leon;

    @BeforeEach
    void setUp() {
        a1 = new Casillero("A1", Tamano.PEQUENO);
        a2 = new Casillero("A2", Tamano.MEDIANO);
        a3 = new Casillero("A3", Tamano.GRANDE);
        b1 = new Casillero("B1", Tamano.PEQUENO);
        c1 = new Casillero("C1", Tamano.GRANDE);

        colA = new Columna("Columna A");
        colA.add(a1);
        colA.add(a2);
        colA.add(a3);
        colB = new Columna("Columna B");
        colB.add(b1);
        colC = new Columna("Columna C");
        colC.add(c1);

        campus = new Taquilla("Taquilla Campus", "Campus");
        campus.add(colA);
        campus.add(colB);
        estacion = new Taquilla("Taquilla Estación", "Estación");
        estacion.add(colC);

        leon = new RedLockers("Red León");
        leon.add(campus);
        leon.add(estacion);
    }

    // ---------------- operaciones recursivas ----------------

    @Test
    void totalCasillerosSeCalculaRecursivamente() {
        assertEquals(3, colA.getTotalCasilleros());
        assertEquals(4, campus.getTotalCasilleros());
        assertEquals(5, leon.getTotalCasilleros());
    }

    @Test
    void casillerosLibresReflejaLasOcupaciones() {
        a1.depositar("P1");
        c1.depositar("P2");
        assertEquals(2, colA.getCasillerosLibres());
        assertEquals(3, campus.getCasillerosLibres());
        assertEquals(0, estacion.getCasillerosLibres());
        assertEquals(3, leon.getCasillerosLibres());
    }

    @Test
    void buscarCasilleroLibreRecorreElArbol() {
        assertSame(a3, leon.buscarCasilleroLibre(Tamano.GRANDE));
        a3.depositar("P1");
        assertSame(c1, leon.buscarCasilleroLibre(Tamano.GRANDE));
        c1.depositar("P2");
        assertNull(leon.buscarCasilleroLibre(Tamano.GRANDE));
    }

    @Test
    void grupoVacioNoTieneCasilleros() {
        Columna vacia = new Columna("Vacía");
        assertEquals(0, vacia.getTotalCasilleros());
        assertEquals(0, vacia.getCasillerosLibres());
        assertNull(vacia.buscarCasilleroLibre(Tamano.PEQUENO));
    }

    // ---------------- manejo de hijos ----------------

    @Test
    void addYGetHijo() {
        assertEquals(3, colA.getNumeroHijos());
        assertSame(a2, colA.getHijo(1));
        assertTrue(colA.esCompuesto());
    }

    @Test
    void removeEliminaElHijoYSuPadre() {
        colA.remove(a2);
        assertEquals(2, colA.getNumeroHijos());
        assertNull(a2.getPadre());
        assertEquals(4, leon.getTotalCasilleros());
    }

    @Test
    void elPadreSeAsignaAlAnadir() {
        assertSame(colA, a1.getPadre());
        assertSame(campus, colA.getPadre());
        assertSame(leon, campus.getPadre());
        assertNull(leon.getPadre());
    }

    @Test
    void rutaCompletaDeUnCasillero() {
        assertEquals("Red León > Taquilla Campus > Columna A > A2", a2.getRuta());
    }

    @Test
    void unElementoNoPuedeTenerDosPadres() {
        assertThrows(IllegalStateException.class, () -> colB.add(a1));
    }

    @Test
    void noSePuedenCrearCiclos() {
        RedLockers espana = new RedLockers("España");
        espana.add(leon);
        assertThrows(IllegalArgumentException.class, () -> leon.add(espana));
        assertThrows(IllegalArgumentException.class, () -> leon.add(leon));
    }

    @Test
    void noSePuedeAnadirNulo() {
        assertThrows(IllegalArgumentException.class, () -> colA.add(null));
    }

    // ---------------- comprobaciones dinámicas de tipo ----------------

    @Test
    void columnaSoloAdmiteCasilleros() {
        assertThrows(IllegalArgumentException.class, () -> colA.add(new Columna("X")));
    }

    @Test
    void taquillaSoloAdmiteColumnas() {
        assertThrows(IllegalArgumentException.class, () -> campus.add(new Casillero("Z", Tamano.GRANDE)));
    }

    @Test
    void redAdmiteTaquillasYOtrasRedes() {
        RedLockers espana = new RedLockers("España");
        espana.add(leon);
        espana.add(new Taquilla("Taquilla Madrid", "Madrid"));
        assertEquals(2, espana.getNumeroHijos());
        assertEquals(5, espana.getTotalCasilleros());
        assertThrows(IllegalArgumentException.class, () -> espana.add(new Columna("Y")));
    }

    @Test
    void mostrarIncluyeTodosLosNiveles() {
        a1.depositar("P1");
        String arbol = leon.mostrar();
        assertTrue(arbol.startsWith("RedLockers Red León (4/5 libres)"));
        assertTrue(arbol.contains("  Taquilla Taquilla Campus (3/4 libres)"));
        assertTrue(arbol.contains("    Columna Columna A (2/3 libres)"));
        assertTrue(arbol.contains("      [A1 PEQUENO ocupado: P1]"));
        assertTrue(arbol.contains("      [C1 GRANDE libre]"));
    }
}
