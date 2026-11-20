package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.rest.dto.CambioDeArma;
import py.edu.uc.lp3.rest.dto.NuevaArma;
import py.edu.uc.lp3.service.ArmaService;

/**
 * CRUD REST de la familia Arma. Solo traduce HTTP: recibe el pedido, llama a ArmaService
 * (que valida, aplica las reglas y maneja la transacción) y devuelve la respuesta.
 */
@RestController
@RequestMapping(ApiPaths.ARMAS)
@Tag(name = "Armas", description = "CRUD de la familia Arma; el subtipo viaja en la propiedad tipo")
public class ArmaController {

    private final ArmaService armas;

    public ArmaController(ArmaService armas) {
        this.armas = armas;
    }

    /** Con ?equipo= o ?precioMaximo= usa las consultas del repositorio. */
    @GetMapping
    @Operation(summary = "Listar armas", description = "Con ?equipo= o ?precioMaximo= filtra")
    public List<Arma> listar(@RequestParam(value = "equipo", required = false) Equipo equipo,
                             @RequestParam(value = "precioMaximo", required = false) Integer precioMaximo) {
        return armas.listar(equipo, precioMaximo);
    }

    @GetMapping(ApiPaths.POR_ID)
    @Operation(summary = "Ver un arma")
    @ApiResponse(responseCode = "404", description = "Id inexistente (RECURSO_NO_ENCONTRADO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public Arma buscar(@PathVariable("id") Long id) {
        return armas.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un arma", description = "tipo: pistola, rifle, francotirador, escopeta, subfusil o granada")
    @ApiResponse(responseCode = "400", description = "Invariante roto, falta un campo o tipo desconocido (VALOR_ILEGAL / JSON_INVALIDO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public Arma crear(@RequestBody NuevaArma pedido) {
        return armas.crear(pedido.crear());
    }

    @PatchMapping(ApiPaths.POR_ID)
    @Operation(summary = "Cambiar nombre y/o precio")
    @ApiResponse(responseCode = "400", description = "Nombre en blanco o precio no positivo (VALOR_ILEGAL)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    @ApiResponse(responseCode = "404", description = "Id inexistente (RECURSO_NO_ENCONTRADO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public Arma actualizar(@PathVariable("id") Long id, @RequestBody CambioDeArma cambio) {
        return armas.actualizar(id, cambio.nombre(), cambio.precio());
    }

    @DeleteMapping(ApiPaths.POR_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Retirar un arma", description = "Quien la tenía equipada la suelta")
    @ApiResponse(responseCode = "404", description = "Id inexistente (RECURSO_NO_ENCONTRADO)",
            content = @Content(schema = @Schema(implementation = ErrorRespuesta.class)))
    public void borrar(@PathVariable("id") Long id) {
        armas.retirar(id);
    }
}
