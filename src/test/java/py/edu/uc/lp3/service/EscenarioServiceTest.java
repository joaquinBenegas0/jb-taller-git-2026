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

import com.fasterxml.jackson.databind.JsonNode;

import py.edu.uc.lp3.cliente.ReglaRemotaDeDisparoRapido;
import py.edu.uc.lp3.cliente.ReglaRemotaDeImpacto;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Impacto;
import py.edu.uc.lp3.domain.JugadaCruzada;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.RifleAsalto;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.ImpactoRepository;
import py.edu.uc.lp3.repository.JugadaCruzadaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;
import py.edu.uc.lp3exceptions.RechazoDeCompaneroException;

/**
 * El escenario sin red: A2 y A3 son dobles que aplican una versión simple de la regla de cada uno.
 * Se comprueba que solo se guarda si los dos aceptan y que el rechazo dice de quién fue.
 */
class EscenarioServiceTest {

    /** Doble de A2 (Matías): estabilidad mayor a 0.7 y al menos 2 balas. */
    static class A2Falso implements ReglaRemotaDeDisparoRapido {
        float estabilidadConsultada = -1f;

        @Override
        public JsonNode validar(float estabilidad, int municionActual) {
            estabilidadConsultada = estabilidad;
            if (estabilidad <= 0.7f || municionActual < 2) {
                throw new RechazoDeCompaneroException("A2", "Matías", 400, "{\"error\":\"regla_de_juego\"}");
            }
            return null;
        }

        @Override
        public String companero() {
            return "Matías";
        }
    }

    /** Doble de A3 (Marcelo): rechaza a un jugador con vida 0. */
    static class A3Falso implements ReglaRemotaDeImpacto {
        boolean consultado;
        String modeloConsultado;

        @Override
        public JsonNode validar(String modelo, float distancia, int vida, int blindaje) {
            consultado = true;
            modeloConsultado = modelo;
            if (vida == 0) {
                throw new RechazoDeCompaneroException("A3", "Marcelo", 409,
                        "{\"excepcion\":\"JugadorEliminadoException\"}");
            }
            return null;
        }

        @Override
        public String companero() {
            return "Marcelo";
        }
    }

    private final A2Falso a2 = new A2Falso();
    private final A3Falso a3 = new A3Falso();
    private JugadaCruzadaRepository jugadas;
    private ImpactoRepository impactos;
    private EscenarioService escenario;
    private Francotirador awp;
    private Jugador sombra;
    private Jugador cuervo;

    @BeforeEach
    void armar() {
        JugadorRepository jugadorRepository = mock(JugadorRepository.class);
        ArmaRepository armaRepository = mock(ArmaRepository.class);
        impactos = mock(ImpactoRepository.class);
        jugadas = mock(JugadaCruzadaRepository.class);
        when(armaRepository.save(any(Arma.class))).thenAnswer(i -> i.getArgument(0));
        when(jugadorRepository.save(any(Jugador.class))).thenAnswer(i -> i.getArgument(0));
        when(impactos.save(any(Impacto.class))).thenAnswer(i -> i.getArgument(0));
        when(jugadas.save(any(JugadaCruzada.class))).thenAnswer(i -> i.getArgument(0));

        awp = new Francotirador("AWP", 4750);
        sombra = new Jugador("Sombra", Equipo.TERRORISTAS);
        cuervo = new Jugador("Cuervo", Equipo.TERRORISTAS);
        when(jugadorRepository.findById(1L)).thenReturn(Optional.of(sombra));
        when(jugadorRepository.findById(2L)).thenReturn(Optional.of(
                new Jugador("Halcón", Equipo.ANTITERRORISTAS, 100, 800, awp)));
        when(jugadorRepository.findById(4L)).thenReturn(Optional.of(cuervo));
        when(jugadorRepository.findById(5L)).thenReturn(Optional.of(new Jugador("Raso", Equipo.TERRORISTAS, 0, 800,
                new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS))));

        ReglaDeMunicion municion = new ReglaDeMunicion();
        ArmaService armas = new ArmaService(armaRepository, jugadorRepository, new ValidadorDeArmas(), municion);
        JugadorService jugadores = new JugadorService(jugadorRepository, armas);
        SimulacionService simulacion = new SimulacionService(jugadores, jugadorRepository, armaRepository, impactos,
                armas, municion, new ReglaDeDanio());
        escenario = new EscenarioService(jugadores, a2, a3, new RegistroDeJugadas(simulacion, jugadas));
    }

    @Test
    void siLosDosAceptanGastaDosBalasYGuardaLaJugada() {
        JugadaCruzada jugada = escenario.jugar(2L, 1L, 40f, true);

        assertEquals(1.0f, a2.estabilidadConsultada);
        assertEquals("AWP", a3.modeloConsultado);
        assertEquals(3, awp.obtenerMunicionCargador());
        assertTrue(sombra.estaEliminado());
        assertEquals(1.0f, jugada.obtenerEstabilidad());
    }

    @Test
    void siA2RechazaNoSePreguntaAA3YNadaCambia() {
        RechazoDeCompaneroException e = assertThrows(RechazoDeCompaneroException.class,
                () -> escenario.jugar(2L, 4L, 40f, false));

        assertEquals("A2", e.rol());
        assertEquals(false, a3.consultado);
        assertEquals(100, cuervo.obtenerVida());
        assertEquals(5, awp.obtenerMunicionCargador());
        verify(jugadas, never()).save(any(JugadaCruzada.class));
    }

    @Test
    void siA3RechazaLaBaseQuedaIgualYElRechazoEsDeA3() {
        escenario.jugar(2L, 1L, 40f, true);

        RechazoDeCompaneroException e = assertThrows(RechazoDeCompaneroException.class,
                () -> escenario.jugar(2L, 1L, 40f, true));

        assertEquals("A3", e.rol());
        assertEquals(3, awp.obtenerMunicionCargador());
    }

    @Test
    void sinFrancotiradorNoSeConsultaANadie() {
        assertThrows(DisparoNoPermitidoException.class, () -> escenario.jugar(5L, 1L, 40f, true));
        assertEquals(-1f, a2.estabilidadConsultada);
        verify(impactos, never()).save(any(Impacto.class));
    }
}
