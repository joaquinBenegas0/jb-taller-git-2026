package py.edu.uc.lp3.domain;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonRawValue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

/**
 * Jugada del escenario cruzado de POO-09: un disparo rápido de francotirador que aceptaron
 * A2 (disparo rápido, Matías) y A3 (vida, Marcelo).
 * Guarda el Impacto que dejó en la partida y lo que respondió cada compañero (JSON tal cual).
 * Solo existe si las dos reglas ajenas aceptaron; si alguna rechazó no se crea.
 */
@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
public class JugadaCruzada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Impacto impacto;

    /** 1.0 si el tirador aguantó la respiración, 0.5 si no. */
    private float estabilidad;

    private String companeroA2;
    @Column(length = 2000)
    @JsonRawValue
    private String respuestaA2;
    private String companeroA3;
    @Column(length = 2000)
    @JsonRawValue
    private String respuestaA3;
    private Instant momento;

    /** Solo para JPA. */
    protected JugadaCruzada() {
    }

    public JugadaCruzada(Impacto impacto, float estabilidad, String companeroA2, String respuestaA2, String companeroA3,
                         String respuestaA3, Instant momento) {
        this.impacto = impacto;
        this.estabilidad = estabilidad;
        this.companeroA2 = companeroA2;
        this.respuestaA2 = respuestaA2;
        this.companeroA3 = companeroA3;
        this.respuestaA3 = respuestaA3;
        this.momento = momento;
    }

    public Long obtenerId() {
        return id;
    }

    public Impacto obtenerImpacto() {
        return impacto;
    }

    public float obtenerEstabilidad() {
        return estabilidad;
    }

    public String obtenerRespuestaA2() {
        return respuestaA2;
    }

    public String obtenerRespuestaA3() {
        return respuestaA3;
    }
}
