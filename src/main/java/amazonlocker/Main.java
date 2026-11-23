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
        ArbolLockerBuilder arbolBuilder = new ArbolLockerBuilder();
        new DirectorLocker(arbolBuilder).construirRed("Red León", "Campus de Vegazana", "Estación de tren");
        ElementoLocker leon = arbolBuilder.getResultado();

        PlanoLockerBuilder planoBuilder = new PlanoLockerBuilder();
        new DirectorLocker(planoBuilder).construirRed("Red León", "Campus de Vegazana", "Estación de tren");
        System.out.println(planoBuilder.getResultado());

        // ---- Observer: pantalla de la taquilla T1 y alerta de la red ----
        PantallaTaquilla pantallaT1 = new PantallaTaquilla((Taquilla) leon.getHijo(0));
        AlertaOcupacion alerta = new AlertaOcupacion(leon, 20);
        System.out.println("Pantalla: " + pantallaT1.mostrar());

        // ---- Composite: el cliente trata todo el árbol de forma uniforme ----
        CentroReparto centro = new CentroReparto();
        centro.asignarPaquete(leon, "PKG-001", Tamano.GRANDE);
        centro.asignarPaquete(leon, "PKG-002", Tamano.PEQUENO);
        centro.asignarPaquete(leon, "PKG-003", Tamano.GRANDE);
        centro.asignarPaquete(leon, "PKG-004", Tamano.GRANDE);
        System.out.println(leon.mostrar());

        // ---- Iterator: recorrer todos los casilleros sin conocer el árbol ----
        System.out.println("Casilleros ocupados:");
        Iterator<Casillero> it = leon.createCasilleroIterator();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            Casillero c = it.currentElement();
            if (!c.estaLibre()) {
                System.out.println("  " + c.getRuta() + " -> " + c.getPaquete());
            }
        }

        Casillero c = centro.localizarPaquete(leon, "PKG-004");
        System.out.println("\nPKG-004 está en: " + c.getRuta());
        System.out.printf("Ocupación de la red: %.1f%%%n", centro.porcentajeOcupacion(leon));

        IndicadorLuz luz = new IndicadorLuz(c);
        System.out.println("Luz de " + c.getId() + ": " + luz.getColor());
        c.retirar();   // el cliente recoge PKG-004
        System.out.println("Luz de " + c.getId() + " tras recogerlo: " + luz.getColor());

        System.out.println("\nPantalla: " + pantallaT1.mostrar());
        System.out.println("Avisos de logística:");
        alerta.getAvisos().forEach(m -> System.out.println("  " + m));
    }
}
