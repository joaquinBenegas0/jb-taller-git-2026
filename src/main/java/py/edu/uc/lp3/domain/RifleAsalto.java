package py.edu.uc.lp3.domain;

public class RifleAsalto extends ArmaDeFuego {

    private ModoDisparo modoDisparo;
    private final float cadenciaDisparo;

    /** Constructor simple: rifle con valores estándar (tipo AK-47). */
    public RifleAsalto(String nombre, int precio, Equipo equipo) {
        this(nombre, precio, equipo, 36, 0.73f, 30, 90, 2.5f, 10f);
    }

    /** Constructor completo: llama a super y valida la cadencia. */
    public RifleAsalto(String nombre, int precio, Equipo equipo, int danio, float precision,
                       int capacidadCargador, int municionReserva, float tiempoRecarga,
                       float cadenciaDisparo) {
        super(nombre, precio, equipo, danio, precision, capacidadCargador, municionReserva, tiempoRecarga);
        if (cadenciaDisparo <= 0f) {
            throw new IllegalArgumentException("La cadencia debe ser mayor a 0");
        }
        this.cadenciaDisparo = cadenciaDisparo;
        this.modoDisparo = ModoDisparo.AUTOMATICO;
    }

    public void cambiarModo(ModoDisparo nuevoModo) {
        if (nuevoModo == null) {
            throw new IllegalArgumentException("El modo de disparo no puede ser nulo");
        }
        this.modoDisparo = nuevoModo;
    }

    public ModoDisparo obtenerModo() {
        return modoDisparo;
    }

    public float obtenerCadencia() {
        return cadenciaDisparo;
    }

    @Override
    protected int balasPorDisparo() {
        return modoDisparo.balasPorDisparo();
    }
}
