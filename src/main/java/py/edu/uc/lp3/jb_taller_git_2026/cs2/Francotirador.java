package py.edu.uc.lp3.jb_taller_git_2026.cs2;

public class Francotirador extends ArmaDeFuego {

    private final float zoom;
    private final float penetracion;
    private boolean zoomActivo;

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

    /** Con el zoom activo la bala aprovecha la penetración. Parte del daño base del padre. */
    @Override
    protected int calcularDanioPorBala() {
        int base = super.calcularDanioPorBala();
        return zoomActivo ? Math.round(base * (1f + penetracion)) : base;
    }
}
