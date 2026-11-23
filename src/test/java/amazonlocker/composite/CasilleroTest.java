package amazonlocker.composite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CasilleroTest {

    private Casillero casillero;

    @BeforeEach
    void setUp() {
        casillero = new Casillero("A1", Tamano.MEDIANO);
    }

    @Test
    void nuevoCasilleroEstaLibre() {
        assertTrue(casillero.estaLibre());
        assertEquals(1, casillero.getTotalCasilleros());
        assertEquals(1, casillero.getCasillerosLibres());
    }

    @Test
    void depositarOcupaElCasillero() {
        casillero.depositar("PKG-1");
        assertFalse(casillero.estaLibre());
        assertEquals("PKG-1", casillero.getPaquete());
        assertEquals(0, casillero.getCasillerosLibres());
    }

    @Test
    void retirarLiberaElCasillero() {
        casillero.depositar("PKG-1");
        assertEquals("PKG-1", casillero.retirar());
        assertTrue(casillero.estaLibre());
    }

    @Test
    void noSePuedeDepositarEnCasilleroOcupado() {
        casillero.depositar("PKG-1");
        assertThrows(IllegalStateException.class, () -> casillero.depositar("PKG-2"));
    }

    @Test
    void noSePuedeRetirarDeCasilleroVacio() {
        assertThrows(IllegalStateException.class, () -> casillero.retirar());
    }

    @Test
    void buscarDevuelveseASiMismoSiCoincideTamanoYEstaLibre() {
        assertSame(casillero, casillero.buscarCasilleroLibre(Tamano.MEDIANO));
        assertNull(casillero.buscarCasilleroLibre(Tamano.GRANDE));
        casillero.depositar("PKG-1");
        assertNull(casillero.buscarCasilleroLibre(Tamano.MEDIANO));
    }

    @Test
    void unaHojaNoAdmiteHijos() {
        Casillero otro = new Casillero("A2", Tamano.PEQUENO);
        assertThrows(UnsupportedOperationException.class, () -> casillero.add(otro));
        assertThrows(UnsupportedOperationException.class, () -> casillero.remove(otro));
        assertThrows(UnsupportedOperationException.class, () -> casillero.getHijo(0));
        assertEquals(0, casillero.getNumeroHijos());
        assertFalse(casillero.esCompuesto());
    }

    @Test
    void idVacioNoPermitido() {
        assertThrows(IllegalArgumentException.class, () -> new Casillero(" ", Tamano.GRANDE));
    }
}
