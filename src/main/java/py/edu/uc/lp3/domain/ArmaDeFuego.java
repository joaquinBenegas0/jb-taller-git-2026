package py.edu.uc.lp3.domain;

/**
 * Concentra las reglas de munición: ninguna hija ni clase externa puede
 * tocar los contadores directamente. Las hijas solo especializan
 * cuántas balas gasta un disparo y cuánto daño hace cada bala.
 */
public abstract class ArmaDeFuego extends Arma {

    private final int danio;
    private final float precision;
    private final int capacidadCargador;
    private final float tiempoRecarga;
    private int municionCargador;
    private int municionReserva;

    protected ArmaDeFuego(String nombre, int precio, Equipo equipo,
                          int danio, float precision, int capacidadCargador,
                          int municionReserva, float tiempoRecarga) {
        super(nombre, precio, equipo);
        if (danio <= 0) {
            throw new IllegalArgumentException("El daño debe ser mayor a 0");
        }
        if (precision < 0f || precision > 1f) {
            throw new IllegalArgumentException("La precisión va de 0 a 1");
        }
        if (capacidadCargador <= 0) {
            throw new IllegalArgumentException("El cargador debe tener capacidad");
        }
        if (municionReserva < 0) {
            throw new IllegalArgumentException("La reserva no puede ser negativa");
        }
        if (tiempoRecarga <= 0f) {
            throw new IllegalArgumentException("El tiempo de recarga debe ser mayor a 0");
        }
        this.danio = danio;
        this.precision = precision;
        this.capacidadCargador = capacidadCargador;
        this.tiempoRecarga = tiempoRecarga;
        this.municionCargador = capacidadCargador; // sale de la tienda con el cargador lleno
        this.municionReserva = municionReserva;
    }

    /** Usar un arma de fuego es disparar; final para que ninguna hija cambie esa regla. */
    @Override
    public final String usar() {
        return disparar();
    }

    /** final: ninguna hija puede saltarse el control de munición. */
    public final String disparar() {
        return efectuarDisparo(calcularDanioPorBala(), "");
    }

    /**
     * Sobrecarga: la misma acción indicando la distancia al objetivo, en metros.
     * Más allá del alcance efectivo el daño cae en proporción a la distancia.
     */
    public final String disparar(float distanciaMetros) {
        if (distanciaMetros < 0f) {
            throw new IllegalArgumentException("La distancia no puede ser negativa");
        }
        float alcance = alcanceEfectivo();
        int danioPorBala = calcularDanioPorBala();
        String detalle = " a " + distanciaMetros + " m";
        if (distanciaMetros > alcance) {
            danioPorBala = Math.round(danioPorBala * alcance / distanciaMetros);
            detalle += " (fuera del alcance efectivo de " + alcance + " m)";
        }
        return efectuarDisparo(danioPorBala, detalle);
    }

    /** Único lugar donde se descuenta munición: lo usan las dos versiones de disparar. */
    private String efectuarDisparo(int danioPorBala, String detalle) {
        if (municionCargador == 0) {
            return obtenerNombre() + ": click... cargador vacío, hay que recargar";
        }
        int balas = Math.min(balasPorDisparo(), municionCargador);
        municionCargador -= balas;
        int danioTotal = danioPorBala * balas;
        return obtenerNombre() + " dispara " + balas + " bala(s)" + detalle + ": " + danioTotal
                + " de daño [" + municionCargador + "/" + municionReserva + "]";
    }

    /** final: la recarga mueve balas de la reserva al cargador sin pasarse de la capacidad. */
    public final String recargar() {
        int faltan = capacidadCargador - municionCargador;
        if (faltan == 0) {
            return obtenerNombre() + ": el cargador ya está lleno";
        }
        if (municionReserva == 0) {
            return obtenerNombre() + ": no queda munición de reserva";
        }
        int cargadas = Math.min(faltan, municionReserva);
        municionReserva -= cargadas;
        municionCargador += cargadas;
        return obtenerNombre() + " recarga " + cargadas + " bala(s) en " + tiempoRecarga
                + " s [" + municionCargador + "/" + municionReserva + "]";
    }

    /** Por defecto un disparo gasta una bala. Pistola y rifle lo redefinen. */
    protected int balasPorDisparo() {
        return 1;
    }

    /** Alcance efectivo en metros. Escopeta y francotirador lo redefinen. */
    protected float alcanceEfectivo() {
        return 50f;
    }

    /** Daño base por bala. Francotirador y escopeta lo redefinen partiendo de este valor. */
    protected int calcularDanioPorBala() {
        return danio;
    }

    public float obtenerPrecision() {
        return precision;
    }

    public int obtenerMunicionCargador() {
        return municionCargador;
    }

    public int obtenerMunicionReserva() {
        return municionReserva;
    }
}
