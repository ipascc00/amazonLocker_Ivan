package amazonlocker.observer;

/**
 * Rol OBSERVER del patrón Observer.
 *
 * Define la interfaz con la que se notifica a los observadores que el
 * Subject ha cambiado. Modelo <i>pull</i>: solo se avisa del cambio y cada
 * observador consulta a su sujeto lo que le interesa.
 */
public interface Observer {

    void update();
}
