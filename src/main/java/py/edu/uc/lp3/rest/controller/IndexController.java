package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;

/**
 * Portero del servicio: solo confirma que la API está viva y quién la hizo.
 * No construye armas.
 */
@RestController
public class IndexController {

    public record EstadoServicio(String servicio, String autor, String dominio,
                                 String estado, List<String> endpoints) {
    }

    @GetMapping(ApiPaths.INDEX)
    public EstadoServicio index() {
        return new EstadoServicio(
                "jb-taller-git-2026",
                "Joaquín Benegas (joaquinBenegas0)",
                "Counter-Strike 2",
                "activo",
                List.of(
                        "GET /",
                        "GET /api/armas/francotirador?nombre=AWP&precio=4750&distancia=300",
                        "GET /api/armas/francotirador/personalizado?nombre=Scout&precio=1700&equipo=TERRORISTAS&danio=74&precision=0.9&cargador=10&reserva=90&recarga=2.9&zoom=2&penetracion=0.3",
                        "GET /api/armas/inventario"));
    }
}
