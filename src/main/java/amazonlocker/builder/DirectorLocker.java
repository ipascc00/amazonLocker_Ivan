package amazonlocker.builder;

import amazonlocker.composite.Tamano;

import static amazonlocker.composite.Tamano.GRANDE;
import static amazonlocker.composite.Tamano.MEDIANO;
import static amazonlocker.composite.Tamano.PEQUENO;

/**
 * Rol DIRECTOR del patrón Builder.
 *
 * Conoce los modelos de taquilla que instala Amazon y el orden en que se
 * montan sus partes, pero no sabe qué representación se está generando:
 * solo habla con la interfaz {@link LockerBuilder}.
 */
public class DirectorLocker {

    /** Modelo estándar: 3 columnas (4 pequeños | 3 medianos | 2 grandes). */
    static final Tamano[][] MODELO_ESTANDAR = {
            {PEQUENO, PEQUENO, PEQUENO, PEQUENO},
            {MEDIANO, MEDIANO, MEDIANO},
            {GRANDE, GRANDE}
    };

    /** Modelo compacto para tiendas pequeñas: 1 columna (2 pequeños, 1 mediano). */
    static final Tamano[][] MODELO_COMPACTO = {
            {PEQUENO, PEQUENO, MEDIANO}
    };

    private final LockerBuilder builder;

    public DirectorLocker(LockerBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("El builder no puede ser nulo");
        }
        this.builder = builder;
    }

    public void construirTaquillaEstandar(String id, String ubicacion) {
        construirTaquilla(id, ubicacion, MODELO_ESTANDAR);
    }

    public void construirTaquillaCompacta(String id, String ubicacion) {
        construirTaquilla(id, ubicacion, MODELO_COMPACTO);
    }

    /**
     * Construye una red con una taquilla estándar en cada ubicación.
     * Las taquillas se identifican como T1, T2, ...
     */
    public void construirRed(String idRed, String... ubicaciones) {
        builder.buildRed(idRed);
        for (int i = 0; i < ubicaciones.length; i++) {
            construirTaquillaEstandar("T" + (i + 1), ubicaciones[i]);
        }
    }

    /**
     * for all objects in structure { builder.buildPart() }
     * Ids: columnas T1-A, T1-B...; casilleros T1-A1, T1-A2...
     */
    private void construirTaquilla(String id, String ubicacion, Tamano[][] modelo) {
        builder.buildTaquilla(id, ubicacion);
        for (int col = 0; col < modelo.length; col++) {
            String idColumna = id + "-" + (char) ('A' + col);
            builder.buildColumna(idColumna);
            for (int fila = 0; fila < modelo[col].length; fila++) {
                builder.buildCasillero(idColumna + (fila + 1), modelo[col][fila]);
            }
        }
    }
}
