package py.edu.uc.lp3.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.domain.RifleAsalto;
import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;
import py.edu.uc.lp3exceptions.MunicionAgotadaException;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/** Las reglas sin estado y los invariantes que antes estaban en los constructores. */
class ReglasYValidacionesTest {

    private final ReglaDeMunicion municion = new ReglaDeMunicion();
    private final ReglaDeDanio danio = new ReglaDeDanio();
    private final ValidadorDeArmas validador = new ValidadorDeArmas();

    @Test
    void municionGastaLasBalasDelModoSinPasarseDeLasQueQuedan() {
        assertEquals(new ReglaDeMunicion.Resultado(5, 25), municion.aplicar("AK-47", 30, 90, 5));
        assertEquals(new ReglaDeMunicion.Resultado(3, 0), municion.aplicar("AK-47", 3, 90, 5));
        assertThrows(MunicionAgotadaException.class, () -> municion.aplicar("AK-47", 0, 90, 5));
    }

    @Test
    void danioReparteEntreBlindajeYVidaSinBajarDeCero() {
        ReglaDeDanio.Resultado parcial = danio.repartir(180, 100, 100);
        assertEquals(90, parcial.absorbidoPorBlindaje());
        assertEquals(10, parcial.vidaFinal());
        assertTrue(danio.repartir(180, 10, 10).eliminado());
        assertEquals(0, danio.repartir(180, 10, 10).vidaFinal());
    }

    @Test
    void danioRechazaFuegoAmigoYObjetivoEliminado() {
        assertThrows(DisparoNoPermitidoException.class, () -> danio.validarObjetivo(
                "Sombra", Equipo.TERRORISTAS, "Cuervo", Equipo.TERRORISTAS, 100));
        assertThrows(DisparoNoPermitidoException.class, () -> danio.validarObjetivo(
                "Sombra", Equipo.TERRORISTAS, "Halcón", Equipo.ANTITERRORISTAS, 0));
    }

    @Test
    void elValidadorRechazaLoQueAntesRechazabaElConstructor() {
        assertDoesNotThrow(() -> validador.validar(new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS)));
        assertThrows(ValorIlegalException.class,
                () -> validador.validar(new RifleAsalto("AK-47", -5, Equipo.TERRORISTAS)));
        assertThrows(ValorIlegalException.class,
                () -> validador.validar(new Pistola("Glock", 200, Equipo.TERRORISTAS, 30, 0.6f, 0, 120, 2.2f)));
    }
}
