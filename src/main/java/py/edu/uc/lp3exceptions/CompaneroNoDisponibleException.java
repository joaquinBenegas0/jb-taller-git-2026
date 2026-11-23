package py.edu.uc.lp3exceptions;

/**
 * No se pudo hablar con un servicio compañero (apagado, URL mal configurada o error 5xx).
 * No es un rechazo de su regla: la jugada no se guarda, pero se avisa que fue un problema de conexión.
 */
public class CompaneroNoDisponibleException extends Cs2Exception {

    private final String rol;
    private final String companero;

    public CompaneroNoDisponibleException(String rol, String companero, String url, String detalle) {
        super(rol + " (" + companero + ") no respondió en " + url + ": " + detalle);
        this.rol = rol;
        this.companero = companero;
    }

    @Override
    public String codigo() {
        return rol + "_NO_DISPONIBLE";
    }

    public String rol() {
        return rol;
    }

    public String companero() {
        return companero;
    }
}
