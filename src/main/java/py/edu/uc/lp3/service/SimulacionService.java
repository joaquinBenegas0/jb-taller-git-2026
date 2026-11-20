package py.edu.uc.lp3.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.ArmaDeFuego;
import py.edu.uc.lp3.domain.Impacto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.ImpactoRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/**
 * La simulación persistida: un jugador le dispara a otro con su arma equipada.
 * Aplica la regla de munición y la de daño con blindaje y guarda todo en una sola transacción.
 * Primero se valida todo y recién después cambia el estado; si algo falla, la transacción se
 * revierte y la base queda igual.
 */
@Service
public class SimulacionService {

    private final JugadorService jugadores;
    private final JugadorRepository jugadorRepository;
    private final ArmaRepository armaRepository;
    private final ImpactoRepository impactos;
    private final ArmaService armas;
    private final ReglaDeMunicion reglaDeMunicion;
    private final ReglaDeDanio reglaDeDanio;

    public SimulacionService(JugadorService jugadores, JugadorRepository jugadorRepository,
                             ArmaRepository armaRepository, ImpactoRepository impactos, ArmaService armas,
                             ReglaDeMunicion reglaDeMunicion, ReglaDeDanio reglaDeDanio) {
        this.jugadores = jugadores;
        this.jugadorRepository = jugadorRepository;
        this.armaRepository = armaRepository;
        this.impactos = impactos;
        this.armas = armas;
        this.reglaDeMunicion = reglaDeMunicion;
        this.reglaDeDanio = reglaDeDanio;
    }

    @Transactional
    public Impacto simular(Long tiradorId, Long objetivoId, float distancia) {
        if (tiradorId == null || objetivoId == null) {
            throw new ValorIlegalException("Faltan tiradorId u objetivoId");
        }
        if (distancia < 0f) {
            throw new ValorIlegalException("La distancia no puede ser negativa");
        }
        Jugador tirador = jugadores.buscar(tiradorId);
        Jugador objetivo = jugadores.buscar(objetivoId);
        ArmaDeFuego arma = validarTirador(tirador, objetivo);

        // Regla 2 (condiciones): el objetivo es un enemigo vivo.
        reglaDeDanio.validarObjetivo(tirador.obtenerNombre(), tirador.obtenerEquipo(),
                objetivo.obtenerNombre(), objetivo.obtenerEquipo(), objetivo.obtenerVida());
        // Regla 1: gasta balas; con el cargador vacío lanza MunicionAgotadaException.
        ReglaDeMunicion.Resultado municion = reglaDeMunicion.aplicar(arma.obtenerNombre(),
                arma.obtenerMunicionCargador(), arma.obtenerMunicionReserva(), arma.balasPorDisparo());
        int danio = armas.danioPorBalaA(arma, distancia) * municion.balasGastadas();
        // Regla 2 (reparto): blindaje y vida.
        ReglaDeDanio.Resultado reparto = reglaDeDanio.repartir(danio, objetivo.obtenerVida(),
                objetivo.obtenerBlindaje());
        int recompensa = reparto.eliminado() ? recompensaTopeada(tirador, arma) : 0;

        // Todo validado: recién ahora cambia el estado.
        arma.actualizarMunicion(municion.municionRestante(), arma.obtenerMunicionReserva());
        objetivo.actualizarSalud(reparto.vidaFinal(), reparto.blindajeFinal());
        tirador.actualizarDinero(tirador.obtenerDinero() + recompensa);
        armaRepository.save(arma);
        jugadorRepository.save(tirador);
        jugadorRepository.save(objetivo);
        return impactos.save(new Impacto(tirador, objetivo, arma, distancia, danio,
                reparto.absorbidoPorBlindaje(), reparto.danioALaVida(), recompensa));
    }

    @Transactional(readOnly = true)
    public List<Impacto> historial(Long tiradorId) {
        return tiradorId == null ? impactos.findAll() : impactos.findByTiradorIdOrderByIdAsc(tiradorId);
    }

    /** El tirador tiene que estar vivo, con un arma de fuego, y no apuntarse a sí mismo. */
    private static ArmaDeFuego validarTirador(Jugador tirador, Jugador objetivo) {
        if (tirador.estaEliminado()) {
            throw new DisparoNoPermitidoException(tirador.obtenerNombre() + " está eliminado y no puede disparar");
        }
        Arma arma = tirador.obtenerArma();
        if (arma == null) {
            throw new DisparoNoPermitidoException(tirador.obtenerNombre() + " no tiene un arma equipada");
        }
        if (tirador.obtenerId() != null && tirador.obtenerId().equals(objetivo.obtenerId()) || tirador == objetivo) {
            throw new DisparoNoPermitidoException(tirador.obtenerNombre() + " no puede dispararse a sí mismo");
        }
        if (!(arma instanceof ArmaDeFuego deFuego)) {
            throw new DisparoNoPermitidoException(arma.obtenerNombre()
                    + " no es un arma de fuego: no se puede disparar con ella");
        }
        return deFuego;
    }

    /** La recompensa del arma, sin pasar del tope de dinero. */
    private static int recompensaTopeada(Jugador tirador, Arma arma) {
        return Math.min(arma.recompensaPorEliminacion(), Jugador.DINERO_MAXIMO - tirador.obtenerDinero());
    }
}
