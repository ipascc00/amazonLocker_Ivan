package amazonlocker.composite;

/**
 * Compuesto concreto: columna vertical de un locker.
 * Solo puede contener casilleros.
 */
public class Columna extends GrupoLocker {

    public Columna(String id) {
        super(id);
    }

    @Override
    protected boolean admite(ElementoLocker elemento) {
        return elemento instanceof Casillero;
    }
}
