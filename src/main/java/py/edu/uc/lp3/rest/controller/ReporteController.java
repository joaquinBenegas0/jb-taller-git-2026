package py.edu.uc.lp3.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Reportable;
import py.edu.uc.lp3.repository.ArmaRepository;
import py.edu.uc.lp3.repository.JugadorRepository;

/**
 * Usa el protocolo Reportable: junta armas y jugadores (dos familias distintas)
 * en una sola lista y les manda el mismo mensaje, sin preguntar qué son.
 */
@RestController
public class ReporteController {

    public record LineaDeReporte(String quien, String reporte) {
    }

    private final ArmaRepository armas;
    private final JugadorRepository jugadores;

    public ReporteController(ArmaRepository armas, JugadorRepository jugadores) {
        this.armas = armas;
        this.jugadores = jugadores;
    }

    @GetMapping(ApiPaths.REPORTES)
    public List<LineaDeReporte> reportar() {
        List<Reportable> reportables = new ArrayList<>();
        reportables.addAll(armas.findAll());
        reportables.addAll(jugadores.findAll());
        return reportables.stream()
                .map(r -> new LineaDeReporte(r.identificacion(), r.reporteDeCombate()))
                .toList();
    }
}
