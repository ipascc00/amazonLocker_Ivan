package amazonlocker.composite;

/**
 * Compuesto concreto: una taquilla Amazon Locker instalada en una ubicación
 * (supermercado, gasolinera, estación...). Está formada por columnas.
 */
public class Taquilla extends GrupoLocker {

    private final String ubicacion;

    public Taquilla(String id, String ubicacion) {
        super(id);
        this.ubicacion = ubicacion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    @Override
    protected boolean admite(ElementoLocker elemento) {
        return elemento instanceof Columna;
    }
}
