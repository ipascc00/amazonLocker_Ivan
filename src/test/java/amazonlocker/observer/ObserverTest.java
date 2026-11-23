package amazonlocker.observer;

import amazonlocker.builder.ArbolLockerBuilder;
import amazonlocker.builder.DirectorLocker;
import amazonlocker.composite.Casillero;
import amazonlocker.composite.CentroReparto;
import amazonlocker.composite.Columna;
import amazonlocker.composite.ElementoLocker;
import amazonlocker.composite.RedLockers;
import amazonlocker.composite.Tamano;
import amazonlocker.composite.Taquilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObserverTest {

    private Casillero a1;
    private Casillero a2;
    private Columna columna;
    private Taquilla taquilla;
    private RedLockers red;

    /** Observador de prueba que cuenta cuántas veces se le notifica. */
    private static class Contador implements Observer {
        int veces;

        @Override
        public void update() {
            veces++;
        }
    }

    @BeforeEach
    void setUp() {
        a1 = new Casillero("A1", Tamano.PEQUENO);
        a2 = new Casillero("A2", Tamano.GRANDE);
        columna = new Columna("Columna A");
        columna.add(a1);
        columna.add(a2);
        taquilla = new Taquilla("Taquilla Campus", "Campus");
        taquilla.add(columna);
        red = new RedLockers("Red León");
        red.add(taquilla);
    }

    // ---------------- Subject ----------------

    @Test
    void sinObservadoresNoFalla() {
        a1.depositar("P1");
        assertEquals("P1", a1.retirar());
        assertEquals(0, a1.getNumeroObservadores());
    }

    @Test
    void attachNoDuplicaYDetachDejaDeNotificar() {
        Contador c = new Contador();
        a1.attach(c);
        a1.attach(c);
        assertEquals(1, a1.getNumeroObservadores());

        a1.depositar("P1");
        a1.detach(c);
        a1.retirar();
        assertEquals(1, c.veces);
        assertEquals(0, a1.getNumeroObservadores());
    }

    @Test
    void attachNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> a1.attach(null));
    }

    @Test
    void noSeNotificaSiLaOperacionFalla() {
        Contador c = new Contador();
        a1.attach(c);
        assertThrows(IllegalStateException.class, a1::retirar);
        assertThrows(IllegalArgumentException.class, () -> a1.depositar(" "));
        assertEquals(0, c.veces);
    }

    @Test
    void unObservadorPuedeDarseDeBajaDuranteLaNotificacion() {
        Contador otro = new Contador();
        Observer deUnSoloUso = new Observer() {
            @Override
            public void update() {
                a1.detach(this);
            }
        };
        a1.attach(deUnSoloUso);
        a1.attach(otro);
        a1.depositar("P1");
        assertEquals(1, otro.veces);   // no se salta al siguiente
        assertEquals(1, a1.getNumeroObservadores());
    }

    @Test
    void elCambioSePropagaHaciaArribaEnElComposite() {
        Contador enCasillero = new Contador();
        Contador enColumna = new Contador();
        Contador enTaquilla = new Contador();
        Contador enRed = new Contador();
        a1.attach(enCasillero);
        columna.attach(enColumna);
        taquilla.attach(enTaquilla);
        red.attach(enRed);

        a1.depositar("P1");

        assertEquals(1, enCasillero.veces);
        assertEquals(1, enColumna.veces);
        assertEquals(1, enTaquilla.veces);
        assertEquals(1, enRed.veces);
    }

    @Test
    void unCasilleroEliminadoYaNoAvisaASuAntiguoPadre() {
        Contador enColumna = new Contador();
        columna.attach(enColumna);
        columna.remove(a1);
        a1.depositar("P1");
        assertEquals(0, enColumna.veces);
    }

    // ---------------- ConcreteObservers ----------------

    @Test
    void indicadorLuzSigueElEstadoDelCasillero() {
        IndicadorLuz luz = new IndicadorLuz(a1);
        assertEquals(IndicadorLuz.Color.VERDE, luz.getColor());
        a1.depositar("P1");
        assertEquals(IndicadorLuz.Color.ROJO, luz.getColor());
        a1.retirar();
        assertEquals(IndicadorLuz.Color.VERDE, luz.getColor());
    }

    @Test
    void indicadorLuzDesconectadoNoCambia() {
        IndicadorLuz luz = new IndicadorLuz(a1);
        luz.desconectar();
        a1.depositar("P1");
        assertEquals(IndicadorLuz.Color.VERDE, luz.getColor());
    }

    @Test
    void pantallaSeActualizaAlCambiarCualquierCasilleroDeLaTaquilla() {
        PantallaTaquilla pantalla = new PantallaTaquilla(taquilla);
        assertEquals("Taquilla Campus (Campus): 2/2 libres", pantalla.mostrar());

        new CentroReparto().asignarPaquete(taquilla, "P1", Tamano.GRANDE);
        assertEquals(1, pantalla.getLibres());

        a1.depositar("P2");
        assertEquals("Taquilla Campus (Campus): COMPLETA", pantalla.mostrar());
        assertEquals(2, pantalla.getRefrescos());
    }

    @Test
    void variosObservadoresIndependientes() {
        IndicadorLuz luzA1 = new IndicadorLuz(a1);
        IndicadorLuz luzA2 = new IndicadorLuz(a2);
        PantallaTaquilla pantalla = new PantallaTaquilla(taquilla);

        a2.depositar("P1");

        assertEquals(IndicadorLuz.Color.VERDE, luzA1.getColor());
        assertEquals(IndicadorLuz.Color.ROJO, luzA2.getColor());
        assertEquals(1, pantalla.getLibres());
    }

    @Test
    void alertaSoloAvisaAlCruzarElUmbral() {
        AlertaOcupacion alerta = new AlertaOcupacion(red, 50);
        a1.depositar("P1");                    // 50 %
        assertTrue(alerta.isActiva());
        a2.depositar("P2");                    // 100 %: ya estaba activa
        a1.retirar();                          // 50 %
        a2.retirar();                          // 0 %
        assertFalse(alerta.isActiva());
        assertEquals(List.of("ALERTA: Red León al 50% de ocupación",
                "Fin de alerta: Red León al 0%"), alerta.getAvisos());
    }

    @Test
    void umbralNoValidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new AlertaOcupacion(red, 0));
        assertThrows(IllegalArgumentException.class, () -> new AlertaOcupacion(red, 120));
    }

    @Test
    void observadoresSobreUnaRedConstruidaConElBuilder() {
        ArbolLockerBuilder builder = new ArbolLockerBuilder();
        new DirectorLocker(builder).construirRed("Red León", "Campus", "Estación");
        ElementoLocker leon = builder.getResultado();
        Taquilla t1 = (Taquilla) leon.getHijo(0);
        PantallaTaquilla pantallaT1 = new PantallaTaquilla(t1);
        AlertaOcupacion alerta = new AlertaOcupacion(leon, 10);

        CentroReparto centro = new CentroReparto();
        centro.asignarPaquete(leon, "P1", Tamano.MEDIANO);
        centro.asignarPaquete(leon, "P2", Tamano.MEDIANO);   // 2/18 = 11 %

        assertEquals(7, pantallaT1.getLibres());
        assertTrue(alerta.isActiva());
    }
}
