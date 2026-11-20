package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/**
 * Regla 2 (daño con blindaje): solo se le dispara a un enemigo vivo; el blindaje absorbe hasta
 * la mitad del daño y el resto baja la vida, que nunca queda por debajo de 0. Con vida 0 el objetivo
 * queda eliminado. No usa la base: la aplica SimulacionService y se publica en la API de reglas.
 */
@Service
public class ReglaDeDanio {

    /** Cómo queda el objetivo después del impacto. */
    public record Resultado(int absorbidoPorBlindaje, int danioALaVida, int vidaFinal, int blindajeFinal,
                            boolean eliminado) {
    }

    /** El objetivo tiene que ser un enemigo vivo. */
    public void validarObjetivo(String tirador, Equipo equipoTirador, String objetivo,
                                Equipo equipoObjetivo, int vidaObjetivo) {
        if (equipoTirador == null || equipoObjetivo == null) {
            throw new ValorIlegalException("Tirador y objetivo deben tener equipo");
        }
        if (vidaObjetivo == 0) {
            throw new DisparoNoPermitidoException(objetivo + " ya está eliminado");
        }
        if (equipoTirador == equipoObjetivo) {
            throw new DisparoNoPermitidoException("Fuego amigo: " + tirador + " y " + objetivo
                    + " son del mismo equipo (" + equipoTirador + ")");
        }
    }

    /** Reparto del daño entre blindaje y vida. */
    public Resultado repartir(int danio, int vida, int blindaje) {
        if (danio < 0) {
            throw new ValorIlegalException("El daño no puede ser negativo");
        }
        if (vida < 0 || vida > Jugador.VIDA_MAXIMA) {
            throw new ValorIlegalException("La vida va de 0 a " + Jugador.VIDA_MAXIMA);
        }
        if (blindaje < 0 || blindaje > Jugador.BLINDAJE_MAXIMO) {
            throw new ValorIlegalException("El blindaje va de 0 a " + Jugador.BLINDAJE_MAXIMO);
        }
        int absorbido = Math.min(blindaje, danio / 2);
        int vidaFinal = Math.max(0, vida - (danio - absorbido));
        return new Resultado(absorbido, vida - vidaFinal, vidaFinal, blindaje - absorbido, vidaFinal == 0);
    }
}
