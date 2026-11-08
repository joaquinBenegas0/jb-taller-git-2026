package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("escopeta")
public class Escopeta extends ArmaDeFuego {

    private int numeroPerdigones;
    private float distanciaEfectiva;

    /** Solo para JPA. */
    protected Escopeta() {
    }

    public Escopeta(String nombre, int precio, Equipo equipo, int danio, float precision,
                    int capacidadCargador, int municionReserva, float tiempoRecarga,
                    int numeroPerdigones, float distanciaEfectiva) {
        super(nombre, precio, equipo, danio, precision, capacidadCargador, municionReserva, tiempoRecarga);
        if (numeroPerdigones <= 0) {
            throw new IllegalArgumentException("Debe disparar al menos un perdigón");
        }
        if (distanciaEfectiva <= 0f) {
            throw new IllegalArgumentException("La distancia efectiva debe ser mayor a 0");
        }
        this.numeroPerdigones = numeroPerdigones;
        this.distanciaEfectiva = distanciaEfectiva;
    }

    public float obtenerDistanciaEfectiva() {
        return distanciaEfectiva;
    }

    /** La escopeta solo es efectiva a corta distancia. */
    @Override
    protected float alcanceEfectivo() {
        return distanciaEfectiva;
    }

    /** Cada cartucho reparte el daño base en varios perdigones. */
    @Override
    protected int calcularDanioPorBala() {
        return super.calcularDanioPorBala() * numeroPerdigones;
    }

    /** Las escopetas pagan más por eliminación: $900. */
    @Override
    public int recompensaPorEliminacion() {
        return 900;
    }
}
