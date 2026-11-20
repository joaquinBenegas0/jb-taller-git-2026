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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.rest.controller.ManejadorDeExcepciones.ErrorRespuesta;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.rest.dto.NuevoJugador;
import py.edu.uc.lp3.service.JugadorService;

/** Jugadores de la partida. Solo traduce HTTP; la lógica está en JugadorService. */
@RestController
@RequestMapping(ApiPaths.JUGADORES)
@Tag(name = "Jugadores", description = "Jugadores de la partida")
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
    @ApiResponse(responseCode = "404", description = "Id inexistente (RECURSO_NO_ENCONTRADO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public Jugador buscar(@PathVariable("id") Long id) {
        return jugadores.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un jugador", description = "blindaje (0 a 100), dinero (0 a 16000) y armaId son opcionales")
    @ApiResponse(responseCode = "400", description = "Nombre, equipo, blindaje o dinero ilegal (VALOR_ILEGAL)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "404", description = "armaId inexistente (RECURSO_NO_ENCONTRADO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public Jugador crear(@RequestBody NuevoJugador pedido) {
        return jugadores.crear(pedido.nombre(), pedido.equipo(), pedido.blindaje(), pedido.dinero(),
                pedido.armaId());
    }
}
