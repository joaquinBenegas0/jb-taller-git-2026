package py.edu.uc.lp3.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import py.edu.uc.lp3exceptions.CompaneroNoDisponibleException;
import py.edu.uc.lp3exceptions.Cs2Exception;
import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;
import py.edu.uc.lp3exceptions.MunicionAgotadaException;
import py.edu.uc.lp3exceptions.RechazoDeCompaneroException;
import py.edu.uc.lp3exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/**
 * Traduce excepciones a HTTP: un handler por tipo, cada uno con su código de estado.
 * Las reglas no se deciden acá; acá solo se elige cómo contarle el error al cliente.
 */
@RestControllerAdvice
public class ManejadorDeExcepciones {

    private final ObjectMapper json;

    public ManejadorDeExcepciones(ObjectMapper json) {
        this.json = json;
    }

    /** Cuerpo JSON de todo error: el cliente distingue los tipos por "status", "codigo" y "excepcion". */
    public record ErrorRespuesta(int status, String codigo, String excepcion, String mensaje) {
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorRespuesta recursoNoEncontrado(RecursoNoEncontradoException e) {
        return deDominio(HttpStatus.NOT_FOUND, e);
    }

    /** Regla 1: el arma existe pero su estado (cargador vacío) choca con el pedido. */
    @ExceptionHandler(MunicionAgotadaException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorRespuesta municionAgotada(MunicionAgotadaException e) {
        return deDominio(HttpStatus.CONFLICT, e);
    }

    /** Regla 2: el JSON es válido pero el juego no permite ese disparo. */
    @ExceptionHandler(DisparoNoPermitidoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorRespuesta disparoNoPermitido(DisparoNoPermitidoException e) {
        return deDominio(HttpStatus.UNPROCESSABLE_ENTITY, e);
    }

    /**
     * Error del escenario cruzado: además de los campos de siempre dice qué compañero (A2 o A3)
     * rechazó, con qué código y qué respondió su servicio, tal cual.
     */
    public record RechazoRespuesta(int status, String codigo, String excepcion, String mensaje,
                                   String rechazadoPor, String companero, int statusDelCompanero,
                                   JsonNode respuestaDelCompanero) {
    }

    /** Una regla ajena rechazó la jugada: no se guardó nada en H2. */
    @ExceptionHandler(RechazoDeCompaneroException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public RechazoRespuesta rechazoDeCompanero(RechazoDeCompaneroException e) {
        return new RechazoRespuesta(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.codigo(),
                e.getClass().getSimpleName(), e.getMessage(), e.rol(), e.companero(),
                e.statusDelCompanero(), comoJson(e.respuestaDelCompanero()));
    }

    /** No se pudo hablar con un compañero: tampoco se guarda nada, pero no es un rechazo de su regla. */
    @ExceptionHandler(CompaneroNoDisponibleException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public RechazoRespuesta companeroNoDisponible(CompaneroNoDisponibleException e) {
        return new RechazoRespuesta(HttpStatus.SERVICE_UNAVAILABLE.value(), e.codigo(),
                e.getClass().getSimpleName(), e.getMessage(), e.rol(), e.companero(), 0, null);
    }

    /** Un service rechazó un dato que rompe un invariante (precio 0, blindaje 150...): no se guardó nada. */
    @ExceptionHandler(ValorIlegalException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta valorIlegalDelJuego(ValorIlegalException e) {
        return deDominio(HttpStatus.BAD_REQUEST, e);
    }

    /** Falta un campo obligatorio en el JSON (lo detectan los records de rest.dto). */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta valorIlegal(IllegalArgumentException e) {
        return new ErrorRespuesta(HttpStatus.BAD_REQUEST.value(), "VALOR_ILEGAL",
                e.getClass().getSimpleName(), e.getMessage());
    }

    /** JSON mal formado, "tipo" desconocido o un equipo que no existe. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta jsonIlegible(HttpMessageNotReadableException e) {
        return new ErrorRespuesta(HttpStatus.BAD_REQUEST.value(), "JSON_INVALIDO",
                e.getClass().getSimpleName(),
                "El cuerpo no se pudo leer: revisá el JSON, el campo \"tipo\" y los valores de \"equipo\"");
    }

    /** La respuesta del compañero como JSON; si no era JSON, como texto. */
    private JsonNode comoJson(String cuerpo) {
        if (cuerpo == null || cuerpo.isBlank()) {
            return null;
        }
        try {
            return json.readTree(cuerpo);
        } catch (Exception e) {
            return TextNode.valueOf(cuerpo);
        }
    }

    private static ErrorRespuesta deDominio(HttpStatus status, Cs2Exception e) {
        return new ErrorRespuesta(status.value(), e.codigo(), e.getClass().getSimpleName(), e.getMessage());
    }
}
