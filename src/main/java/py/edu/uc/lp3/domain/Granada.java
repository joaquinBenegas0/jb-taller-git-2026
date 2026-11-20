package py.edu.uc.lp3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Arma arrojadiza: no tiene munición, pero sí un ciclo de vida (disponible -> lanzada -> detonada).
 * La entidad guarda en qué paso está; el ciclo lo controla ArmaService.
 */
@Entity
@DiscriminatorValue("granada")
public class Granada extends Arma {

    @Column(name = "danio_explosion")
    private int danio;
    private float radioExplosion;
    private float tiempoActivacion;
    private boolean esLetal;
    private String efecto;
    private boolean lanzada;
    private boolean detonada;
    @JsonIgnore
    private long momentoLanzamiento;

    /** Solo para JPA. */
    protected Granada() {
    }

    /** Sobrecarga para granadas explosivas (letales): solo se indica el daño. */
    public Granada(String nombre, int precio, Equipo equipo, int danio) {
        this(nombre, precio, equipo, danio, 4f, 1.5f, true, "onda expansiva");
    }

    /** Sobrecarga para granadas tácticas (no letales): solo se indica el efecto. */
    public Granada(String nombre, int precio, Equipo equipo, String efecto) {
        this(nombre, precio, equipo, 0, 6f, 1.5f, false, efecto);
    }

    /** Constructor completo: llama a super. Letalidad, radio y efecto los valida ValidadorDeArmas. */
    public Granada(String nombre, int precio, Equipo equipo, int danio, float radioExplosion,
                   float tiempoActivacion, boolean esLetal, String efecto) {
        super(nombre, precio, equipo);
        this.danio = danio;
        this.radioExplosion = radioExplosion;
        this.tiempoActivacion = tiempoActivacion;
        this.esLetal = esLetal;
        this.efecto = efecto;
    }

    /** Una granada se usa lanzándola. Solo describe; lanzar y detonar los hace ArmaService. */
    @Override
    public String usar() {
        return obtenerNombre() + " se lanza y detona a los " + tiempoActivacion + " s en un radio de "
                + radioExplosion + " m: " + efecto + (esLetal ? " (" + danio + " de daño)" : "");
    }

    /** Lo llama ArmaService al lanzarla. */
    public void marcarLanzada(long momento) {
        this.lanzada = true;
        this.momentoLanzamiento = momento;
    }

    /** Lo llama ArmaService cuando ya pasó el tiempo de activación. */
    public void marcarDetonada() {
        this.detonada = true;
    }

    public int obtenerDanio() {
        return danio;
    }

    public float obtenerRadioExplosion() {
        return radioExplosion;
    }

    public float obtenerTiempoActivacion() {
        return tiempoActivacion;
    }

    public boolean esLetal() {
        return esLetal;
    }

    public String obtenerEfecto() {
        return efecto;
    }

    public long obtenerMomentoLanzamiento() {
        return momentoLanzamiento;
    }

    public boolean fueLanzada() {
        return lanzada;
    }

    public boolean detono() {
        return detonada;
    }
}
