package py.edu.uc.lp3.domain;

public class Pistola extends ArmaDeFuego {

    private boolean modoRafaga;

    /** Constructor simple: pistola con valores estándar (tipo Glock-18). */
    public Pistola(String nombre, int precio, Equipo equipo) {
        this(nombre, precio, equipo, 30, 0.6f, 20, 120, 2.2f);
    }

    /** Constructor completo: llama a super con todos los valores. */
    public Pistola(String nombre, int precio, Equipo equipo, int danio, float precision,
                   int capacidadCargador, int municionReserva, float tiempoRecarga) {
        super(nombre, precio, equipo, danio, precision, capacidadCargador, municionReserva, tiempoRecarga);
        this.modoRafaga = false;
    }

    public void activarModoRafaga() {
        modoRafaga = true;
    }

    public void desactivarModoRafaga() {
        modoRafaga = false;
    }

    public boolean estaEnModoRafaga() {
        return modoRafaga;
    }

    @Override
    protected int balasPorDisparo() {
        return modoRafaga ? 3 : 1;
    }
}
