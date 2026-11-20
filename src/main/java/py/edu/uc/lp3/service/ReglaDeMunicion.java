package py.edu.uc.lp3.service;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3exceptions.MunicionAgotadaException;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/**
 * Regla 1 (munición): un disparo gasta las balas de su modo, sin pasarse de las que quedan,
 * y con el cargador vacío no se dispara. No usa la base: la aplican SimulacionService y ArmaService,
 * y es la misma que se publica en la API de reglas.
 */
@Service
public class ReglaDeMunicion {

    /** Resultado de aplicar la regla: cuántas balas salen y cuántas quedan en el cargador. */
    public record Resultado(int balasGastadas, int municionRestante) {
    }

    public Resultado aplicar(String arma, int municionCargador, int municionReserva, int balasPorDisparo) {
        if (municionCargador < 0 || municionReserva < 0) {
            throw new ValorIlegalException("La munición no puede ser negativa");
        }
        if (balasPorDisparo <= 0) {
            throw new ValorIlegalException("Un disparo gasta al menos una bala");
        }
        if (municionCargador == 0) {
            throw new MunicionAgotadaException(arma, municionReserva);
        }
        int balas = Math.min(balasPorDisparo, municionCargador);
        return new Resultado(balas, municionCargador - balas);
    }
}
