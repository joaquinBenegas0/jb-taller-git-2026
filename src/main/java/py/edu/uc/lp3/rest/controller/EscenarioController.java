package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.JugadaCruzada;
import py.edu.uc.lp3.rest.controller.ManejadorDeExcepciones.ErrorRespuesta;
import py.edu.uc.lp3.rest.controller.ManejadorDeExcepciones.RechazoRespuesta;
import py.edu.uc.lp3.rest.dto.DisparoRapido;
import py.edu.uc.lp3.service.EscenarioService;

/** Escenario de POO-09: disparo rápido validado por A2 (disparo rápido) y A3 (vida). Solo traduce HTTP. */
@RestController
@RequestMapping(ApiPaths.ESCENARIO)
@Tag(name = "Escenario POO-09",
        description = "Disparo rápido con francotirador: se guarda solo si A2 (Matías, disparo rápido) y A3 (Marcelo, vida) aceptan")
public class EscenarioController {

    private final EscenarioService escenario;

    public EscenarioController(EscenarioService escenario) {
        this.escenario = escenario;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Hacer un disparo rápido cruzado",
            description = "El tirador dispara dos balas de una vez con su francotirador. Primero consulta la regla de "
                    + "disparo rápido de A2 (estabilidad mayor a 0.7 y al menos 2 balas) y después la regla de vida "
                    + "de A3 (el objetivo no está eliminado). Si las dos aceptan aplica el disparo en la partida y "
                    + "guarda la jugada; si alguna rechaza, la base queda igual y la respuesta dice quién rechazó.")
    @ApiResponse(responseCode = "201", description = "A2 y A3 aceptaron: jugada e impacto guardados")
    @ApiResponse(responseCode = "422", description = "Rechazo de A2 o de A3 (RECHAZO_DE_A2 / RECHAZO_DE_A3), "
            + "o regla propia: el tirador no tiene francotirador (DISPARO_NO_PERMITIDO)",
            content = @Content(schema = @Schema(implementation = RechazoRespuesta.class), examples = @ExampleObject(value = """
                    {"status":422,"codigo":"RECHAZO_DE_A3","excepcion":"RechazoDeCompaneroException",
                     "mensaje":"La jugada la rechazó A3 (Marcelo Kropff (Pomona06)) con HTTP 409",
                     "rechazadoPor":"A3","companero":"Marcelo Kropff (Pomona06)","statusDelCompanero":409,
                     "respuestaDelCompanero":{"error":"El jugador ya está eliminado y no puede recibir más daño","excepcion":"JugadorEliminadoException"}}""")))
    @ApiResponse(responseCode = "503", description = "A2 o A3 no respondió (A2_NO_DISPONIBLE / A3_NO_DISPONIBLE)",
            content = @Content(schema = @Schema(implementation = RechazoRespuesta.class)))
    @ApiResponse(responseCode = "404", description = "Tirador u objetivo inexistente (RECURSO_NO_ENCONTRADO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "400", description = "Faltan ids o la distancia es negativa (VALOR_ILEGAL)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public JugadaCruzada jugar(@RequestBody DisparoRapido disparo) {
        return escenario.jugar(disparo.tiradorId(), disparo.objetivoId(), disparo.distancia(), disparo.aguanta());
    }

    @GetMapping
    @Operation(summary = "Jugadas guardadas", description = "Solo aparecen las que aceptaron A2 y A3")
    public List<JugadaCruzada> listar() {
        return escenario.listar();
    }
}
