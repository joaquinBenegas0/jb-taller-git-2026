package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.rest.dto.NuevoJugador;
import py.edu.uc.lp3.service.JugadorService;

/** Jugadores de la partida. Solo traduce HTTP; la lógica está en JugadorService. */
@RestController
@RequestMapping(ApiPaths.JUGADORES)
public class JugadorController {

    private final JugadorService jugadores;

    public JugadorController(JugadorService jugadores) {
        this.jugadores = jugadores;
    }

    @GetMapping
    public List<Jugador> listar() {
        return jugadores.listar();
    }

    @GetMapping(ApiPaths.POR_ID)
    public Jugador buscar(@PathVariable("id") Long id) {
        return jugadores.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Jugador crear(@RequestBody NuevoJugador pedido) {
        return jugadores.crear(pedido.nombre(), pedido.equipo(), pedido.blindaje(), pedido.dinero(),
                pedido.armaId());
    }
}
