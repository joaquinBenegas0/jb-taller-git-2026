package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;
import py.edu.uc.lp3.rest.dto.NuevoJugador;
import py.edu.uc.lp3exceptions.RecursoNoEncontradoException;

/** Alta y consulta de jugadores: son los tiradores y objetivos de la simulación. */
@RestController
@RequestMapping(ApiPaths.JUGADORES)
public class JugadorController {

    private final JugadorRepository jugadores;
    private final ArmaRepository armas;

    public JugadorController(JugadorRepository jugadores, ArmaRepository armas) {
        this.jugadores = jugadores;
        this.armas = armas;
    }

    @GetMapping
    public List<Jugador> listar() {
        return jugadores.findAll();
    }

    @GetMapping(ApiPaths.POR_ID)
    public Jugador buscar(@PathVariable("id") Long id) {
        return jugadores.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("jugador", id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Jugador crear(@RequestBody NuevoJugador pedido) {
        Arma arma = pedido.armaId() == null ? null
                : armas.findById(pedido.armaId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("arma", pedido.armaId()));
        int blindaje = pedido.blindaje() == null ? 0 : pedido.blindaje();
        int dinero = pedido.dinero() == null ? Jugador.DINERO_INICIAL : pedido.dinero();
        return jugadores.save(new Jugador(pedido.nombre(), pedido.equipo(), blindaje, dinero, arma));
    }
}
