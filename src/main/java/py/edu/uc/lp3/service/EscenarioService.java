package py.edu.uc.lp3.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import py.edu.uc.lp3.cliente.ReglaRemotaDeDisparoRapido;
import py.edu.uc.lp3.cliente.ReglaRemotaDeImpacto;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.JugadaCruzada;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/**
 * Escenario de POO-09 («disparo rápido cruzado»): un jugador de mi partida hace un disparo rápido
 * (dos balas) con su francotirador, y la jugada se guarda en mi H2 solo si la aceptan la regla de
 * disparo rápido de A2 (Matías) y la regla de vida de A3 (Marcelo).
 *
 * Orden: primero A2, porque si el francotirador no está estabilizado o no tiene dos balas el disparo
 * no existe y no tiene sentido preguntarle a A3 por un impacto que no va a ocurrir; después A3.
 * Las llamadas HTTP van fuera de la transacción: la base se toca recién en RegistroDeJugadas,
 * cuando las dos aceptaron. Si alguna rechaza se lanza RechazoDeCompaneroException con el rol
 * (A2 o A3) y la base queda igual.
 */
@Service
public class EscenarioService {

    /** Estabilidad del francotirador según la jugada: aguantando la respiración, o respirando normal. */
    public static final float ESTABILIDAD_AGUANTANDO = 1.0f;
    public static final float ESTABILIDAD_NORMAL = 0.5f;

    private final JugadorService jugadores;
    private final ReglaRemotaDeDisparoRapido a2;
    private final ReglaRemotaDeImpacto a3;
    private final RegistroDeJugadas registro;

    public EscenarioService(JugadorService jugadores, ReglaRemotaDeDisparoRapido a2, ReglaRemotaDeImpacto a3,
                            RegistroDeJugadas registro) {
        this.jugadores = jugadores;
        this.a2 = a2;
        this.a3 = a3;
        this.registro = registro;
    }

    public JugadaCruzada jugar(Long tiradorId, Long objetivoId, float distancia, boolean aguantaRespiracion) {
        if (tiradorId == null || objetivoId == null) {
            throw new ValorIlegalException("Faltan tiradorId u objetivoId");
        }
        if (distancia < 0f) {
            throw new ValorIlegalException("La distancia no puede ser negativa");
        }
        Jugador tirador = jugadores.buscar(tiradorId);
        Jugador objetivo = jugadores.buscar(objetivoId);
        Francotirador arma = francotiradorDe(tirador);
        float estabilidad = aguantaRespiracion ? ESTABILIDAD_AGUANTANDO : ESTABILIDAD_NORMAL;

        // A2: ¿el francotirador está estabilizado y tiene dos balas?
        JsonNode respuestaA2 = a2.validar(estabilidad, arma.obtenerMunicionCargador());

        // A3: ¿el objetivo puede recibir el impacto de esa arma a esa distancia?
        JsonNode respuestaA3 = a3.validar(modeloDe(arma), distancia, objetivo.obtenerVida(),
                objetivo.obtenerBlindaje());

        // Las dos aceptaron: recién ahora se toca la base.
        return registro.guardar(tiradorId, objetivoId, distancia, estabilidad, a2.companero(),
                String.valueOf(respuestaA2), a3.companero(), String.valueOf(respuestaA3));
    }

    public List<JugadaCruzada> listar() {
        return registro.listar();
    }

    /** El disparo rápido es de francotirador: si no tiene uno, la jugada ni se arma (regla de A1). */
    private static Francotirador francotiradorDe(Jugador tirador) {
        Arma arma = tirador.obtenerArma();
        if (!(arma instanceof Francotirador francotirador)) {
            throw new DisparoNoPermitidoException(tirador.obtenerNombre()
                    + " no tiene un francotirador equipado: el disparo rápido es solo con francotirador");
        }
        return francotirador;
    }

    /** Nombre del arma en el catálogo de A3: sin lo que va entre paréntesis ("USP-S (última bala)" → "USP-S"). */
    private static String modeloDe(Arma arma) {
        String nombre = arma.obtenerNombre();
        int parentesis = nombre.indexOf(" (");
        return parentesis < 0 ? nombre : nombre.substring(0, parentesis);
    }
}
