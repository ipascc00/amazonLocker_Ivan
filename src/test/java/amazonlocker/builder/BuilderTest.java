package amazonlocker.builder;

import amazonlocker.composite.Casillero;
import amazonlocker.composite.Columna;
import amazonlocker.composite.ElementoLocker;
import amazonlocker.composite.RedLockers;
import amazonlocker.composite.Tamano;
import amazonlocker.composite.Taquilla;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuilderTest {

    // ---------------- Director + ArbolLockerBuilder ----------------

    @Test
    void construyeUnaTaquillaEstandar() {
        ArbolLockerBuilder builder = new ArbolLockerBuilder();
        new DirectorLocker(builder).construirTaquillaEstandar("T1", "Campus");

        ElementoLocker resultado = builder.getResultado();
        assertInstanceOf(Taquilla.class, resultado);
        assertEquals("Campus", ((Taquilla) resultado).getUbicacion());
        assertEquals(3, resultado.getNumeroHijos());
        assertEquals(9, resultado.getTotalCasilleros());
        assertEquals(9, resultado.getCasillerosLibres());
    }

    @Test
    void losIdentificadoresSiguenElModelo() {
        ArbolLockerBuilder builder = new ArbolLockerBuilder();
        new DirectorLocker(builder).construirTaquillaEstandar("T1", "Campus");

        ElementoLocker columnaC = builder.getResultado().getHijo(2);
        assertEquals("T1-C", columnaC.getId());
        Casillero c2 = (Casillero) columnaC.getHijo(1);
        assertEquals("T1-C2", c2.getId());
        assertEquals(Tamano.GRANDE, c2.getTamano());
        assertEquals("T1 > T1-C > T1-C2", c2.getRuta());
    }

    @Test
    void construyeUnaTaquillaCompacta() {
        ArbolLockerBuilder builder = new ArbolLockerBuilder();
        new DirectorLocker(builder).construirTaquillaCompacta("T9", "Tienda");
        assertEquals(1, builder.getResultado().getNumeroHijos());
        assertEquals(3, builder.getResultado().getTotalCasilleros());
        assertNull(builder.getResultado().buscarCasilleroLibre(Tamano.GRANDE));
    }

    @Test
    void construyeUnaRedCompleta() {
        ArbolLockerBuilder builder = new ArbolLockerBuilder();
        new DirectorLocker(builder).construirRed("León", "Campus", "Estación", "Centro");

        ElementoLocker red = builder.getResultado();
        assertInstanceOf(RedLockers.class, red);
        assertEquals(3, red.getNumeroHijos());
        assertEquals(27, red.getTotalCasilleros());
        assertEquals("T3", red.getHijo(2).getId());
        assertSame(red, red.getHijo(0).getPadre());
    }

    @Test
    void elArbolConstruidoEsUnComposite() {
        ArbolLockerBuilder builder = new ArbolLockerBuilder();
        new DirectorLocker(builder).construirRed("León", "Campus");
        ElementoLocker red = builder.getResultado();

        Casillero c = red.buscarCasilleroLibre(Tamano.MEDIANO);
        assertEquals("T1-B1", c.getId());
        c.depositar("PKG");
        assertEquals(8, red.getCasillerosLibres());
    }

    // ---------------- ArbolLockerBuilder sin Director ----------------

    @Test
    void elBuilderCompruebaElOrdenDeLasPartes() {
        ArbolLockerBuilder builder = new ArbolLockerBuilder();
        assertThrows(IllegalStateException.class, builder::getResultado);
        assertThrows(IllegalStateException.class, () -> builder.buildColumna("A"));
        assertThrows(IllegalStateException.class, () -> builder.buildCasillero("A1", Tamano.PEQUENO));

        builder.buildTaquilla("T1", "Campus");
        assertThrows(IllegalStateException.class, () -> builder.buildCasillero("A1", Tamano.PEQUENO));
        assertThrows(IllegalStateException.class, () -> builder.buildRed("León"));
        assertThrows(IllegalStateException.class, () -> builder.buildTaquilla("T2", "Otra"));
    }

    @Test
    void cadaCasilleroVaALaUltimaColumna() {
        ArbolLockerBuilder builder = new ArbolLockerBuilder();
        builder.buildTaquilla("T1", "Campus");
        builder.buildColumna("A");
        builder.buildCasillero("A1", Tamano.PEQUENO);
        builder.buildColumna("B");
        builder.buildCasillero("B1", Tamano.GRANDE);
        builder.buildCasillero("B2", Tamano.GRANDE);

        ElementoLocker t = builder.getResultado();
        assertEquals(1, t.getHijo(0).getNumeroHijos());
        assertEquals(2, t.getHijo(1).getNumeroHijos());
        assertInstanceOf(Columna.class, t.getHijo(1));
    }

    // ---------------- PlanoLockerBuilder: otra representación ----------------

    @Test
    void elMismoProcesoGeneraUnPlanoDeInstalacion() {
        PlanoLockerBuilder builder = new PlanoLockerBuilder();
        new DirectorLocker(builder).construirRed("León", "Campus", "Estación");

        PlanoInstalacion plano = builder.getResultado();
        assertEquals(2, plano.getNumTaquillas());
        assertEquals(6, plano.getNumColumnas());
        assertEquals(8, plano.getNumCasilleros(Tamano.PEQUENO));
        assertEquals(6, plano.getNumCasilleros(Tamano.MEDIANO));
        assertEquals(4, plano.getNumCasilleros(Tamano.GRANDE));
        assertEquals(18, plano.getTotalCasilleros());
        assertEquals("TAQUILLA T1 en Campus", plano.getLineas().get(0));
        assertEquals("    casillero T1-A1 (PEQUENO)", plano.getLineas().get(2));
    }

    @Test
    void ambasRepresentacionesCoinciden() {
        ArbolLockerBuilder arbol = new ArbolLockerBuilder();
        PlanoLockerBuilder plano = new PlanoLockerBuilder();
        new DirectorLocker(arbol).construirTaquillaCompacta("T1", "Tienda");
        new DirectorLocker(plano).construirTaquillaCompacta("T1", "Tienda");
        assertEquals(arbol.getResultado().getTotalCasilleros(), plano.getResultado().getTotalCasilleros());
    }

    @Test
    void directorSinBuilderNoPermitido() {
        assertThrows(IllegalArgumentException.class, () -> new DirectorLocker(null));
    }
}
