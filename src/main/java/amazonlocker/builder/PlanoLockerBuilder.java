package amazonlocker.builder;

import amazonlocker.composite.Tamano;

/**
 * Rol CONCRETE BUILDER del patrón Builder (segunda representación).
 *
 * Con el MISMO proceso de construcción (el del Director) genera un
 * {@link PlanoInstalacion} en lugar del árbol de objetos. No sobrescribe
 * buildRed(): la red es un concepto lógico que no se instala, así que usa
 * el método vacío heredado de {@link LockerBuilder}.
 */
public class PlanoLockerBuilder extends LockerBuilder {

    private final PlanoInstalacion plano = new PlanoInstalacion();

    @Override
    public void buildTaquilla(String id, String ubicacion) {
        plano.anadirTaquilla(id, ubicacion);
    }

    @Override
    public void buildColumna(String id) {
        plano.anadirColumna(id);
    }

    @Override
    public void buildCasillero(String id, Tamano tamano) {
        plano.anadirCasillero(id, tamano);
    }

    public PlanoInstalacion getResultado() {
        return plano;
    }
}
