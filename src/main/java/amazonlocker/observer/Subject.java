package amazonlocker.observer;

import amazonlocker.iterator.Iterator;
import amazonlocker.iterator.VectorAggregate;

/**
 * Rol SUBJECT del patrón Observer.
 *
 * Conoce a sus observadores (de 0 a N) y ofrece la posibilidad de añadirlos
 * y eliminarlos. Los guarda en un {@link VectorAggregate} y los recorre con
 * el Iterator del proyecto.
 */
public abstract class Subject {

    private final VectorAggregate<Observer> observers;

    protected Subject() {
        this.observers = new VectorAggregate<>();
    }

    public void attach(Observer o) {
        if (o == null) {
            throw new IllegalArgumentException("El observador no puede ser nulo");
        }
        if (!observers.contains(o)) {
            observers.addElement(o);
        }
    }

    public void detach(Observer o) {
        observers.remove(o);
    }

    /**
     * for all o in observers: o.update()
     *
     * Se recorre una copia para que un observador pueda darse de baja
     * (detach) dentro de su propio update() sin que se salte al siguiente.
     */
    public void notifyObservers() {
        VectorAggregate<Observer> copia = new VectorAggregate<>();
        copia.addAll(observers);
        Iterator<Observer> it = copia.createIterator();
        for (it.firstElement(); it.hasMoreElements(); it.nextElement()) {
            it.currentElement().update();
        }
    }

    public int getNumeroObservadores() {
        return observers.size();
    }
}
