package amazonlocker.composite;

import amazonlocker.iterator.Iterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Rol CLIENTE del patrón Composite (y del patrón Iterator).
 *
 * El centro de reparto asigna paquetes a casilleros y localiza los paquetes
 * que hay que recoger. Trabaja siempre con {@link ElementoLocker}, sin saber
 * si recibe una red entera, una taquilla, una columna o un único casillero.
 */
public class CentroReparto {

    /**
     * Asigna un paquete a un casillero libre del tamaño indicado dentro de
     * {@code destino}.
     *
     * @return el casillero asignado o {@code null} si no hay hueco
     */
    public Casillero asignarPaquete(ElementoLocker destino, String codigoPaquete, Tamano tamano) {
        Casillero casillero = destino.buscarCasilleroLibre(tamano);
        if (casillero != null) {
            casillero.depositar(codigoPaquete);
        }
        return casillero;
    }

    /** Porcentaje de ocupación de cualquier elemento del locker. */
    public double porcentajeOcupacion(ElementoLocker elemento) {
        int total = elemento.getTotalCasilleros();
        if (total == 0) {
            return 0.0;
        }
        return 100.0 * (total - elemento.getCasillerosLibres()) / total;
    }

    /**
     * Localiza el casillero que contiene un paquete (lo que hace la pantalla
     * del locker cuando el cliente introduce su código de recogida).
     *
     * @return el casillero o {@code null} si el paquete no está en {@code zona}
     */
    public Casillero localizarPaquete(ElementoLocker zona, String codigoPaquete) {
        Iterator<Casillero> it = zona.createCasilleroIterator();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            Casillero c = it.currentElement();
            if (codigoPaquete.equals(c.getPaquete())) {
                return c;
            }
        }
        return null;
    }

    /** Lista de casilleros ocupados (ruta de recogida de paquetes caducados). */
    public List<Casillero> casillerosOcupados(ElementoLocker zona) {
        List<Casillero> ocupados = new ArrayList<>();
        Iterator<Casillero> it = zona.createCasilleroIterator();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            if (!it.currentElement().estaLibre()) {
                ocupados.add(it.currentElement());
            }
        }
        return ocupados;
    }
}
