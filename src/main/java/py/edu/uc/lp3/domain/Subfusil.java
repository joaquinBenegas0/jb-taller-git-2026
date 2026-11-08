package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("subfusil")
public class Subfusil extends ArmaDeFuego {

    private float controlRetroceso;

    /** Solo para JPA. */
    protected Subfusil() {
    }

    public Subfusil(String nombre, int precio, Equipo equipo, int danio, float precision,
                    int capacidadCargador, int municionReserva, float tiempoRecarga,
                    float controlRetroceso) {
        super(nombre, precio, equipo, danio, precision, capacidadCargador, municionReserva, tiempoRecarga);
        if (controlRetroceso < 0f || controlRetroceso > 1f) {
            throw new IllegalArgumentException("El control de retroceso va de 0 a 1");
        }
        this.controlRetroceso = controlRetroceso;
    }

    public float obtenerControlRetroceso() {
        return controlRetroceso;
    }

    /** Los subfusiles pagan más por eliminación: $600. */
    @Override
    public int recompensaPorEliminacion() {
        return 600;
    }
}
