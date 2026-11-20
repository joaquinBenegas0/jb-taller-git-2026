package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("francotirador")
public class Francotirador extends ArmaDeFuego {

    private float zoom;
    private float penetracion;
    private boolean zoomActivo;

    /** Solo para JPA. */
    protected Francotirador() {
    }

    /** Constructor simple: francotirador estándar (valores del AWP) para los antiterroristas. */
    public Francotirador(String nombre, int precio) {
        this(nombre, precio, Equipo.ANTITERRORISTAS);
    }

    /** Sobrecarga: se elige el equipo; el resto de los valores son los estándar. */
    public Francotirador(String nombre, int precio, Equipo equipo) {
        this(nombre, precio, equipo, 115, 0.95f, 5, 30, 3.6f, 4f, 0.5f);
    }

    /** Constructor completo: todos los valores; llama a super. Zoom y penetración los valida ValidadorDeArmas. */
    public Francotirador(String nombre, int precio, Equipo equipo, int danio, float precision,
                         int capacidadCargador, int municionReserva, float tiempoRecarga,
                         float zoom, float penetracion) {
        super(nombre, precio, equipo, danio, precision, capacidadCargador, municionReserva, tiempoRecarga);
        this.zoom = zoom;
        this.penetracion = penetracion;
        this.zoomActivo = false;
    }

    public void activarZoom() {
        zoomActivo = true;
    }

    public void desactivarZoom() {
        zoomActivo = false;
    }

    public boolean tieneZoomActivo() {
        return zoomActivo;
    }

    public float obtenerZoom() {
        return zoom;
    }

    public float obtenerPenetracion() {
        return penetracion;
    }

    /** La mira multiplica el alcance efectivo del arma. */
    @Override
    public float alcanceEfectivo() {
        return super.alcanceEfectivo() * zoom;
    }

    /** Con el zoom activo la bala aprovecha la penetración. Parte del daño base del padre. */
    @Override
    public int calcularDanioPorBala() {
        int base = super.calcularDanioPorBala();
        return zoomActivo ? Math.round(base * (1f + penetracion)) : base;
    }

    /** En CS2 el AWP paga poco por eliminación: $100. */
    @Override
    public int recompensaPorEliminacion() {
        return 100;
    }
}
