package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Impacto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.ImpactoRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.rest.dto.DisparoSimulado;
import py.edu.uc.lp3exceptions.RecursoNoEncontradoException;

/**
 * Recurso de la simulación: /api/simulaciones/disparos.
 * El controller busca a los jugadores, dispara la regla del dominio (Jugador.dispararA)
 * y guarda el resultado. Si una regla falla, la transacción se revierte y la base queda igual.
 */
@RestController
@RequestMapping(ApiPaths.DISPAROS)
public class SimulacionController {

    private final JugadorRepository jugadores;
    private final ArmaRepository armas;
    private final ImpactoRepository impactos;

    public SimulacionController(JugadorRepository jugadores, ArmaRepository armas, ImpactoRepository impactos) {
        this.jugadores = jugadores;
        this.armas = armas;
        this.impactos = impactos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Impacto simular(@RequestBody DisparoSimulado disparo) {
        Jugador tirador = buscarJugador(disparo.tiradorId());
        Jugador objetivo = buscarJugador(disparo.objetivoId());
        Impacto impacto = tirador.dispararA(objetivo, disparo.distancia());
        armas.save(tirador.obtenerArma());
        jugadores.save(tirador);
        jugadores.save(objetivo);
        return impactos.save(impacto);
    }

    /** Historial persistido; con ?tiradorId= filtra por tirador. */
    @GetMapping
    public List<Impacto> historial(@RequestParam(value = "tiradorId", required = false) Long tiradorId) {
        return tiradorId == null ? impactos.findAll() : impactos.findByTiradorIdOrderByIdAsc(tiradorId);
    }

    private Jugador buscarJugador(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Faltan tiradorId u objetivoId");
        }
        return jugadores.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("jugador", id));
    }
}
