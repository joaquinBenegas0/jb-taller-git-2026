package py.edu.uc.lp3.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import py.edu.uc.lp3exceptions.Cs2Exception;
import py.edu.uc.lp3exceptions.DisparoNoPermitidoException;
import py.edu.uc.lp3exceptions.MunicionAgotadaException;
import py.edu.uc.lp3exceptions.RecursoNoEncontradoException;
import py.edu.uc.lp3exceptions.ValorIlegalException;

/**
 * Traduce excepciones a HTTP: un handler por tipo, cada uno con su código de estado.
 * Las reglas no se deciden acá; acá solo se elige cómo contarle el error al cliente.
 */
@RestControllerAdvice
public class ManejadorDeExcepciones {

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

    private static ErrorRespuesta deDominio(HttpStatus status, Cs2Exception e) {
        return new ErrorRespuesta(status.value(), e.codigo(), e.getClass().getSimpleName(), e.getMessage());
    }
}
