package amazonlocker.composite;

/**
 * Rol HOJA del patrón Composite y CONCRETE SUBJECT del patrón Observer.
 *
 * Un casillero es la unidad mínima de un Amazon Locker: una puerta con
 * cerradura donde se deposita un paquete. No tiene hijos.
 *
 * Su estado (libre / ocupado con un paquete) interesa a otros objetos
 * (la luz de la puerta, la pantalla de la taquilla...), así que llama a
 * notifyObservers() cada vez que se deposita o se retira un paquete.
 */
public class Casillero extends ElementoLocker {

    private final Tamano tamano;
    private String paquete;   // estado del sujeto: código del paquete (null si está libre)

    public Casillero(String id, Tamano tamano) {
        super(id);
        if (tamano == null) {
            throw new IllegalArgumentException("El tamaño no puede ser nulo");
        }
        this.tamano = tamano;
    }

    public Tamano getTamano() {
        return tamano;
    }

    public boolean estaLibre() {
        return paquete == null;
    }

    public String getPaquete() {
        return paquete;
    }

    /** El repartidor deposita un paquete en el casillero. */
    public void depositar(String codigoPaquete) {
        if (!estaLibre()) {
            throw new IllegalStateException("El casillero " + getId() + " ya está ocupado");
        }
        if (codigoPaquete == null || codigoPaquete.isBlank()) {
            throw new IllegalArgumentException("Código de paquete no válido");
        }
        this.paquete = codigoPaquete;
        notifyObservers();   // se avisa cuando el estado ya es coherente
    }

    /** El cliente recoge su paquete y el casillero queda libre. */
    public String retirar() {
        if (estaLibre()) {
            throw new IllegalStateException("El casillero " + getId() + " está vacío");
        }
        String recogido = paquete;
        paquete = null;
        notifyObservers();
        return recogido;
    }

    // ---------------- operation() ----------------

    @Override
    public int getTotalCasilleros() {
        return 1;
    }

    @Override
    public int getCasillerosLibres() {
        return estaLibre() ? 1 : 0;
    }

    @Override
    public Casillero buscarCasilleroLibre(Tamano tamano) {
        return estaLibre() && this.tamano == tamano ? this : null;
    }

    @Override
    public String mostrar(String sangria) {
        return sangria + "[" + getId() + " " + tamano + " "
                + (estaLibre() ? "libre" : "ocupado: " + paquete) + "]\n";
    }
}
