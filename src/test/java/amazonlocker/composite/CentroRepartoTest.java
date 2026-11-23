package amazonlocker.composite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * El cliente trata hojas y compuestos de forma uniforme.
 */
class CentroRepartoTest {

    private CentroReparto centro;
    private Casillero suelto;
    private Columna columna;
    private Taquilla taquilla;

    @BeforeEach
    void setUp() {
        centro = new CentroReparto();
        suelto = new Casillero("S1", Tamano.GRANDE);

        columna = new Columna("Columna A");
        columna.add(new Casillero("A1", Tamano.PEQUENO));
        columna.add(new Casillero("A2", Tamano.PEQUENO));
        columna.add(new Casillero("A3", Tamano.GRANDE));
        columna.add(new Casillero("A4", Tamano.MEDIANO));

        taquilla = new Taquilla("Taquilla Campus", "Campus");
        taquilla.add(columna);
    }

    @Test
    void asignaPaqueteAUnaHoja() {
        Casillero asignado = centro.asignarPaquete(suelto, "P1", Tamano.GRANDE);
        assertSame(suelto, asignado);
        assertEquals("P1", suelto.getPaquete());
    }

    @Test
    void asignaPaqueteAUnCompuesto() {
        Casillero asignado = centro.asignarPaquete(taquilla, "P1", Tamano.PEQUENO);
        assertNotNull(asignado);
        assertEquals("A1", asignado.getId());
        assertEquals("P1", asignado.getPaquete());
        assertEquals(3, taquilla.getCasillerosLibres());
    }

    @Test
    void devuelveNullSiNoHayHueco() {
        centro.asignarPaquete(taquilla, "P1", Tamano.GRANDE);
        assertNull(centro.asignarPaquete(taquilla, "P2", Tamano.GRANDE));
    }

    @Test
    void porcentajeDeOcupacionUniforme() {
        assertEquals(0.0, centro.porcentajeOcupacion(taquilla), 1e-9);
        centro.asignarPaquete(taquilla, "P1", Tamano.PEQUENO);
        assertEquals(25.0, centro.porcentajeOcupacion(taquilla), 1e-9);
        assertEquals(25.0, centro.porcentajeOcupacion(columna), 1e-9);

        centro.asignarPaquete(suelto, "P2", Tamano.GRANDE);
        assertEquals(100.0, centro.porcentajeOcupacion(suelto), 1e-9);
    }

    @Test
    void porcentajeDeGrupoVacioEsCero() {
        assertEquals(0.0, centro.porcentajeOcupacion(new Columna("Vacía")), 1e-9);
    }

    @Test
    void localizaUnPaqueteConElIterador() {
        centro.asignarPaquete(taquilla, "P1", Tamano.PEQUENO);
        centro.asignarPaquete(taquilla, "P2", Tamano.MEDIANO);
        assertEquals("A4", centro.localizarPaquete(taquilla, "P2").getId());
        assertNull(centro.localizarPaquete(taquilla, "NO-EXISTE"));
    }

    @Test
    void listaLosCasillerosOcupados() {
        assertTrue(centro.casillerosOcupados(taquilla).isEmpty());
        centro.asignarPaquete(taquilla, "P1", Tamano.GRANDE);
        centro.asignarPaquete(taquilla, "P2", Tamano.PEQUENO);
        List<Casillero> ocupados = centro.casillerosOcupados(taquilla);
        assertEquals(2, ocupados.size());
        assertEquals("A1", ocupados.get(0).getId());
        assertEquals("A3", ocupados.get(1).getId());
    }
}
