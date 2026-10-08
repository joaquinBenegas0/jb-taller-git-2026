package py.edu.uc.lp3.jb_taller_git_2026.cs2;

public class Subfusil extends ArmaDeFuego {

    private final float controlRetroceso;

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
}
