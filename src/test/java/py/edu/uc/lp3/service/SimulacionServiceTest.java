package py.edu.uc.lp3.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.domain.Impacto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.domain.RifleAsalto;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.ImpactoRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;
import py.edu.uc.lp3exceptions.MunicionAgotadaException;

/**
 * Las dos reglas de la simulación, probadas en el service sin Spring ni base:
 * los repositorios son dobles de Mockito que devuelven lo que se les guarda.
 */
class SimulacionServiceTest {

    private JugadorRepository jugadorRepository;
    private ImpactoRepository impactoRepository;
    private SimulacionService simulacion;

    @BeforeEach
    void armarServices() {
        jugadorRepository = mock(JugadorRepository.class);
        ArmaRepository armaRepository = mock(ArmaRepository.class);
        impactoRepository = mock(ImpactoRepository.class);
        when(armaRepository.save(any(Arma.class))).thenAnswer(i -> i.getArgument(0));
        when(jugadorRepository.save(any(Jugador.class))).thenAnswer(i -> i.getArgument(0));
        when(impactoRepository.save(any(Impacto.class))).thenAnswer(i -> i.getArgument(0));
        ReglaDeMunicion municion = new ReglaDeMunicion();
        ArmaService armas = new ArmaService(armaRepository, jugadorRepository, new ValidadorDeArmas(), municion);
        JugadorService jugadores = new JugadorService(jugadorRepository, armas);
        simulacion = new SimulacionService(jugadores, jugadorRepository, armaRepository, impactoRepository,
                armas, municion, new ReglaDeDanio());
    }

    private void enLaBase(long id, Jugador jugador) {
        when(jugadorRepository.findById(id)).thenReturn(Optional.of(jugador));
    }

    @Test
    void elDisparoGastaBalasYElBlindajeAbsorbeLaMitad() {
        RifleAsalto ak = new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS);
        Jugador halcon = new Jugador("Halcón", Equipo.ANTITERRORISTAS, 100, 800, null);
        enLaBase(1, new Jugador("Sombra", Equipo.TERRORISTAS, 0, 800, ak));
        enLaBase(2, halcon);

        Impacto impacto = simulacion.simular(1L, 2L, 20f);

        assertEquals(25, ak.obtenerMunicionCargador());
        assertEquals(90, impacto.obtenerAbsorbidoPorBlindaje());
        assertEquals(10, halcon.obtenerBlindaje());
        assertEquals(10, halcon.obtenerVida());
    }

    @Test
    void laEliminacionPagaLaRecompensaDelArma() {
        Jugador sombra = new Jugador("Sombra", Equipo.TERRORISTAS, 0, 800,
                new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS));
        Jugador halcon = new Jugador("Halcón", Equipo.ANTITERRORISTAS);
        enLaBase(1, sombra);
        enLaBase(2, halcon);

        Impacto impacto = simulacion.simular(1L, 2L, 20f);

        assertTrue(halcon.estaEliminado());
        assertEquals(300, impacto.obtenerRecompensaCobrada());
        assertEquals(1100, sombra.obtenerDinero());
    }

    @Test
    void conElCargadorVacioNoSeDisparaYNadaCambia() {
        Pistola usp = new Pistola("USP-S", 200, Equipo.ANTITERRORISTAS, 35, 0.8f, 1, 0, 2.2f);
        Jugador sombra = new Jugador("Sombra", Equipo.TERRORISTAS);
        enLaBase(3, new Jugador("Lince", Equipo.ANTITERRORISTAS, 0, 800, usp));
        enLaBase(1, sombra);
        simulacion.simular(3L, 1L, 10f);
        int vidaAntes = sombra.obtenerVida();

        assertThrows(MunicionAgotadaException.class, () -> simulacion.simular(3L, 1L, 10f));
        assertEquals(vidaAntes, sombra.obtenerVida());
        assertEquals(0, usp.obtenerMunicionCargador());
    }

    @Test
    void elFuegoAmigoYLaGranadaNoDisparanYNoSeGuardaNada() {
        enLaBase(1, new Jugador("Sombra", Equipo.TERRORISTAS, 0, 800,
                new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS)));
        enLaBase(4, new Jugador("Cuervo", Equipo.TERRORISTAS, 0, 800,
                new Granada("HE", 300, Equipo.TERRORISTAS, 98)));
        enLaBase(3, new Jugador("Lince", Equipo.ANTITERRORISTAS));

        assertThrows(DisparoNoPermitidoException.class, () -> simulacion.simular(1L, 4L, 10f));
        assertThrows(DisparoNoPermitidoException.class, () -> simulacion.simular(4L, 3L, 10f));
        verify(impactoRepository, never()).save(any(Impacto.class));
    }
}
