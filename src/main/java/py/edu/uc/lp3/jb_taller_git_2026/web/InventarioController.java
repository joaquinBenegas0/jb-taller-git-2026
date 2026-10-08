package py.edu.uc.lp3.jb_taller_git_2026.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.jb_taller_git_2026.cs2.Arma;
import py.edu.uc.lp3.jb_taller_git_2026.cs2.Equipo;
import py.edu.uc.lp3.jb_taller_git_2026.cs2.Escopeta;
import py.edu.uc.lp3.jb_taller_git_2026.cs2.Francotirador;
import py.edu.uc.lp3.jb_taller_git_2026.cs2.Granada;
import py.edu.uc.lp3.jb_taller_git_2026.cs2.Pistola;
import py.edu.uc.lp3.jb_taller_git_2026.cs2.RifleAsalto;
import py.edu.uc.lp3.jb_taller_git_2026.cs2.Subfusil;

/**
 * Inventario: guarda cualquier arma como el tipo padre Arma y le pide a cada una que se use.
 * No hay ningún if por tipo: cada objeto informa su propio comportamiento.
 */
@RestController
public class InventarioController {

    public record UsoDeArma(String arma, String resultado) {
    }

    @GetMapping("/api/armas/inventario")
    public List<UsoDeArma> usarInventario() {
        List<Arma> inventario = List.of(
                new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS, 36, 0.73f, 30, 90, 2.5f, 10f),
                new Francotirador("AWP", 4750, Equipo.ANTITERRORISTAS, 115, 0.95f, 5, 30, 3.6f, 4f, 0.5f),
                new Pistola("Glock-18", 200, Equipo.TERRORISTAS, 30, 0.6f, 20, 120, 2.2f),
                new Escopeta("Nova", 1050, Equipo.ANTITERRORISTAS, 26, 0.4f, 8, 32, 0.5f, 9, 10f),
                new Subfusil("MP9", 1250, Equipo.ANTITERRORISTAS, 26, 0.65f, 30, 120, 2.1f, 0.7f),
                new Granada("HE", 300, Equipo.TERRORISTAS, 98, 4f, 1.5f, true, "onda expansiva"),
                new Granada("Flashbang", 200, Equipo.ANTITERRORISTAS, 0, 6f, 1.5f, false, "ceguera temporal"));

        return inventario.stream()
                .map(arma -> new UsoDeArma(arma.mostrarEnTienda(), arma.usar()))
                .toList();
    }
}
