package py.edu.uc.lp3exceptions;

/** Se pidió un arma o un jugador con un id que la base no tiene. */
public class RecursoNoEncontradoException extends Cs2Exception {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super("No existe " + recurso + " con id " + id);
    }

    @Override
    public String codigo() {
        return "RECURSO_NO_ENCONTRADO";
    }
}
