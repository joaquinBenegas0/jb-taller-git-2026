package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.rest.controller.ManejadorDeExcepciones.ErrorRespuesta;
import py.edu.uc.lp3.domain.Impacto;
import py.edu.uc.lp3.rest.dto.DisparoSimulado;
import py.edu.uc.lp3.service.SimulacionService;

/**
 * Recurso de la simulación: /api/simulaciones/disparos.
 * Solo traduce HTTP: SimulacionService busca a los jugadores, aplica las dos reglas y guarda
 * el resultado en una transacción. Si una regla falla, la base queda igual.
 */
@RestController
@RequestMapping(ApiPaths.DISPAROS)
@Tag(name = "Simulación", description = "Disparos entre jugadores guardados: aplica las dos reglas y persiste el Impacto")
public class SimulacionController {

    private final SimulacionService simulaciones;

    public SimulacionController(SimulacionService simulaciones) {
        this.simulaciones = simulaciones;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Simular un disparo",
            description = "El tirador dispara su arma equipada al objetivo a la distancia indicada (metros). "
                    + "Aplica la regla de munición y la de daño con blindaje; si alguna falla, la base queda igual.")
    @ApiResponse(responseCode = "201", description = "Impacto guardado; el arma y los dos jugadores quedan actualizados")
    @ApiResponse(responseCode = "400", description = "Faltan ids o la distancia es negativa (VALOR_ILEGAL)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "404", description = "Tirador u objetivo inexistente (RECURSO_NO_ENCONTRADO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "409", description = "Regla 1: cargador vacío (MUNICION_AGOTADA)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "422", description = "Regla 2: fuego amigo, objetivo o tirador eliminado, sin arma o con granada (DISPARO_NO_PERMITIDO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public Impacto simular(@RequestBody DisparoSimulado disparo) {
        return simulaciones.simular(disparo.tiradorId(), disparo.objetivoId(), disparo.distancia());
    }

    /** Historial persistido; con ?tiradorId= filtra por tirador. */
    @GetMapping
    @Operation(summary = "Historial de disparos", description = "Con ?tiradorId= filtra por tirador")
    public List<Impacto> historial(@RequestParam(value = "tiradorId", required = false) Long tiradorId) {
        return simulaciones.historial(tiradorId);
    }
}
