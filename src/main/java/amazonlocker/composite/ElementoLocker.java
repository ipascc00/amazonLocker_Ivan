package amazonlocker.composite;

import amazonlocker.iterator.Iterator;
import amazonlocker.iterator.NullIterator;
import amazonlocker.observer.Subject;

/**
 * Rol COMPONENTE del patrón Composite.
 *
 * Declara la interfaz común a todos los elementos de un Amazon Locker
 * (casilleros, columnas, taquillas y redes de taquillas), de modo que el
 * cliente pueda tratarlos de forma uniforme.
 *
 * Siguiendo el diagrama de clase, las operaciones de manejo de los hijos
 * (add, remove, getChild) se declaran aquí para conseguir TRANSPARENCIA.
 * Por defecto lanzan una excepción: solo los compuestos las sobrescriben,
 * así una hoja nunca acepta hijos (recuperamos parte de la seguridad).
 *
 * Además hereda de {@link Subject} (patrón Observer): cualquier elemento del
 * locker (casillero, columna, taquilla o red) puede ser observado.
 */
public abstract class ElementoLocker extends Subject {

    private final String id;
    private ElementoLocker padre;

    protected ElementoLocker(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El identificador no puede estar vacío");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // ---------------------------------------------------------------
    // Operaciones comunes (operation() en el diagrama)
    // ---------------------------------------------------------------

    /** Número total de casilleros que contiene el elemento. */
    public abstract int getTotalCasilleros();

    /** Número de casilleros libres que contiene el elemento. */
    public abstract int getCasillerosLibres();

    /**
     * Busca un casillero libre del tamaño indicado dentro del elemento.
     *
     * @return el casillero encontrado o {@code null} si no hay ninguno libre
     */
    public abstract Casillero buscarCasilleroLibre(Tamano tamano);

    /** Representación en árbol del elemento, con la sangría indicada. */
    public abstract String mostrar(String sangria);

    public String mostrar() {
        return mostrar("");
    }

    // ---------------------------------------------------------------
    // Operaciones de manejo de hijos (add / remove / getChild)
    // ---------------------------------------------------------------

    public void add(ElementoLocker elemento) {
        throw new UnsupportedOperationException(id + " no admite elementos hijo");
    }

    public void remove(ElementoLocker elemento) {
        throw new UnsupportedOperationException(id + " no admite elementos hijo");
    }

    public ElementoLocker getHijo(int indice) {
        throw new UnsupportedOperationException(id + " no tiene elementos hijo");
    }

    public int getNumeroHijos() {
        return 0;
    }

    public boolean esCompuesto() {
        return false;
    }

    // ---------------------------------------------------------------
    // Creación de iteradores (patrón Iterator)
    // ---------------------------------------------------------------

    /**
     * Iterador sobre los hijos directos. Una hoja devuelve un
     * {@link NullIterator}, de modo que el cliente puede iterar sobre
     * cualquier elemento sin saber si es hoja o compuesto.
     */
    public Iterator<ElementoLocker> createIterator() {
        return new NullIterator<>();
    }

    /** Iterador en profundidad sobre todos los casilleros que contiene el elemento. */
    public Iterator<Casillero> createCasilleroIterator() {
        return new CasilleroIterator(this);
    }

    // ---------------------------------------------------------------
    // Referencia al padre (opcional en el patrón)
    // ---------------------------------------------------------------

    public ElementoLocker getPadre() {
        return padre;
    }

    /** Solo la gestionan los compuestos al añadir / eliminar hijos. */
    void setPadre(ElementoLocker padre) {
        this.padre = padre;
    }

    // ---------------------------------------------------------------
    // Observer: propagación del cambio hacia arriba en el árbol
    // ---------------------------------------------------------------

    /**
     * Cuando un casillero cambia, también cambia el estado (casilleros libres)
     * de su columna, su taquilla y su red. Por eso, tras avisar a sus propios
     * observadores, el elemento avisa a los de su padre.
     */
    @Override
    public void notifyObservers() {
        super.notifyObservers();
        if (padre != null) {
            padre.notifyObservers();
        }
    }

    /** Ruta completa del elemento, p. ej. "Red León > Taquilla Campus > Columna A > A1". */
    public String getRuta() {
        return padre == null ? id : padre.getRuta() + " > " + id;
    }
}
