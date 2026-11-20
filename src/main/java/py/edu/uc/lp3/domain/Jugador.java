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

/**
 * Jugador de una partida de CS2: tiene vida, blindaje (kevlar), dinero y un arma equipada.
 * Es una entidad: solo guarda estado y no tiene setters genéricos. Los topes de vida, blindaje
 * y dinero los valida JugadorService, y el disparo (reglas de munición y daño) lo hace SimulacionService.
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

    /** Constructor completo: siempre con la vida llena. */
    public Jugador(String nombre, Equipo equipo, int blindaje, int dinero, Arma arma) {
        this.nombre = nombre;
        this.equipo = equipo;
        this.vida = VIDA_MAXIMA;
        this.blindaje = blindaje;
        this.dinero = dinero;
        this.arma = arma;
    }

    public void equipar(Arma nuevaArma) {
        this.arma = nuevaArma;
    }

    /** El jugador suelta el arma (por ejemplo, cuando la tienda la retira). */
    public void soltarArma() {
        this.arma = null;
    }

    /** Lo llama SimulacionService después de aplicar la regla de daño. */
    public void actualizarSalud(int nuevaVida, int nuevoBlindaje) {
        this.vida = nuevaVida;
        this.blindaje = nuevoBlindaje;
    }

    /** Lo llama SimulacionService al cobrar una recompensa (ya topeada). */
    public void actualizarDinero(int nuevoDinero) {
        this.dinero = nuevoDinero;
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
