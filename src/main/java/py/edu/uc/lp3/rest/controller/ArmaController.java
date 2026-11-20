package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Equipo;
import py.edu.uc.lp3.rest.dto.CambioDeArma;
import py.edu.uc.lp3.rest.dto.NuevaArma;
import py.edu.uc.lp3.service.ArmaService;

/**
 * CRUD REST de la familia Arma. Solo traduce HTTP: recibe el pedido, llama a ArmaService
 * (que valida, aplica las reglas y maneja la transacción) y devuelve la respuesta.
 */
@RestController
@RequestMapping(ApiPaths.ARMAS)
public class ArmaController {

    private final ArmaService armas;

    public ArmaController(ArmaService armas) {
        this.armas = armas;
    }

    /** Con ?equipo= o ?precioMaximo= usa las consultas del repositorio. */
    @GetMapping
    public List<Arma> listar(@RequestParam(value = "equipo", required = false) Equipo equipo,
                             @RequestParam(value = "precioMaximo", required = false) Integer precioMaximo) {
        return armas.listar(equipo, precioMaximo);
    }

    @GetMapping(ApiPaths.POR_ID)
    public Arma buscar(@PathVariable("id") Long id) {
        return armas.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Arma crear(@RequestBody NuevaArma pedido) {
        return armas.crear(pedido.crear());
    }

    @PatchMapping(ApiPaths.POR_ID)
    public Arma actualizar(@PathVariable("id") Long id, @RequestBody CambioDeArma cambio) {
        return armas.actualizar(id, cambio.nombre(), cambio.precio());
    }

    @DeleteMapping(ApiPaths.POR_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void borrar(@PathVariable("id") Long id) {
        armas.retirar(id);
    }
}
