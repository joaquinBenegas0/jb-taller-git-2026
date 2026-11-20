package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.service.ReporteService;

/** Usa la interfaz Reportable: armas y jugadores reciben el mismo mensaje. */
@RestController
public class ReporteController {

    public record LineaDeReporte(String quien, String reporte) {
    }

    private final ReporteService reportes;

    public ReporteController(ReporteService reportes) {
        this.reportes = reportes;
    }

    @GetMapping(ApiPaths.REPORTES)
    public List<LineaDeReporte> reportar() {
        return reportes.reportables().stream()
                .map(r -> new LineaDeReporte(r.identificacion(), r.reporteDeCombate()))
                .toList();
    }
}
