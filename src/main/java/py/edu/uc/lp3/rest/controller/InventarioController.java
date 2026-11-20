package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.service.ArmaService;

/** Recorre una List<Arma> y le manda usar() a cada una, sin ningún if por tipo. */
@RestController
public class InventarioController {

    public record UsoDeArma(String arma, String resultado) {
    }

    private final ArmaService armas;

    public InventarioController(ArmaService armas) {
        this.armas = armas;
    }

    @GetMapping(ApiPaths.ARMAS + ApiPaths.INVENTARIO)
    public List<UsoDeArma> usarInventario() {
        return armas.inventarioDeEjemplo().stream()
                .map(arma -> new UsoDeArma(arma.mostrarEnTienda(), arma.usar()))
                .toList();
    }
}
