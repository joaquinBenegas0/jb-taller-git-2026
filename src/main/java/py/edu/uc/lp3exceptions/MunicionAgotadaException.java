package py.edu.uc.lp3exceptions;

/** Regla 1 (munición): no se puede disparar con el cargador vacío. */
public class MunicionAgotadaException extends Cs2Exception {

    public MunicionAgotadaException(String arma, int municionReserva) {
        super(arma + " tiene el cargador vacío (reserva: " + municionReserva + "): hay que recargar antes de disparar");
    }

    @Override
    public String codigo() {
        return "MUNICION_AGOTADA";
    }
}
