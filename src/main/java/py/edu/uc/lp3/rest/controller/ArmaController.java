package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;

import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Francotirador;

/**
 * Construye un arma del dominio con los parámetros de la URL.
 * El controller no valida ni corrige valores: eso lo hace el constructor de la clase.
 */
@RestController
@RequestMapping(ApiPaths.ARMAS)
public class ArmaController {

    public record FrancotiradorCreado(String nombre, int precio, Equipo equipo, float precision,
                                      float zoom, int municionCargador, int municionReserva) {
    }

    @GetMapping(ApiPaths.FRANCOTIRADOR)
    public FrancotiradorCreado construirFrancotirador(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") int precio,
            @RequestParam(value = "equipo", defaultValue = "ANTITERRORISTAS") Equipo equipo,
            @RequestParam(value = "danio", defaultValue = "115") int danio,
            @RequestParam(value = "precision", defaultValue = "0.95") float precision,
            @RequestParam(value = "cargador", defaultValue = "5") int cargador,
            @RequestParam(value = "reserva", defaultValue = "30") int reserva,
            @RequestParam(value = "recarga", defaultValue = "3.6") float recarga,
            @RequestParam(value = "zoom", defaultValue = "4") float zoom,
            @RequestParam(value = "penetracion", defaultValue = "0.5") float penetracion) {

        Francotirador arma = new Francotirador(nombre, precio, equipo, danio, precision,
                cargador, reserva, recarga, zoom, penetracion);

        return new FrancotiradorCreado(arma.obtenerNombre(), arma.obtenerPrecio(), arma.obtenerEquipo(),
                arma.obtenerPrecision(), arma.obtenerZoom(),
                arma.obtenerMunicionCargador(), arma.obtenerMunicionReserva());
    }

    /** Si la clase rechaza los valores, se informa como 400 Bad Request. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> valoresRechazados(IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }
}
