package amazonlocker;

import amazonlocker.builder.ArbolLockerBuilder;
import amazonlocker.builder.DirectorLocker;
import amazonlocker.builder.PlanoLockerBuilder;
import amazonlocker.composite.Casillero;
import amazonlocker.composite.CentroReparto;
import amazonlocker.composite.ElementoLocker;
import amazonlocker.composite.Tamano;
import amazonlocker.composite.Taquilla;
import amazonlocker.iterator.Iterator;
import amazonlocker.observer.AlertaOcupacion;
import amazonlocker.observer.IndicadorLuz;
import amazonlocker.observer.PantallaTaquilla;

/**
 * Demostración de los patrones Builder + Composite + Iterator + Observer
 * aplicados a Amazon Locker.
 */
public class Main {

    public static void main(String[] args) {
        // ---- Builder: el mismo Director genera dos representaciones ----
        System.out.println("===== BUILDER =====");
        System.out.println("Director: construirRed(\"Red León\", \"Campus de Vegazana\", \"Estación de tren\")");

        System.out.println("\n--- 1) Con ArbolLockerBuilder -> árbol de objetos (Composite) ---");
        ArbolLockerBuilder arbolBuilder = new ArbolLockerBuilder();
        new DirectorLocker(arbolBuilder).construirRed("Red León", "Campus de Vegazana", "Estación de tren");
        ElementoLocker leon = arbolBuilder.getResultado();
        System.out.println(leon.mostrar());

        System.out.println("--- 2) Con PlanoLockerBuilder -> plano de instalación ---");
        PlanoLockerBuilder planoBuilder = new PlanoLockerBuilder();
        new DirectorLocker(planoBuilder).construirRed("Red León", "Campus de Vegazana", "Estación de tren");
        System.out.println(planoBuilder.getResultado());

        // ---- Composite: el cliente trata todo el árbol de forma uniforme ----
        System.out.println("===== COMPOSITE =====");
        CentroReparto centro = new CentroReparto();
        Taquilla t1 = (Taquilla) leon.getHijo(0);
        System.out.println("asignarPaquete(red, PKG-001, GRANDE)     -> " + centro.asignarPaquete(leon, "PKG-001", Tamano.GRANDE).getRuta());
        System.out.println("asignarPaquete(red, PKG-002, PEQUENO)    -> " + centro.asignarPaquete(leon, "PKG-002", Tamano.PEQUENO).getRuta());
        System.out.println("asignarPaquete(T1, PKG-003, GRANDE)      -> " + centro.asignarPaquete(t1, "PKG-003", Tamano.GRANDE).getRuta());
        System.out.println("asignarPaquete(red, PKG-004, GRANDE)     -> " + centro.asignarPaquete(leon, "PKG-004", Tamano.GRANDE).getRuta());
        System.out.println();
        System.out.println(leon.mostrar());
        System.out.printf("Ocupación: red %.1f%%, T1 %.1f%%%n%n",
                centro.porcentajeOcupacion(leon), centro.porcentajeOcupacion(t1));

        // ---- Iterator: recorrer todos los casilleros sin conocer el árbol ----
        System.out.println("===== ITERATOR =====");
        System.out.println("Casilleros ocupados (CasilleroIterator sobre la red):");
        Iterator<Casillero> it = leon.createCasilleroIterator();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            Casillero c = it.currentElement();
            if (!c.estaLibre()) {
                System.out.println("  " + c.getRuta() + " -> " + c.getPaquete());
            }
        }
        System.out.println("localizarPaquete(red, PKG-004) -> " + centro.localizarPaquete(leon, "PKG-004").getRuta());
        System.out.println();

        // ---- Observer: los observadores se actualizan solos ----
        System.out.println("===== OBSERVER =====");
        Casillero b1 = (Casillero) t1.getHijo(1).getHijo(0);   // T1-B1, libre
        System.out.println("1) Se suscriben los observadores (attach en su constructor):");
        IndicadorLuz luz = new IndicadorLuz(b1);
        PantallaTaquilla pantalla = new PantallaTaquilla(t1);
        AlertaOcupacion alerta = new AlertaOcupacion(leon, 30);
        System.out.println("   [Pantalla] " + pantalla.mostrar());
        System.out.printf("   [Logística] vigilando %s, alerta al 30%% (ahora %.0f%%)%n",
                leon.getId(), centro.porcentajeOcupacion(leon));

        System.out.println("\n2) El repartidor deposita PKG-005 en T1-B1:  b1.depositar(\"PKG-005\")");
        b1.depositar("PKG-005");

        System.out.println("\n3) El centro asigna PKG-006 a la red:  asignarPaquete(red, PKG-006, MEDIANO)");
        centro.asignarPaquete(leon, "PKG-006", Tamano.MEDIANO);

        System.out.println("\n4) El cliente recoge su paquete:  b1.retirar()");
        b1.retirar();

        System.out.println("\n5) Se desconecta la luz (detach) y se vuelve a depositar en T1-B1:");
        luz.desconectar();
        b1.depositar("PKG-007");
        System.out.println("   (la luz ya no se entera; la pantalla sí, porque observa la taquilla)");
    }
}
