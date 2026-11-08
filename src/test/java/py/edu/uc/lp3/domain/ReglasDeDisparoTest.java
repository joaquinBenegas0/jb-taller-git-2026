package py.edu.uc.lp3.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;
import py.edu.uc.lp3exceptions.MunicionAgotadaException;

/** Las dos reglas de la simulación, probadas sin Spring ni base de datos. */
class ReglasDeDisparoTest {

    @Test
    void elDisparoGastaBalasYElBlindajeAbsorbeLaMitad() {
        RifleAsalto ak = new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS);
        Jugador sombra = new Jugador("Sombra", Equipo.TERRORISTAS, 0, 800, ak);
        Jugador halcon = new Jugador("Halcón", Equipo.ANTITERRORISTAS, 100, 800, null);

        Impacto impacto = sombra.dispararA(halcon, 20f);

        assertEquals(25, ak.obtenerMunicionCargador());
        assertEquals(90, impacto.obtenerAbsorbidoPorBlindaje());
        assertEquals(10, halcon.obtenerBlindaje());
        assertEquals(10, halcon.obtenerVida());
    }

    @Test
    void laEliminacionPagaLaRecompensaDelArma() {
        RifleAsalto ak = new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS);
        Jugador sombra = new Jugador("Sombra", Equipo.TERRORISTAS, 0, 800, ak);
        Jugador halcon = new Jugador("Halcón", Equipo.ANTITERRORISTAS);

        Impacto impacto = sombra.dispararA(halcon, 20f);

        assertTrue(halcon.estaEliminado());
        assertEquals(0, halcon.obtenerVida());
        assertEquals(300, impacto.obtenerRecompensaCobrada());
        assertEquals(1100, sombra.obtenerDinero());
    }

    @Test
    void conElCargadorVacioNoSeDisparaYNadaCambia() {
        Pistola usp = new Pistola("USP-S", 200, Equipo.ANTITERRORISTAS, 35, 0.8f, 1, 0, 2.2f);
        Jugador lince = new Jugador("Lince", Equipo.ANTITERRORISTAS, 0, 800, usp);
        Jugador sombra = new Jugador("Sombra", Equipo.TERRORISTAS);
        lince.dispararA(sombra, 10f);
        int vidaAntes = sombra.obtenerVida();

        assertThrows(MunicionAgotadaException.class, () -> lince.dispararA(sombra, 10f));
        assertEquals(vidaAntes, sombra.obtenerVida());
    }

    @Test
    void elFuegoAmigoYLaGranadaNoDisparan() {
        Granada he = new Granada("HE", 300, Equipo.TERRORISTAS, 98);
        Jugador sombra = new Jugador("Sombra", Equipo.TERRORISTAS, 0, 800,
                new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS));
        Jugador cuervo = new Jugador("Cuervo", Equipo.TERRORISTAS, 0, 800, he);
        Jugador lince = new Jugador("Lince", Equipo.ANTITERRORISTAS);

        assertThrows(DisparoNoPermitidoException.class, () -> sombra.dispararA(cuervo, 10f));
        assertThrows(DisparoNoPermitidoException.class, () -> cuervo.dispararA(lince, 10f));
    }
}
