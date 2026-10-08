package py.edu.uc.lp3.jb_taller_git_2026.cs2;

/**
 * Raíz de la jerarquía: todo lo que se puede comprar en la tienda.
 * Los datos de tienda son inmutables y se validan una sola vez, en el constructor.
 */
public abstract class Arma {

    private final String nombre;
    private final int precio;
    private final Equipo equipo;

    protected Arma(String nombre, int precio, Equipo equipo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El arma necesita un nombre");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (equipo == null) {
            throw new IllegalArgumentException("El arma debe pertenecer a un equipo");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.equipo = equipo;
    }

    /**
     * Mensaje común a todas las armas: "usate".
     * El padre no puede implementarlo porque cada tipo se usa distinto:
     * un arma de fuego dispara (y controla su munición), una granada se lanza (y controla su activación).
     */
    public abstract String usar();

    /** Cómo se muestra cualquier arma en la tienda: igual para todas. */
    public String mostrarEnTienda() {
        return nombre + " - $" + precio + " (" + equipo + ")";
    }

    public String obtenerNombre() {
        return nombre;
    }

    public int obtenerPrecio() {
        return precio;
    }

    public Equipo obtenerEquipo() {
        return equipo;
    }

    @Override
    public String toString() {
        return mostrarEnTienda();
    }
}
