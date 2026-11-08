package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;

/**
 * Jugador de una partida de CS2: tiene vida, blindaje (kevlar), dinero y un arma equipada.
 * Vida, blindaje y dinero solo cambian por mensajes del juego; no hay setters.
 */
@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
public class Jugador implements Reportable {

    public static final int VIDA_MAXIMA = 100;
    public static final int BLINDAJE_MAXIMO = 100;
    public static final int DINERO_MAXIMO = 16000;
    public static final int DINERO_INICIAL = 800;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private Equipo equipo;

    private int vida;
    private int blindaje;
    private int dinero;

    @ManyToOne
    private Arma arma;

    /** Solo para JPA. */
    protected Jugador() {
    }

    /** Constructor simple: arranca la ronda sin blindaje, con el dinero inicial y sin arma. */
    public Jugador(String nombre, Equipo equipo) {
        this(nombre, equipo, 0, DINERO_INICIAL, null);
    }

    /** Constructor completo: siempre con la vida llena; valida blindaje y dinero. */
    public Jugador(String nombre, Equipo equipo, int blindaje, int dinero, Arma arma) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El jugador necesita un nombre");
        }
        if (equipo == null) {
            throw new IllegalArgumentException("El jugador debe pertenecer a un equipo");
        }
        if (blindaje < 0 || blindaje > BLINDAJE_MAXIMO) {
            throw new IllegalArgumentException("El blindaje va de 0 a " + BLINDAJE_MAXIMO);
        }
        if (dinero < 0 || dinero > DINERO_MAXIMO) {
            throw new IllegalArgumentException("El dinero va de 0 a " + DINERO_MAXIMO);
        }
        this.nombre = nombre;
        this.equipo = equipo;
        this.vida = VIDA_MAXIMA;
        this.blindaje = blindaje;
        this.dinero = dinero;
        this.arma = arma;
    }

    public void equipar(Arma nuevaArma) {
        if (nuevaArma == null) {
            throw new IllegalArgumentException("No se puede equipar un arma nula");
        }
        this.arma = nuevaArma;
    }

    /** El jugador suelta el arma (por ejemplo, cuando la tienda la retira). */
    public void soltarArma() {
        this.arma = null;
    }

    /**
     * Simulación de un disparo: este jugador le dispara a otro con su arma equipada.
     * Regla 1 (munición): el arma gasta balas y con el cargador vacío no dispara.
     * Regla 2 (daño): el blindaje del objetivo absorbe hasta la mitad del daño, el resto baja la vida;
     * si la vida llega a 0 el objetivo queda eliminado y el tirador cobra la recompensa del arma.
     * Primero se validan todas las condiciones y recién después cambia el estado.
     */
    public Impacto dispararA(Jugador objetivo, float distanciaMetros) {
        if (objetivo == null) {
            throw new IllegalArgumentException("El disparo necesita un objetivo");
        }
        if (estaEliminado()) {
            throw new DisparoNoPermitidoException(nombre + " está eliminado y no puede disparar");
        }
        if (arma == null) {
            throw new DisparoNoPermitidoException(nombre + " no tiene un arma equipada");
        }
        if (objetivo == this) {
            throw new DisparoNoPermitidoException(nombre + " no puede dispararse a sí mismo");
        }
        if (objetivo.estaEliminado()) {
            throw new DisparoNoPermitidoException(objetivo.nombre + " ya está eliminado");
        }
        if (objetivo.equipo == equipo) {
            throw new DisparoNoPermitidoException("Fuego amigo: " + nombre + " y " + objetivo.nombre
                    + " son del mismo equipo (" + equipo + ")");
        }
        int danio = arma.impactar(distanciaMetros);
        int vidaAntes = objetivo.vida;
        int blindajeAntes = objetivo.blindaje;
        objetivo.recibirDanio(danio);
        int recompensa = objetivo.estaEliminado() ? cobrarRecompensa(arma.recompensaPorEliminacion()) : 0;
        return new Impacto(this, objetivo, arma, distanciaMetros, danio,
                blindajeAntes - objetivo.blindaje, vidaAntes - objetivo.vida, recompensa);
    }

    /** Regla 2: el blindaje absorbe hasta la mitad del daño; la vida nunca baja de 0. */
    private void recibirDanio(int danio) {
        int absorbido = Math.min(blindaje, danio / 2);
        blindaje -= absorbido;
        vida = Math.max(0, vida - (danio - absorbido));
    }

    /** Suma la recompensa sin pasar del tope de dinero; devuelve lo que efectivamente cobró. */
    private int cobrarRecompensa(int recompensa) {
        int cobrado = Math.min(recompensa, DINERO_MAXIMO - dinero);
        dinero += cobrado;
        return cobrado;
    }

    public boolean estaEliminado() {
        return vida == 0;
    }

    @Override
    public String identificacion() {
        return "Jugador #" + id + " " + nombre;
    }

    @Override
    public String reporteDeCombate() {
        String estado = estaEliminado() ? "ELIMINADO" : "vida " + vida + ", blindaje " + blindaje;
        String conArma = arma == null ? "sin arma" : "con " + arma.obtenerNombre();
        return nombre + " (" + equipo + "): " + estado + ", $" + dinero + ", " + conArma;
    }

    public Long obtenerId() {
        return id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public Equipo obtenerEquipo() {
        return equipo;
    }

    public int obtenerVida() {
        return vida;
    }

    public int obtenerBlindaje() {
        return blindaje;
    }

    public int obtenerDinero() {
        return dinero;
    }

    public Arma obtenerArma() {
        return arma;
    }
}
