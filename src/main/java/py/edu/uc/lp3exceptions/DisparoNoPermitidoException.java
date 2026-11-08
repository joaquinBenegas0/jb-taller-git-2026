package py.edu.uc.lp3exceptions;

/**
 * Regla 2 (daño): solo se le dispara a un enemigo vivo, estando vivo y con un arma de fuego.
 * Cubre fuego amigo, objetivo ya eliminado, tirador eliminado o sin arma de fuego.
 */
public class DisparoNoPermitidoException extends Cs2Exception {

    public DisparoNoPermitidoException(String motivo) {
        super(motivo);
    }

    @Override
    public String codigo() {
        return "DISPARO_NO_PERMITIDO";
    }
}
