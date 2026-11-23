package amazonlocker.observer;

import amazonlocker.composite.Taquilla;

/**
 * Rol CONCRETE OBSERVER.
 *
 * Pantalla táctil de una taquilla que muestra cuántos casilleros quedan
 * libres. Observa a la taquilla; la taquilla avisa cada vez que cambia uno
 * de sus casilleros (el cambio se propaga hacia arriba en el Composite).
 */
public class PantallaTaquilla implements Observer {

    private final Taquilla subject;
    private int libres;      // observerState
    private int total;
    private int refrescos;

    public PantallaTaquilla(Taquilla subject) {
        if (subject == null) {
            throw new IllegalArgumentException("La taquilla no puede ser nula");
        }
        this.subject = subject;
        subject.attach(this);
        refrescar();
    }

    @Override
    public void update() {
        refrescar();
        refrescos++;
        System.out.println("   [Pantalla] " + mostrar());
    }

    private void refrescar() {
        libres = subject.getCasillerosLibres();
        total = subject.getTotalCasilleros();
    }

    public int getLibres() {
        return libres;
    }

    public int getRefrescos() {
        return refrescos;
    }

    public String mostrar() {
        return subject.getId() + " (" + subject.getUbicacion() + "): "
                + (libres == 0 ? "COMPLETA" : libres + "/" + total + " libres");
    }

    public void desconectar() {
        subject.detach(this);
    }
}
