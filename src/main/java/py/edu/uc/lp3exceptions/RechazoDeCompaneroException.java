package py.edu.uc.lp3exceptions;

/**
 * Un servicio compañero (A2 o A3) respondió que la jugada no es legal según su regla.
 * Guarda quién rechazó y lo que respondió, para que el cliente vea de dónde vino el rechazo.
 */
public class RechazoDeCompaneroException extends Cs2Exception {

    private final String rol;
    private final String companero;
    private final int statusDelCompanero;
    private final String respuestaDelCompanero;

    public RechazoDeCompaneroException(String rol, String companero, int statusDelCompanero,
                                       String respuestaDelCompanero) {
        super("La jugada la rechazó " + rol + " (" + companero + ") con HTTP " + statusDelCompanero);
        this.rol = rol;
        this.companero = companero;
        this.statusDelCompanero = statusDelCompanero;
        this.respuestaDelCompanero = respuestaDelCompanero;
    }

    @Override
    public String codigo() {
        return "RECHAZO_DE_" + rol;
    }

    public String rol() {
        return rol;
    }

    public String companero() {
        return companero;
    }

    public int statusDelCompanero() {
        return statusDelCompanero;
    }

    public String respuestaDelCompanero() {
        return respuestaDelCompanero;
    }
}
