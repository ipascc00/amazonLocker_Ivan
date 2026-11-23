package amazonlocker.observer;

import amazonlocker.composite.Casillero;

/**
 * Rol CONCRETE OBSERVER.
 *
 * Luz LED de la puerta de un casillero: verde si está libre y roja si tiene
 * un paquete dentro. Guarda una referencia a su sujeto y se suscribe en el
 * constructor.
 */
public class IndicadorLuz implements Observer {

    public enum Color { VERDE, ROJO }

    private final Casillero subject;
    private Color color;   // observerState

    public IndicadorLuz(Casillero subject) {
        if (subject == null) {
            throw new IllegalArgumentException("El casillero no puede ser nulo");
        }
        this.subject = subject;
        subject.attach(this);
        update();   // estado inicial coherente con el del sujeto
    }

    @Override
    public void update() {
        color = subject.estaLibre() ? Color.VERDE : Color.ROJO;
    }

    public Color getColor() {
        return color;
    }

    /** Deja de observar el casillero (p. ej. si se avería la luz). */
    public void desconectar() {
        subject.detach(this);
    }
}
