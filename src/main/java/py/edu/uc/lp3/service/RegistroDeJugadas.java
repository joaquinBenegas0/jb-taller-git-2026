package py.edu.uc.lp3.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Impacto;
import py.edu.uc.lp3.domain.JugadaCruzada;
import py.edu.uc.lp3.repository.JugadaCruzadaRepository;

/**
 * Parte persistente del escenario: se llama solo cuando A2 y A3 ya aceptaron.
 * En una sola transacción aplica el disparo rápido en la partida (SimulacionService) y guarda la jugada;
 * si algo falla acá, no queda ni el impacto ni la jugada.
 */
@Service
public class RegistroDeJugadas {

    private final SimulacionService simulacion;
    private final JugadaCruzadaRepository jugadas;

    public RegistroDeJugadas(SimulacionService simulacion, JugadaCruzadaRepository jugadas) {
        this.simulacion = simulacion;
        this.jugadas = jugadas;
    }

    @Transactional
    public JugadaCruzada guardar(Long tiradorId, Long objetivoId, float distancia, float estabilidad,
                                 String companeroA2, String respuestaA2, String companeroA3, String respuestaA3) {
        Impacto impacto = simulacion.simularDisparoRapido(tiradorId, objetivoId, distancia);
        return jugadas.save(new JugadaCruzada(impacto, estabilidad, companeroA2, respuestaA2, companeroA3,
                respuestaA3, Instant.now()));
    }

    @Transactional(readOnly = true)
    public List<JugadaCruzada> listar() {
        return jugadas.findAll();
    }
}
