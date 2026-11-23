package amazonlocker.builder;

import amazonlocker.composite.Tamano;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Rol PRODUCT (segunda representación).
 *
 * Plano de instalación que recibe el técnico que monta las taquillas:
 * cuántas taquillas, columnas y casilleros de cada tamaño hay que pedir,
 * y una línea por cada pieza a montar.
 */
public class PlanoInstalacion {

    private int numTaquillas;
    private int numColumnas;
    private final Map<Tamano, Integer> casilleros = new EnumMap<>(Tamano.class);
    private final List<String> lineas = new ArrayList<>();

    PlanoInstalacion() {
        for (Tamano t : Tamano.values()) {
            casilleros.put(t, 0);
        }
    }

    void anadirTaquilla(String id, String ubicacion) {
        numTaquillas++;
        lineas.add("TAQUILLA " + id + " en " + ubicacion);
    }

    void anadirColumna(String id) {
        numColumnas++;
        lineas.add("  columna " + id);
    }

    void anadirCasillero(String id, Tamano tamano) {
        casilleros.merge(tamano, 1, Integer::sum);
        lineas.add("    casillero " + id + " (" + tamano + ")");
    }

    public int getNumTaquillas() {
        return numTaquillas;
    }

    public int getNumColumnas() {
        return numColumnas;
    }

    public int getNumCasilleros(Tamano tamano) {
        return casilleros.get(tamano);
    }

    public int getTotalCasilleros() {
        int total = 0;
        for (int n : casilleros.values()) {
            total += n;
        }
        return total;
    }

    public List<String> getLineas() {
        return List.copyOf(lineas);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PLANO DE INSTALACIÓN\n");
        for (String linea : lineas) {
            sb.append(linea).append('\n');
        }
        sb.append("Material: ").append(numTaquillas).append(" taquillas, ")
          .append(numColumnas).append(" columnas, ").append(casilleros).append('\n');
        return sb.toString();
    }
}
