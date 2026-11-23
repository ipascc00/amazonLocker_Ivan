package amazonlocker.composite;

import amazonlocker.iterator.Iterator;
import amazonlocker.iterator.VectorAggregate;

/**
 * Rol COMPUESTO del patrón Composite.
 *
 * Agrupa elementos del locker (casilleros u otros grupos), almacena a los
 * hijos, implementa las operaciones de manejo de hijos y define las
 * operaciones comunes delegando recursivamente en ellos:
 *
 *     forall e in hijos: e.operacion()
 *
 * Los hijos se guardan en un {@link VectorAggregate} (patrón Iterator) y se
 * recorren siempre a través de un {@link Iterator}, sin depender de la
 * estructura interna de la colección.
 */
public abstract class GrupoLocker extends ElementoLocker {

    private final VectorAggregate<ElementoLocker> hijos = new VectorAggregate<>();

    protected GrupoLocker(String id) {
        super(id);
    }

    // ---------------- manejo de hijos ----------------

    @Override
    public void add(ElementoLocker elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se puede añadir un elemento nulo");
        }
        if (contieneAncestro(elemento)) {
            throw new IllegalArgumentException("No se puede añadir un elemento dentro de sí mismo");
        }
        if (elemento.getPadre() != null) {
            throw new IllegalStateException(elemento.getId() + " ya pertenece a " + elemento.getPadre().getId());
        }
        if (!admite(elemento)) {
            throw new IllegalArgumentException(getClass().getSimpleName() + " no admite "
                    + elemento.getClass().getSimpleName());
        }
        hijos.addElement(elemento);
        elemento.setPadre(this);
    }

    @Override
    public void remove(ElementoLocker elemento) {
        int indice = hijos.indexOf(elemento);
        if (indice >= 0) {
            hijos.removeElement(indice);
            elemento.setPadre(null);
        }
    }

    @Override
    public ElementoLocker getHijo(int indice) {
        return hijos.getElement(indice);
    }

    @Override
    public int getNumeroHijos() {
        return hijos.getSize();
    }

    @Override
    public boolean esCompuesto() {
        return true;
    }

    @Override
    public Iterator<ElementoLocker> createIterator() {
        return hijos.createIterator();
    }

    /** Recorrido de los hijos en orden inverso (p. ej. columna de abajo a arriba). */
    public Iterator<ElementoLocker> createIterator(String tipo) {
        return hijos.createIterator(tipo);
    }

    /**
     * Comprobación dinámica del tipo de hijos permitido (ver "Consecuencias"
     * del patrón Composite). Cada compuesto concreto restringe lo que puede contener.
     */
    protected abstract boolean admite(ElementoLocker elemento);

    /** Evita ciclos: el elemento no puede ser este grupo ni uno de sus ancestros. */
    private boolean contieneAncestro(ElementoLocker elemento) {
        for (ElementoLocker e = this; e != null; e = e.getPadre()) {
            if (e == elemento) {
                return true;
            }
        }
        return false;
    }

    // ---------------- operaciones comunes (recursivas) ----------------

    @Override
    public int getTotalCasilleros() {
        int total = 0;
        Iterator<ElementoLocker> it = createIterator();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            total += it.currentElement().getTotalCasilleros();
        }
        return total;
    }

    @Override
    public int getCasillerosLibres() {
        int libres = 0;
        Iterator<ElementoLocker> it = createIterator();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            libres += it.currentElement().getCasillerosLibres();
        }
        return libres;
    }

    @Override
    public Casillero buscarCasilleroLibre(Tamano tamano) {
        Iterator<ElementoLocker> it = createIterator();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            Casillero encontrado = it.currentElement().buscarCasilleroLibre(tamano);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }

    @Override
    public String mostrar(String sangria) {
        StringBuilder sb = new StringBuilder();
        sb.append(sangria).append(getClass().getSimpleName()).append(" ").append(getId())
          .append(" (").append(getCasillerosLibres()).append("/")
          .append(getTotalCasilleros()).append(" libres)\n");
        Iterator<ElementoLocker> it = createIterator();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            sb.append(it.currentElement().mostrar(sangria + "  "));
        }
        return sb.toString();
    }
}
