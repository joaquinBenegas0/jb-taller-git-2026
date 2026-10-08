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
 * Construye un francotirador con los parámetros de la URL.
 * Los valores van directo a un constructor; el controller no valida ni corrige nada:
 * si un valor rompe una regla, la clase lo rechaza y aquí solo se informa.
 */
@RestController
@RequestMapping(ApiPaths.ARMAS)
public class ArmaController {

    public record FrancotiradorCreado(String arma, float precision, float zoom,
                                      int municionCargador, int municionReserva,
                                      String disparo, String disparoConDistancia) {
    }

    /** Usa el constructor sobrecargado (nombre, precio, equipo) y prueba las dos versiones de disparar. */
    @GetMapping(ApiPaths.FRANCOTIRADOR)
    public FrancotiradorCreado construirFrancotirador(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") int precio,
            @RequestParam(value = "equipo", defaultValue = "ANTITERRORISTAS") Equipo equipo,
            @RequestParam(value = "distancia", defaultValue = "300") float distancia) {

        Francotirador arma = new Francotirador(nombre, precio, equipo);
        return probar(arma, distancia);
    }

    /** Usa el constructor completo: todos los valores llegan desde la URL. */
    @GetMapping(ApiPaths.FRANCOTIRADOR_PERSONALIZADO)
    public FrancotiradorCreado construirFrancotiradorPersonalizado(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") int precio,
            @RequestParam("equipo") Equipo equipo,
            @RequestParam("danio") int danio,
            @RequestParam("precision") float precision,
            @RequestParam("cargador") int cargador,
            @RequestParam("reserva") int reserva,
            @RequestParam("recarga") float recarga,
            @RequestParam("zoom") float zoom,
            @RequestParam("penetracion") float penetracion,
            @RequestParam(value = "distancia", defaultValue = "300") float distancia) {

        Francotirador arma = new Francotirador(nombre, precio, equipo, danio, precision,
                cargador, reserva, recarga, zoom, penetracion);
        return probar(arma, distancia);
    }

    private FrancotiradorCreado probar(Francotirador arma, float distancia) {
        float precision = arma.obtenerPrecision();
        float zoom = arma.obtenerZoom();
        String disparo = arma.disparar();
        String disparoConDistancia = arma.disparar(distancia);
        return new FrancotiradorCreado(arma.mostrarEnTienda(), precision, zoom,
                arma.obtenerMunicionCargador(), arma.obtenerMunicionReserva(),
                disparo, disparoConDistancia);
    }

    /** Si la clase rechaza los valores, se informa como 400 Bad Request. */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> valoresRechazados(IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }
}
