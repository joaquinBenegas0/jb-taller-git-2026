package py.edu.uc.lp3.domain;

public class Francotirador extends ArmaDeFuego {

    private final float zoom;
    private final float penetracion;
    private boolean zoomActivo;

    /** Constructor simple: francotirador estándar (valores del AWP) para los antiterroristas. */
    public Francotirador(String nombre, int precio) {
        this(nombre, precio, Equipo.ANTITERRORISTAS);
    }

    /** Sobrecarga: se elige el equipo; el resto de los valores son los estándar. */
    public Francotirador(String nombre, int precio, Equipo equipo) {
        this(nombre, precio, equipo, 115, 0.95f, 5, 30, 3.6f, 4f, 0.5f);
    }

    /** Constructor completo: todos los valores; llama a super y valida zoom y penetración. */
    public Francotirador(String nombre, int precio, Equipo equipo, int danio, float precision,
                         int capacidadCargador, int municionReserva, float tiempoRecarga,
                         float zoom, float penetracion) {
        super(nombre, precio, equipo, danio, precision, capacidadCargador, municionReserva, tiempoRecarga);
        if (zoom < 1f) {
            throw new IllegalArgumentException("El zoom debe ser al menos 1x");
        }
        if (penetracion < 0f || penetracion > 1f) {
            throw new IllegalArgumentException("La penetración va de 0 a 1");
        }
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

    /** La mira multiplica el alcance efectivo del arma. */
    @Override
    protected float alcanceEfectivo() {
        return super.alcanceEfectivo() * zoom;
    }

    /** Con el zoom activo la bala aprovecha la penetración. Parte del daño base del padre. */
    @Override
    protected int calcularDanioPorBala() {
        int base = super.calcularDanioPorBala();
        return zoomActivo ? Math.round(base * (1f + penetracion)) : base;
    }
}
