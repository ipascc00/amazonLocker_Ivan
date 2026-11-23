package amazonlocker.composite;

/**
 * Compuesto concreto: red de taquillas de una zona (ciudad, región...).
 * Puede contener taquillas u otras redes (p. ej. País > Región > Ciudad).
 */
public class RedLockers extends GrupoLocker {

    public RedLockers(String id) {
        super(id);
    }

    @Override
    protected boolean admite(ElementoLocker elemento) {
        return elemento instanceof Taquilla || elemento instanceof RedLockers;
    }
}
