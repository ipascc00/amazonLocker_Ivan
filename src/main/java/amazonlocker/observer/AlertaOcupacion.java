package amazonlocker.observer;

import amazonlocker.composite.ElementoLocker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rol CONCRETE OBSERVER.
 *
 * Avisa al equipo de logística cuando una zona (normalmente una red de
 * taquillas) supera un porcentaje de ocupación, para redirigir los envíos a
 * otra zona. Solo avisa cuando se cruza el umbral, no en cada cambio.
 */
public class AlertaOcupacion implements Observer {

    private final ElementoLocker subject;
    private final double umbral;
    private boolean activa;                                // observerState
    private final List<String> avisos = new ArrayList<>();

    public AlertaOcupacion(ElementoLocker subject, double umbralPorcentaje) {
        if (subject == null) {
            throw new IllegalArgumentException("La zona no puede ser nula");
        }
        if (umbralPorcentaje <= 0 || umbralPorcentaje > 100) {
            throw new IllegalArgumentException("El umbral debe estar entre 0 y 100");
        }
        this.subject = subject;
        this.umbral = umbralPorcentaje;
        subject.attach(this);
    }

    @Override
    public void update() {
        double ocupacion = getOcupacion();
        if (!activa && ocupacion >= umbral) {
            activa = true;
            avisos.add(String.format("ALERTA: %s al %.0f%% de ocupación", subject.getId(), ocupacion));
        } else if (activa && ocupacion < umbral) {
            activa = false;
            avisos.add(String.format("Fin de alerta: %s al %.0f%%", subject.getId(), ocupacion));
        } else {
            return;
        }
        System.out.println("   [Logística] " + avisos.get(avisos.size() - 1));
    }

    private double getOcupacion() {
        int total = subject.getTotalCasilleros();
        return total == 0 ? 0 : 100.0 * (total - subject.getCasillerosLibres()) / total;
    }

    public boolean isActiva() {
        return activa;
    }

    public List<String> getAvisos() {
        return Collections.unmodifiableList(avisos);
    }

    public void desconectar() {
        subject.detach(this);
    }
}
