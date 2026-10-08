package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.domain.Escopeta;
import py.edu.uc.lp3.domain.Francotirador;
import py.edu.uc.lp3.domain.Granada;
import py.edu.uc.lp3.domain.Pistola;
import py.edu.uc.lp3.domain.RifleAsalto;
import py.edu.uc.lp3.domain.Subfusil;

/**
 * Inventario: guarda cualquier arma como el tipo padre Arma y le pide a cada una que se use.
 * No hay ningún if por tipo: cada objeto informa su propio comportamiento.
 */
@RestController
public class InventarioController {

    public record UsoDeArma(String arma, String resultado) {
    }

    @GetMapping(ApiPaths.ARMAS + ApiPaths.INVENTARIO)
    public List<UsoDeArma> usarInventario() {
        // Se mezclan constructores simples, sobrecargados y completos: todos dejan el arma en estado legal.
        List<Arma> inventario = List.of(
                new RifleAsalto("AK-47", 2700, Equipo.TERRORISTAS),
                new Francotirador("AWP", 4750),
                new Pistola("Glock-18", 200, Equipo.TERRORISTAS),
                new Escopeta("Nova", 1050, Equipo.ANTITERRORISTAS, 26, 0.4f, 8, 32, 0.5f, 9, 10f),
                new Subfusil("MP9", 1250, Equipo.ANTITERRORISTAS, 26, 0.65f, 30, 120, 2.1f, 0.7f),
                new Granada("HE", 300, Equipo.TERRORISTAS, 98),
                new Granada("Flashbang", 200, Equipo.ANTITERRORISTAS, "ceguera temporal"));

        return inventario.stream()
                .map(arma -> new UsoDeArma(arma.mostrarEnTienda(), arma.usar()))
                .toList();
    }
}
