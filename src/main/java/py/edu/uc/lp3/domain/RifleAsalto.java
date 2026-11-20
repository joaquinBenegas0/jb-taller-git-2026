package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@DiscriminatorValue("rifle")
public class RifleAsalto extends ArmaDeFuego {

    @Enumerated(EnumType.STRING)
    private ModoDisparo modoDisparo;
    private float cadenciaDisparo;

    /** Solo para JPA. */
    protected RifleAsalto() {
    }

    /** Constructor simple: rifle con valores estándar (tipo AK-47). */
    public RifleAsalto(String nombre, int precio, Equipo equipo) {
        this(nombre, precio, equipo, 36, 0.73f, 30, 90, 2.5f, 10f);
    }

    /** Constructor completo: llama a super; la cadencia la valida ValidadorDeArmas. */
    public RifleAsalto(String nombre, int precio, Equipo equipo, int danio, float precision,
                       int capacidadCargador, int municionReserva, float tiempoRecarga,
                       float cadenciaDisparo) {
        super(nombre, precio, equipo, danio, precision, capacidadCargador, municionReserva, tiempoRecarga);
        this.cadenciaDisparo = cadenciaDisparo;
        this.modoDisparo = ModoDisparo.AUTOMATICO;
    }

    public void cambiarModo(ModoDisparo nuevoModo) {
        this.modoDisparo = nuevoModo;
    }

    public ModoDisparo obtenerModo() {
        return modoDisparo;
    }

    public float obtenerCadencia() {
        return cadenciaDisparo;
    }

    @Override
    public int balasPorDisparo() {
        return modoDisparo.balasPorDisparo();
    }
}
