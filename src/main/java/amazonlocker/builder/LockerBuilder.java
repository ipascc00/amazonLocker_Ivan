package amazonlocker.builder;

import amazonlocker.composite.Tamano;

/**
 * Rol BUILDER del patrón Builder.
 *
 * Especifica la interfaz abstracta para crear las partes de un Amazon
 * Locker: red, taquilla, columna y casillero. Cada parte se añade a la
 * última parte "contenedora" construida (la taquilla a la red, la columna a
 * la última taquilla y el casillero a la última columna).
 *
 * Los métodos están vacíos por defecto (ver "Detalles de implementación"):
 * cada ConcreteBuilder solo sobrescribe las partes que le interesan.
 *
 * No hay método getResultado() aquí porque cada builder produce un producto
 * de un tipo distinto, sin una clase abstracta común.
 */
public abstract class LockerBuilder {

    public void buildRed(String id) {
    }

    public void buildTaquilla(String id, String ubicacion) {
    }

    public void buildColumna(String id) {
    }

    public void buildCasillero(String id, Tamano tamano) {
    }
}
