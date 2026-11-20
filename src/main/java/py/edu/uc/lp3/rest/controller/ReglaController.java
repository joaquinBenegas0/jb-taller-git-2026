package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.rest.controller.ManejadorDeExcepciones.ErrorRespuesta;
import py.edu.uc.lp3.rest.dto.ConsultaDeDanio;
import py.edu.uc.lp3.rest.dto.ConsultaDeMunicion;
import py.edu.uc.lp3.service.ReglaDeDanio;
import py.edu.uc.lp3.service.ReglaDeMunicion;

/**
 * Las dos reglas del juego publicadas para otros servicios (POO-09).
 * Solo traduce HTTP: ReglaDeMunicion y ReglaDeDanio (los mismos services que usa la simulación)
 * aplican la regla sin tocar la base y responden 200 si la jugada es legal o el error de la regla si no.
 */
@RestController
@RequestMapping(ApiPaths.REGLAS)
@Tag(name = "Reglas", description = "Validaciones sin estado que pueden consultar otros servicios. "
        + "200 = la regla acepta; 409 / 422 = la regla rechaza; 400 = el pedido está mal armado.")
public class ReglaController {

    private final ReglaDeMunicion reglaDeMunicion;
    private final ReglaDeDanio reglaDeDanio;

    public ReglaController(ReglaDeMunicion reglaDeMunicion, ReglaDeDanio reglaDeDanio) {
        this.reglaDeMunicion = reglaDeMunicion;
        this.reglaDeDanio = reglaDeDanio;
    }

    /** Lo que responde una regla cuando acepta. */
    public record Aceptada<T>(String regla, boolean permitido, T resultado) {
    }

    @PostMapping(ApiPaths.REGLA_MUNICION)
    @Operation(summary = "Regla 1 · munición",
            description = "Un disparo gasta las balas de su modo (SEMI 1, RAFAGA 3, AUTOMATICO 5), sin pasarse de las "
                    + "que quedan. Con el cargador vacío no se dispara.")
    @ApiResponse(responseCode = "200", description = "Hay balas: el disparo es legal",
            content = @Content(examples = @ExampleObject(value = """
                    {"regla":"MUNICION","permitido":true,"resultado":{"balasGastadas":5,"municionRestante":25}}""")))
    @ApiResponse(responseCode = "409", description = "Cargador vacío (MUNICION_AGOTADA)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class), examples = @ExampleObject(value = """
                    {"status":409,"codigo":"MUNICION_AGOTADA","excepcion":"MunicionAgotadaException",
                     "mensaje":"AK-47 tiene el cargador vacío (reserva: 90): hay que recargar antes de disparar"}""")))
    @ApiResponse(responseCode = "400", description = "Falta municionCargador, es negativa o el modo no existe (VALOR_ILEGAL / JSON_INVALIDO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public Aceptada<ReglaDeMunicion.Resultado> municion(@RequestBody ConsultaDeMunicion consulta) {
        ReglaDeMunicion.Resultado resultado = reglaDeMunicion.consultar(consulta.arma(),
                consulta.municionCargador(), consulta.municionReserva(), consulta.modo());
        return new Aceptada<>("MUNICION", true, resultado);
    }

    @PostMapping(ApiPaths.REGLA_DANIO)
    @Operation(summary = "Regla 2 · daño con blindaje",
            description = "Solo se le dispara a un enemigo vivo (otro equipo, vida mayor a 0). El blindaje absorbe "
                    + "hasta la mitad del daño y el resto baja la vida, que nunca queda por debajo de 0. Con vida 0 "
                    + "el objetivo queda eliminado.")
    @ApiResponse(responseCode = "200", description = "Objetivo válido: devuelve cómo queda",
            content = @Content(examples = @ExampleObject(value = """
                    {"regla":"DANIO","permitido":true,"resultado":{"absorbidoPorBlindaje":90,"danioALaVida":90,
                     "vidaFinal":10,"blindajeFinal":10,"eliminado":false}}""")))
    @ApiResponse(responseCode = "422", description = "Fuego amigo u objetivo ya eliminado (DISPARO_NO_PERMITIDO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class), examples = @ExampleObject(value = """
                    {"status":422,"codigo":"DISPARO_NO_PERMITIDO","excepcion":"DisparoNoPermitidoException",
                     "mensaje":"Fuego amigo: Sombra y Cuervo son del mismo equipo (TERRORISTAS)"}""")))
    @ApiResponse(responseCode = "400", description = "Faltan campos, equipo desconocido o vida/blindaje fuera de 0 a 100",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public Aceptada<ReglaDeDanio.Resultado> danio(@RequestBody ConsultaDeDanio consulta) {
        ReglaDeDanio.Resultado resultado = reglaDeDanio.consultar(consulta.tirador(), consulta.equipoTirador(),
                consulta.objetivo(), consulta.equipoObjetivo(), consulta.vidaObjetivo(), consulta.blindajeObjetivo(),
                consulta.danio());
        return new Aceptada<>("DANIO", true, resultado);
    }
}
