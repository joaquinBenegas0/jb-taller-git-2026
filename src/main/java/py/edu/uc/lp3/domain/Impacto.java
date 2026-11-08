package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Registro persistido de un disparo simulado: quién, a quién, con qué y qué pasó.
 * Solo lo crea Jugador.dispararA (constructor de paquete) y no tiene mensajes que lo modifiquen.
 * Guarda ids y nombres (no relaciones) para que el historial sobreviva aunque se borre un arma.
 */
@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
public class Impacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tiradorId;
    private String tirador;
    private Long objetivoId;
    private String objetivo;
    private Long armaId;
    private String arma;
    private float distancia;
    private int danioDelArma;
    private int absorbidoPorBlindaje;
    private int danioALaVida;
    private int vidaRestante;
    private int blindajeRestante;
    private boolean eliminado;
    private int recompensaCobrada;

    /** Solo para JPA. */
    protected Impacto() {
    }

    Impacto(Jugador tirador, Jugador objetivo, Arma arma, float distancia, int danioDelArma,
            int absorbidoPorBlindaje, int danioALaVida, int recompensaCobrada) {
        this.tiradorId = tirador.obtenerId();
        this.tirador = tirador.obtenerNombre();
        this.objetivoId = objetivo.obtenerId();
        this.objetivo = objetivo.obtenerNombre();
        this.armaId = arma.obtenerId();
        this.arma = arma.obtenerNombre();
        this.distancia = distancia;
        this.danioDelArma = danioDelArma;
        this.absorbidoPorBlindaje = absorbidoPorBlindaje;
        this.danioALaVida = danioALaVida;
        this.vidaRestante = objetivo.obtenerVida();
        this.blindajeRestante = objetivo.obtenerBlindaje();
        this.eliminado = objetivo.estaEliminado();
        this.recompensaCobrada = recompensaCobrada;
    }

    public Long obtenerId() {
        return id;
    }

    public boolean huboEliminacion() {
        return eliminado;
    }

    public int obtenerDanioALaVida() {
        return danioALaVida;
    }

    public int obtenerAbsorbidoPorBlindaje() {
        return absorbidoPorBlindaje;
    }

    public int obtenerRecompensaCobrada() {
        return recompensaCobrada;
    }
}
