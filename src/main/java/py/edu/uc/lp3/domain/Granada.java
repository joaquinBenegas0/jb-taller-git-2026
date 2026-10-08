package py.edu.uc.lp3.domain;

/**
 * Arma arrojadiza: no tiene munición, pero sí un ciclo de vida
 * (disponible -> lanzada -> detonada) que nadie de afuera puede saltarse.
 */
public class Granada extends Arma {

    private final int danio;
    private final float radioExplosion;
    private final float tiempoActivacion;
    private final boolean esLetal;
    private final String efecto;
    private boolean lanzada;
    private boolean detonada;
    private long momentoLanzamiento;

    public Granada(String nombre, int precio, Equipo equipo, int danio, float radioExplosion,
                   float tiempoActivacion, boolean esLetal, String efecto) {
        super(nombre, precio, equipo);
        if (esLetal && danio <= 0) {
            throw new IllegalArgumentException("Una granada letal debe hacer daño");
        }
        if (!esLetal && danio != 0) {
            throw new IllegalArgumentException("Una granada no letal no hace daño");
        }
        if (radioExplosion <= 0f) {
            throw new IllegalArgumentException("El radio de explosión debe ser mayor a 0");
        }
        if (tiempoActivacion < 0f) {
            throw new IllegalArgumentException("El tiempo de activación no puede ser negativo");
        }
        if (efecto == null || efecto.isBlank()) {
            throw new IllegalArgumentException("La granada debe tener un efecto");
        }
        this.danio = danio;
        this.radioExplosion = radioExplosion;
        this.tiempoActivacion = tiempoActivacion;
        this.esLetal = esLetal;
        this.efecto = efecto;
    }

    /** Usar una granada es lanzarla. */
    @Override
    public String usar() {
        return lanzar();
    }

    public String lanzar() {
        if (lanzada) {
            return obtenerNombre() + ": ya fue lanzada";
        }
        lanzada = true;
        momentoLanzamiento = System.currentTimeMillis();
        return obtenerNombre() + " lanzada, detona en " + tiempoActivacion + " s";
    }

    /** El cooldown (tiempo de activación) lo controla la propia granada. */
    public String detonar() {
        if (!lanzada) {
            return obtenerNombre() + ": no se puede detonar sin lanzarla";
        }
        if (detonada) {
            return obtenerNombre() + ": ya detonó";
        }
        long transcurrido = System.currentTimeMillis() - momentoLanzamiento;
        if (transcurrido < tiempoActivacion * 1000) {
            return obtenerNombre() + ": todavía no se activó";
        }
        detonada = true;
        String resultado = obtenerNombre() + " detona en un radio de " + radioExplosion + " m: " + efecto;
        return esLetal ? resultado + " (" + danio + " de daño)" : resultado;
    }

    public boolean fueLanzada() {
        return lanzada;
    }

    public boolean detono() {
        return detonada;
    }
}
