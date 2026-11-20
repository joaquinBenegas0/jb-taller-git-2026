package py.edu.uc.lp3.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Arma con cargador. Guarda daño, precisión y munición; las hijas solo especializan
 * cuántas balas gasta un disparo, el alcance y el daño por bala.
 * Disparar y recargar (las reglas que cambian la munición) los hace ArmaService.
 */
@Entity
public abstract class ArmaDeFuego extends Arma {

    /** Más allá de este alcance (en metros) el daño por bala cae en proporción a la distancia. */
    protected static final float ALCANCE_ESTANDAR = 50f;

    private int danio;
    @Column(name = "precision_disparo")
    private float precision;
    private int capacidadCargador;
    private float tiempoRecarga;
    private int municionCargador;
    private int municionReserva;

    /** Solo para JPA. */
    protected ArmaDeFuego() {
    }

    protected ArmaDeFuego(String nombre, int precio, Equipo equipo,
                          int danio, float precision, int capacidadCargador,
                          int municionReserva, float tiempoRecarga) {
        super(nombre, precio, equipo);
        this.danio = danio;
        this.precision = precision;
        this.capacidadCargador = capacidadCargador;
        this.tiempoRecarga = tiempoRecarga;
        this.municionCargador = capacidadCargador; // sale de la tienda con el cargador lleno
        this.municionReserva = municionReserva;
    }

    /** Un arma de fuego se usa disparándola; final para que ninguna hija cambie esa forma de uso. */
    @Override
    public final String usar() {
        return obtenerNombre() + " se dispara: " + balasPorDisparo() + " bala(s) por disparo de "
                + calcularDanioPorBala() + " de daño, alcance efectivo " + alcanceEfectivo() + " m";
    }

    /** Lo llama ArmaService al disparar o recargar, después de aplicar la regla de munición. */
    public void actualizarMunicion(int cargador, int reserva) {
        this.municionCargador = cargador;
        this.municionReserva = reserva;
    }

    /** Por defecto un disparo gasta una bala. Pistola y rifle lo redefinen. */
    public int balasPorDisparo() {
        return 1;
    }

    /** Alcance efectivo en metros. Escopeta y francotirador lo redefinen. */
    public float alcanceEfectivo() {
        return ALCANCE_ESTANDAR;
    }

    /** Daño base por bala. Francotirador y escopeta lo redefinen partiendo de este valor. */
    public int calcularDanioPorBala() {
        return danio;
    }

    /** El reporte de un arma de fuego suma el estado de su munición. */
    @Override
    public String reporteDeCombate() {
        return super.reporteDeCombate() + " | munición " + municionCargador + "/" + municionReserva;
    }

    public int obtenerDanio() {
        return danio;
    }

    public int obtenerCapacidadCargador() {
        return capacidadCargador;
    }

    public float obtenerPrecision() {
        return precision;
    }

    public float obtenerTiempoRecarga() {
        return tiempoRecarga;
    }

    public int obtenerMunicionCargador() {
        return municionCargador;
    }

    public int obtenerMunicionReserva() {
        return municionReserva;
    }
}
