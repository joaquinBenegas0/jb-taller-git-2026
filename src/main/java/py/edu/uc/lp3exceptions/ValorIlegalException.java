package py.edu.uc.lp3exceptions;

/**
 * Un dato rompe un invariante del juego (precio 0, blindaje 150, cargador vacío de fábrica...).
 * La lanzan los services antes de guardar: el objeto nunca llega a la base.
 */
public class ValorIlegalException extends Cs2Exception {

    public ValorIlegalException(String motivo) {
        super(motivo);
    }

    @Override
    public String codigo() {
        return "VALOR_ILEGAL";
    }
}
