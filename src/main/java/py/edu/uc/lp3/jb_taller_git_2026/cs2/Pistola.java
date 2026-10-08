package py.edu.uc.lp3.jb_taller_git_2026.cs2;

public class Pistola extends ArmaDeFuego {

    private boolean modoRafaga;

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
