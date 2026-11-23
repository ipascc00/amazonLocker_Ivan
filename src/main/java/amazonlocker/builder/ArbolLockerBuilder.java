package amazonlocker.builder;

import amazonlocker.composite.Casillero;
import amazonlocker.composite.Columna;
import amazonlocker.composite.ElementoLocker;
import amazonlocker.composite.RedLockers;
import amazonlocker.composite.Tamano;
import amazonlocker.composite.Taquilla;

/**
 * Rol CONCRETE BUILDER del patrón Builder.
 *
 * Construye el árbol Composite del Amazon Locker
 * (RedLockers → Taquilla → Columna → Casillero) y guarda la referencia
 * al producto que está creando.
 */
public class ArbolLockerBuilder extends LockerBuilder {

    private ElementoLocker raiz;
    private RedLockers red;
    private Taquilla taquillaActual;
    private Columna columnaActual;

    @Override
    public void buildRed(String id) {
        if (raiz != null) {
            throw new IllegalStateException("La red debe ser la primera parte que se construye");
        }
        red = new RedLockers(id);
        raiz = red;
    }

    @Override
    public void buildTaquilla(String id, String ubicacion) {
        taquillaActual = new Taquilla(id, ubicacion);
        columnaActual = null;
        if (red != null) {
            red.add(taquillaActual);
        } else if (raiz == null) {
            raiz = taquillaActual;            // se construye una taquilla suelta
        } else {
            throw new IllegalStateException("Sin una red solo se puede construir una taquilla");
        }
    }

    @Override
    public void buildColumna(String id) {
        if (taquillaActual == null) {
            throw new IllegalStateException("Hay que construir una taquilla antes que la columna " + id);
        }
        columnaActual = new Columna(id);
        taquillaActual.add(columnaActual);
    }

    @Override
    public void buildCasillero(String id, Tamano tamano) {
        if (columnaActual == null) {
            throw new IllegalStateException("Hay que construir una columna antes que el casillero " + id);
        }
        columnaActual.add(new Casillero(id, tamano));
    }

    /** Devuelve el producto construido: la raíz del árbol. */
    public ElementoLocker getResultado() {
        if (raiz == null) {
            throw new IllegalStateException("Todavía no se ha construido nada");
        }
        return raiz;
    }
}
