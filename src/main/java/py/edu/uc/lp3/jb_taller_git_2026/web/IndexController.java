package py.edu.uc.lp3.jb_taller_git_2026.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Portero del servicio: solo confirma que la API está viva y quién la hizo.
 * No construye armas.
 */
@RestController
public class IndexController {

    public record EstadoServicio(String servicio, String autor, String dominio,
                                 String estado, List<String> endpoints) {
    }

    @GetMapping("/")
    public EstadoServicio index() {
        return new EstadoServicio(
                "jb-taller-git-2026",
                "Joaquín Benegas (joaquinBenegas0)",
                "Counter-Strike 2",
                "activo",
                List.of(
                        "GET /",
                        "GET /api/armas/francotirador?nombre=AWP&precio=4750",
                        "GET /api/armas/inventario"));
    }
}
